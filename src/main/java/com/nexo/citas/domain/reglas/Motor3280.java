package com.nexo.citas.domain.reglas;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nexo.citas.domain.PerfilPaciente;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Motor de reglas 3280 (Core Domain service).
 *
 * Evalua el perfil del paciente contra el conjunto de reglas data-driven
 * habilitadas y produce un pipeline ordenado de citas preventivas sugeridas.
 * Es puro dominio: depende de {@link ReglaProvider} (interfaz) y de un
 * {@link CondicionEvaluador}, sin conocer JPA/Spring.
 */
public class Motor3280 {

    private final ReglaProvider reglaProvider;
    private final CondicionEvaluador evaluador;
    private final ObjectMapper objectMapper;

    public Motor3280(ReglaProvider reglaProvider, CondicionEvaluador evaluador, ObjectMapper objectMapper) {
        this.reglaProvider = reglaProvider;
        this.evaluador = evaluador;
        this.objectMapper = objectMapper;
    }

    /**
     * Sugiere citas preventivas para el perfil dado una especialidad origen.
     * Resultado ordenado por prioridad (menor = mayor prioridad).
     */
    public List<Sugerencia> sugerir(Long especialidadTipoId, PerfilPaciente perfil) {
        List<Regla3280> reglas =
                especialidadTipoId != null
                        ? reglaProvider.listarPorEspecialidad(especialidadTipoId)
                        : reglaProvider.listarHabilitadas();

        List<Sugerencia> resultado = new ArrayList<>();
        for (Regla3280 regla : reglas) {
            if (!regla.isHabilitada()) {
                continue;
            }
            try {
                JsonNode condicion = objectMapper.readTree(regla.getCondicionJson());
                if (evaluador.evalua(condicion, perfil)) {
                    resultado.add(new Sugerencia(
                            regla.getTipoCitaSugeridaId(),
                            regla.getTipoCitaSugeridaNombre(),
                            regla.getDiasVentana(),
                            regla.getPrioridad()));
                }
            } catch (JsonProcessingException e) {
                // Regla mal formada: se ignora y se registra para auditoria.
                throw new IllegalStateException("Regla 3280 " + regla.getId() + " tiene condicion JSON invalida", e);
            }
        }
        resultado.sort(Comparator.comparingInt(Sugerencia::getPrioridad));
        return resultado;
    }
}

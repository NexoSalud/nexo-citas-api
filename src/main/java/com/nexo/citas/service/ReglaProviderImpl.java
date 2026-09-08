package com.nexo.citas.service;

import com.nexo.citas.domain.reglas.Regla3280;
import com.nexo.citas.domain.reglas.ReglaProvider;
import com.nexo.citas.entity.Regla3280Entity;
import com.nexo.citas.repository.Regla3280Repository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Adaptador de persistenia que implementa ReglaProvider (interfaz de dominio).
 * El dominio depende de la interfaz; la infraestructura lo satisface.
 */
@Component
public class ReglaProviderImpl implements ReglaProvider {

    private final Regla3280Repository repository;

    public ReglaProviderImpl(Regla3280Repository repository) {
        this.repository = repository;
    }

    @Override
    public List<Regla3280> listarHabilitadas() {
        return repository.findByHabilitadaTrueOrderByPrioridadAsc()
                .stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Regla3280> listarPorEspecialidad(Long especialidadTipoId) {
        return repository.findByHabilitadaTrueAndEspecialidadOrigenIdOrderByPrioridadAsc(especialidadTipoId)
                .stream().map(this::toDomain).collect(Collectors.toList());
    }

    private Regla3280 toDomain(Regla3280Entity e) {
        return new Regla3280(
                e.getId(),
                e.getEspecialidadOrigenId(),
                e.getCondicion(),
                e.getTipoCitaSugeridaId(),
                e.getTipoCitaSugeridaNombre(),
                e.getDiasVentana(),
                e.getPrioridad() != null ? e.getPrioridad() : 0,
                Boolean.TRUE.equals(e.getHabilitada()));
    }
}

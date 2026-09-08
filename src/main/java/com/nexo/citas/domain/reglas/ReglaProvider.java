package com.nexo.citas.domain.reglas;

import java.util.List;

/**
 * Proveedor de reglas 3280 habilitadas, dependencia invertida desde el dominio
 * hacia la infraestructura. Implementado por el adaptador de persistencia.
 */
public interface ReglaProvider {

    List<Regla3280> listarHabilitadas();

    List<Regla3280> listarPorEspecialidad(Long especialidadTipoId);
}

package com.nexo.citas.domain;

import java.util.EnumSet;
import java.util.Set;

/**
 * Maquina de estados de la cita (Core Domain).
 *
 * Ciclo de vida exacto requerido:
 *   Creada -> Facturada -> Completada -> No Asistio
 *   Creada -> Cancelada por el Usuario
 *   Creada/Facturada -> Reagendada por el Sistema -> Cancelada
 *
 * Las transiciones validas se modelan explicitamente para que el dominio
 * no pueda llegar a estados invalidos (invariantes protegidas).
 */
public enum EstadoCita {

    CREADA,
    FACTURADA,
    COMPLETADA,
    APLICADA,
    NO_ASISTIO,
    CANCELADA_USUARIO,
    REAGENDADA_SISTEMA,
    CANCELADA;

    /**
     * Transiciones permitidas por el Core Domain.
     */
    public static final Set<EstadoCita> TRANSICIONES_VALIDAS_FROM_CREADA = EnumSet.of(
            FACTURADA, APLICADA, CANCELADA_USUARIO, REAGENDADA_SISTEMA);

    public static final Set<EstadoCita> TRANSICIONES_VALIDAS_FROM_FACTURADA = EnumSet.of(
            COMPLETADA, APLICADA, NO_ASISTIO, REAGENDADA_SISTEMA);

    public static final Set<EstadoCita> TRANSICIONES_VALIDAS_FROM_REAGENDADA_SISTEMA = EnumSet.of(
            CANCELADA, FACTURADA);

    public static final Set<EstadoCita> TRANSICIONES_VALIDAS_FROM_COMPLETADA = EnumSet.of(
            APLICADA);

    public static boolean puedeTransicionar(EstadoCita actual, EstadoCita nuevo) {
        if (actual == null) {
            return false;
        }
        switch (actual) {
            case CREADA:
                return TRANSICIONES_VALIDAS_FROM_CREADA.contains(nuevo);
            case FACTURADA:
                return TRANSICIONES_VALIDAS_FROM_FACTURADA.contains(nuevo);
            case REAGENDADA_SISTEMA:
                return TRANSICIONES_VALIDAS_FROM_REAGENDADA_SISTEMA.contains(nuevo);
            case COMPLETADA:
                return TRANSICIONES_VALIDAS_FROM_COMPLETADA.contains(nuevo);
            default:
                // Estados terminales (APLICADA, NO_ASISTIO, CANCELADA_USUARIO, CANCELADA)
                return false;
        }
    }
}

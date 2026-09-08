package com.nexo.citas.domain;

import java.util.List;
import java.util.Map;

/**
 * Value Object: Perfil del paciente evaluado por el motor de reglas 3280.
 *
 * Es un snapshot materializado (materializado desde Admin Core vía eventos)
 * para que la evaluacion de reglas preventivas sea rapida y offline.
 * Los campos extra/riesgos se representan de forma generica para permitir
 * que las reglas data-driven (condiciones JSONB) evalúen atributos nuevos
 * sin cambiar el codigo del dominio.
 */
public class PerfilPaciente {

    private final Integer edad;            // años
    private final String etnia;
    private final String genero;
    private final Integer numHijos;
    private final String ubicacion;        // departamento/municipio
    private final List<String> afecciones; // condiciones previas
    private final List<String> riesgos;    // factores de riesgo
    private final Map<String, Object> extra; // atributos dinamicos (JSONB)

    public PerfilPaciente(Integer edad, String etnia, String genero, Integer numHijos,
                          String ubicacion, List<String> afecciones, List<String> riesgos,
                          Map<String, Object> extra) {
        this.edad = edad;
        this.etnia = etnia;
        this.genero = genero;
        this.numHijos = numHijos;
        this.ubicacion = ubicacion;
        this.afecciones = afecciones != null ? afecciones : List.of();
        this.riesgos = riesgos != null ? riesgos : List.of();
        this.extra = extra != null ? extra : Map.of();
    }

    public Integer getEdad() {
        return edad;
    }

    public String getEtnia() {
        return etnia;
    }

    public String getGenero() {
        return genero;
    }

    public Integer getNumHijos() {
        return numHijos;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public List<String> getAfecciones() {
        return afecciones;
    }

    public List<String> getRiesgos() {
        return riesgos;
    }

    public Map<String, Object> getExtra() {
        return extra;
    }

    public boolean tieneRiesgo(String riesgo) {
        return riesgos.stream().anyMatch(r -> r.equalsIgnoreCase(riesgo));
    }

    public boolean tieneAfeccion(String afeccion) {
        return afecciones.stream().anyMatch(a -> a.equalsIgnoreCase(afeccion));
    }
}

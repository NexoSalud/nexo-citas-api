package com.nexo.citas.domain.reglas;

import com.fasterxml.jackson.databind.JsonNode;
import com.nexo.citas.domain.PerfilPaciente;

import java.util.ArrayList;
import java.util.List;

/**
 * Evaluador de condiciones JSONB data-driven para las reglas 3280.
 *
 * Estructura soportada (arbol JSON):
 *   {
 *     "all":   [ ...operadores... ],          // AND
 *     "any":   [ ...operadores... ],          // OR
 *     "edad_gte": 18, "edad_lte": 5,          // rango de edad
 *     "etnia_in":  ["INDIGENA", ...],
 *     "genero": "F",
 *     "ubicacion_in": ["AMAZONAS", ...],
 *     "num_hijos_gte": 2,
 *     "tiene_riesgo": "EMBARAZO",
 *     "tiene_afeccion": "DIABETES"
 *   }
 *
 * Nuevos operadores son registrables sin modificar el core evaluador:
 * cada nodo "tipo" resuelve su propio resultado. Esa es la clave de que sea
 * data-driven: agregar una regla nunca implica recompilar.
 */
public class CondicionEvaluador {

    public boolean evalua(JsonNode condicion, PerfilPaciente perfil) {
        if (condicion == null || condicion.isNull() || condicion.isMissingNode()) {
            return true; // sin condicion => se aplica a todos
        }
        return evaluarNodo(condicion, perfil);
    }

    private boolean evaluarNodo(JsonNode nodo, PerfilPaciente p) {
        if (nodo.isObject()) {
            // Operadores de agrupacion AND/OR
            JsonNode all = nodo.get("all");
            if (all != null && all.isArray()) {
                for (JsonNode sub : all) {
                    if (!evaluarNodo(sub, p)) {
                        return false;
                    }
                }
            }
            JsonNode any = nodo.get("any");
            if (any != null && any.isArray()) {
                boolean anyOk = false;
                for (JsonNode sub : any) {
                    if (evaluarNodo(sub, p)) {
                        anyOk = true;
                        break;
                    }
                }
                if (!anyOk && (all == null || !all.isArray())) {
                    return false;
                }
            }

            // Rangos de edad
            if (nodo.has("edad_gte") && p.getEdad() != null && p.getEdad() < nodo.get("edad_gte").asInt()) {
                return false;
            }
            if (nodo.has("edad_lte") && p.getEdad() != null && p.getEdad() > nodo.get("edad_lte").asInt()) {
                return false;
            }
            // Etnia
            if (nodo.has("etnia_in") && !inList(p.getEtnia(), nodo.get("etnia_in"))) {
                return false;
            }
            // Genero
            if (nodo.has("genero") && p.getGenero() != null
                    && !p.getGenero().equalsIgnoreCase(nodo.get("genero").asText())) {
                return false;
            }
            // Ubicacion
            if (nodo.has("ubicacion_in") && !inList(p.getUbicacion(), nodo.get("ubicacion_in"))) {
                return false;
            }
            // Numero de hijos
            if (nodo.has("num_hijos_gte") && (p.getNumHijos() == null || p.getNumHijos() < nodo.get("num_hijos_gte").asInt())) {
                return false;
            }
            // Riesgos / afecciones
            if (nodo.has("tiene_riesgo") && !p.tieneRiesgo(nodo.get("tiene_riesgo").asText())) {
                return false;
            }
            if (nodo.has("tiene_afeccion") && !p.tieneAfeccion(nodo.get("tiene_afeccion").asText())) {
                return false;
            }
            return true;
        }
        return true;
    }

    private boolean inList(String valor, JsonNode lista) {
        if (valor == null || lista == null || !lista.isArray()) {
            return false;
        }
        List<String> ids = new ArrayList<>();
        lista.forEach(n -> ids.add(n.asText()));
        return ids.stream().anyMatch(v -> v.equalsIgnoreCase(valor));
    }
}

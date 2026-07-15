package com.panaderia.ecommerce.shared.infrastructure.web;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * Endpoints de solo lectura para los combos de ubicación (departamento / provincia / distrito).
 * Se exponen fuera de /admin/** porque tanto clientes como administradores los necesitan
 * para registrar direcciones.
 */
@RestController
@RequestMapping("/api/geo")
public class GeoController {

    private final JdbcTemplate jdbcTemplate;

    public GeoController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @GetMapping("/departamentos")
    public List<Map<String, Object>> departamentos() {
        return jdbcTemplate.queryForList("SELECT id_departamento AS id, nombre FROM departamento ORDER BY nombre");
    }

    @GetMapping("/provincias/{departamentoId}")
    public List<Map<String, Object>> provincias(@PathVariable Long departamentoId) {
        return jdbcTemplate.queryForList(
                "SELECT id_provincia AS id, nombre FROM provincia WHERE id_departamento = ? ORDER BY nombre",
                departamentoId);
    }

    @GetMapping("/distritos/{provinciaId}")
    public List<Map<String, Object>> distritos(@PathVariable Long provinciaId) {
        return jdbcTemplate.queryForList(
                "SELECT id_distrito AS id, nombre FROM distrito WHERE id_provincia = ? ORDER BY nombre",
                provinciaId);
    }

    /**
     * Dada una dirección/distrito ya guardado, devuelve la jerarquía completa
     * (departamento, provincia, distrito) para poder pre-seleccionar los combos
     * en el formulario de edición de direcciones.
     */
    @GetMapping("/distritos/{distritoId}/jerarquia")
    public Map<String, Object> jerarquia(@PathVariable Long distritoId) {
        List<Map<String, Object>> resultado = jdbcTemplate.queryForList(
                "SELECT dep.id_departamento AS departamentoId, prov.id_provincia AS provinciaId, dist.id_distrito AS distritoId " +
                        "FROM distrito dist " +
                        "JOIN provincia prov ON dist.id_provincia = prov.id_provincia " +
                        "JOIN departamento dep ON prov.id_departamento = dep.id_departamento " +
                        "WHERE dist.id_distrito = ?",
                distritoId);
        return resultado.isEmpty() ? Map.of() : resultado.get(0);
    }
}

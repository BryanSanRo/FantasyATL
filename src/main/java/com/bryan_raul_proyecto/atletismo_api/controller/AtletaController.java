package com.bryan_raul_proyecto.atletismo_api.controller;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class AtletaController {

    private final JdbcTemplate jdbc;

    public AtletaController(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @GetMapping("/atletas")
    public List<Map<String, Object>> listarAtletas(
            @RequestParam(defaultValue = "50") int limit,
            @RequestParam(defaultValue = "0") int offset
    ) {
        if (limit > 200) limit = 200;

        return jdbc.queryForList("""
            SELECT id, nombre, apellido, sexo, fecha_nac, nacionalidad
            FROM atletas
            ORDER BY apellido, nombre
            LIMIT ? OFFSET ?
        """, limit, offset);
    }
}
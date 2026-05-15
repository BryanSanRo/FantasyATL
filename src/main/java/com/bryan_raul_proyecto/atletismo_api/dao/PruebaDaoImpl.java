package com.bryan_raul_proyecto.atletismo_api.dao;

import com.bryan_raul_proyecto.atletismo_api.dto.PruebaDto;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.UUID;


@Repository
public class PruebaDaoImpl implements PruebaDao {

    private final JdbcTemplate jdbcTemplate;

    public PruebaDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public UUID insertar(PruebaDto prueba) {
        String sql = """
                INSERT INTO pruebas (codigo, nombre, sector, unidad)
                VALUES (?, ?, ?::sector, ?::unidad)
                RETURNING id
                """;
        return jdbcTemplate.queryForObject(
                sql,
                UUID.class,
                prueba.getCodigo(),
                prueba.getNombre(),
                prueba.getSector(),
                prueba.getUnidad()
        );
    }

    @Override
    public boolean existeCodigo(String codigo) {
        String sql = "SELECT COUNT(*) FROM pruebas WHERE codigo = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, codigo);
        return count != null && count > 0;
    }
}
package com.bryan_raul_proyecto.atletismo_api.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * Implementacion JDBC del acceso a puntos_jornada.
 */
@Repository
public class PuntoJornadaDaoImpl implements PuntoJornadaDao {

    private final JdbcTemplate jdbcTemplate;

    public PuntoJornadaDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public int deletePorCompeticion(UUID competicionId) {
        String sql = """
                DELETE FROM puntos_jornada
                WHERE atleta_id IN (
                    SELECT atleta_id
                    FROM resultados
                    WHERE competicion_id = ?
                )
                AND jornada = (
                    SELECT jornada
                    FROM competiciones
                    WHERE id = ?
                )
                """;
        return jdbcTemplate.update(sql, competicionId, competicionId);
    }

    @Override
    public void insertar(UUID atletaId, int jornada, int puntos, String descripcion) {
        String sql = """
                INSERT INTO puntos_jornada (atleta_id, jornada, puntos, descripcion)
                VALUES (?, ?, ?, ?)
                """;
        jdbcTemplate.update(sql, atletaId, jornada, puntos, descripcion);
    }
}
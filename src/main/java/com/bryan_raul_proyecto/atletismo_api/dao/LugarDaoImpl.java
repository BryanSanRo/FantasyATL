package com.bryan_raul_proyecto.atletismo_api.dao;

import com.bryan_raul_proyecto.atletismo_api.dto.LugarDto;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public class LugarDaoImpl implements LugarDao {

    private final JdbcTemplate jdbcTemplate;

    public LugarDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public UUID insertar(LugarDto lugar) {
        String sql = """
                INSERT INTO lugares (nombre, ciudad, pais)
                VALUES (?, ?, ?)
                RETURNING id
                """;
        return jdbcTemplate.queryForObject(
                sql,
                UUID.class,
                lugar.getNombre(),
                lugar.getCiudad(),
                lugar.getPais()
        );
    }

    @Override
    public boolean existePorId(UUID id) {
        String sql = "SELECT COUNT(*) FROM lugares WHERE id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, id);
        return count != null && count > 0;
    }
}
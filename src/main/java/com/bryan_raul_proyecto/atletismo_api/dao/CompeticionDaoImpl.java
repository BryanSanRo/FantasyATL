package com.bryan_raul_proyecto.atletismo_api.dao;

import com.bryan_raul_proyecto.atletismo_api.dto.CompeticionDto;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.UUID;


@Repository
public class CompeticionDaoImpl implements CompeticionDao {

    private final JdbcTemplate jdbcTemplate;

    public CompeticionDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public UUID insertar(CompeticionDto competicion) {
        String sql = """
                INSERT INTO competiciones (nombre, fecha, lugar_id, tipo_pista, tipo, jornada)
                VALUES (?, ?, ?, ?::tipo_pista, ?, ?)
                RETURNING id
                """;
        return jdbcTemplate.queryForObject(
                sql,
                UUID.class,
                competicion.getNombre(),
                competicion.getFecha(),
                competicion.getLugarId(),
                competicion.getTipoPista(),
                competicion.getTipo(),
                competicion.getJornada()
        );
    }

    @Override
    public boolean existePorId(UUID id) {
        String sql = "SELECT COUNT(*) FROM competiciones WHERE id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, id);
        return count != null && count > 0;
    }
}
package com.bryan_raul_proyecto.atletismo_api.dao;

import com.bryan_raul_proyecto.atletismo_api.dto.AtletaDto;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Repository
public class AtletaDaoImpl implements AtletaDao {

    private final JdbcTemplate jdbcTemplate;

    public AtletaDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // ============================================================
    // RowMapper compartido para no duplicar logica de mapeo
    // ============================================================
    private final RowMapper<AtletaDto> atletaRowMapper = (rs, rowNum) -> {
        AtletaDto dto = new AtletaDto();
        dto.setId(rs.getObject("id", UUID.class));
        dto.setNombre(rs.getString("nombre"));
        dto.setApellidos(rs.getString("apellidos"));
        dto.setNacionalidad(rs.getString("nacionalidad"));
        dto.setDisciplina(rs.getString("disciplina"));
        dto.setCategoria(rs.getString("categoria"));
        dto.setValoracion(rs.getObject("valoracion", Integer.class));
        dto.setPrecio(rs.getObject("precio", Integer.class));
        dto.setImagenUrl(rs.getString("imagen_url"));
        return dto;
    };

    @Override
    public List<AtletaDto> findAtletas(String disciplina, Integer limit, Integer offset) {
        StringBuilder sql = new StringBuilder("""
                SELECT id, nombre, apellidos, nacionalidad, disciplina,
                       categoria, valoracion, precio, imagen_url
                FROM atletas
                WHERE 1=1
                """);

        List<Object> params = new ArrayList<>();

        if (disciplina != null && !disciplina.isBlank()) {
            sql.append(" AND disciplina = ?");
            params.add(disciplina);
        }

        sql.append(" ORDER BY valoracion DESC, apellidos, nombre");

        if (limit != null) {
            sql.append(" LIMIT ?");
            params.add(limit);
        }

        if (offset != null && offset > 0) {
            sql.append(" OFFSET ?");
            params.add(offset);
        }

        return jdbcTemplate.query(sql.toString(), atletaRowMapper, params.toArray());
    }

    @Override
    public AtletaDto findById(UUID id) {
        String sql = """
                SELECT id, nombre, apellidos, nacionalidad, disciplina,
                       categoria, valoracion, precio, imagen_url
                FROM atletas
                WHERE id = ?
                """;

        try {
            return jdbcTemplate.queryForObject(sql, atletaRowMapper, id);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }
}
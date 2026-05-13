package com.bryan_raul_proyecto.atletismo_api.dao;

import com.bryan_raul_proyecto.atletismo_api.dto.ResultadoDto;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Repository
public class ResultadoDaoImpl implements ResultadoDao {

    private final JdbcTemplate jdbcTemplate;

    public ResultadoDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<ResultadoDto> findResultados(UUID atletaId, UUID competicionId, Integer limit) {
        StringBuilder sql = new StringBuilder("""
                SELECT
                    r.id AS "resultadoId",
                    a.id AS "atletaId",
                    a.nombre AS "nombre",
                    a.apellidos AS "apellidos",
                    p.nombre AS "prueba",
                    c.nombre AS "competicion",
                    c.fecha AS "fecha",
                    l.nombre AS "lugar",
                    c.tipo_pista AS "tipoPista",
                    r.estado AS "estado",
                    r.marca_num AS "marcaNum",
                    r.posicion AS "posicion",
                    r.record_personal AS "recordPersonal",
                    r.record_mundial AS "recordMundial"
                FROM resultados r
                JOIN atletas a ON r.atleta_id = a.id
                JOIN pruebas p ON r.prueba_id = p.id
                JOIN competiciones c ON r.competicion_id = c.id
                JOIN lugares l ON c.lugar_id = l.id
                WHERE 1=1
                """);

        List<Object> params = new ArrayList<>();

        if (atletaId != null) {
            sql.append(" AND a.id = ?");
            params.add(atletaId);
        }

        if (competicionId != null) {
            sql.append(" AND c.id = ?");
            params.add(competicionId);
        }

        sql.append(" ORDER BY c.fecha DESC, r.id");

        if (limit != null) {
            sql.append(" LIMIT ?");
            params.add(limit);
        }

        return jdbcTemplate.query(sql.toString(), params.toArray(), (rs, rowNum) -> {
            ResultadoDto dto = new ResultadoDto();
            dto.setResultadoId(rs.getObject("resultadoId", UUID.class));
            dto.setAtletaId(rs.getObject("atletaId", UUID.class));
            dto.setNombre(rs.getString("nombre"));
            dto.setApellidos(rs.getString("apellidos"));
            dto.setPrueba(rs.getString("prueba"));
            dto.setCompeticion(rs.getString("competicion"));
            dto.setFecha(rs.getObject("fecha", java.time.LocalDate.class));
            dto.setLugar(rs.getString("lugar"));
            dto.setTipoPista(rs.getString("tipoPista"));
            dto.setEstado(rs.getString("estado"));
            dto.setMarcaNum(rs.getBigDecimal("marcaNum"));
            dto.setPosicion(rs.getObject("posicion", Integer.class));
            dto.setRecordPersonal(rs.getObject("recordPersonal", Boolean.class));
            dto.setRecordMundial(rs.getObject("recordMundial", Boolean.class));
            return dto;
        });
    }
}
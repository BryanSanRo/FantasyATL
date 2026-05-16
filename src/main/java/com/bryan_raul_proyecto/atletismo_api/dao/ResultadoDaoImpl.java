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
                    r.id AS "id",
                    a.id AS "atletaId",
                    c.id AS "competicionId",
                    p.id AS "pruebaId",
                    a.nombre AS "nombreAtleta",
                    a.apellidos AS "apellidosAtleta",
                    p.nombre AS "nombrePrueba",
                    c.nombre AS "nombreCompeticion",
                    c.fecha AS "fechaCompeticion",
                    l.nombre AS "nombreLugar",
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

        return jdbcTemplate.query(sql.toString(), (rs, rowNum) -> {
            ResultadoDto dto = new ResultadoDto();
            dto.setId(rs.getObject("id", UUID.class));
            dto.setAtletaId(rs.getObject("atletaId", UUID.class));
            dto.setCompeticionId(rs.getObject("competicionId", UUID.class));
            dto.setPruebaId(rs.getObject("pruebaId", UUID.class));
            dto.setNombreAtleta(rs.getString("nombreAtleta"));
            dto.setApellidosAtleta(rs.getString("apellidosAtleta"));
            dto.setNombrePrueba(rs.getString("nombrePrueba"));
            dto.setNombreCompeticion(rs.getString("nombreCompeticion"));
            dto.setFechaCompeticion(rs.getObject("fechaCompeticion", java.time.LocalDate.class));
            dto.setNombreLugar(rs.getString("nombreLugar"));
            dto.setTipoPista(rs.getString("tipoPista"));
            dto.setEstado(rs.getString("estado"));
            dto.setMarcaNum(rs.getBigDecimal("marcaNum"));
            dto.setPosicion(rs.getObject("posicion", Integer.class));
            dto.setRecordPersonal(rs.getObject("recordPersonal", Boolean.class));
            dto.setRecordMundial(rs.getObject("recordMundial", Boolean.class));
            return dto;
        }, params.toArray());
    }

    @Override
    public UUID insertar(ResultadoDto resultado) {
        String sql = """
                INSERT INTO resultados (atleta_id, competicion_id, prueba_id,
                                        estado, marca_num, posicion,
                                        record_personal, record_mundial)
                VALUES (?, ?, ?, ?::estado_resultado, ?, ?, ?, ?)
                RETURNING id
                """;
        return jdbcTemplate.queryForObject(
                sql,
                UUID.class,
                resultado.getAtletaId(),
                resultado.getCompeticionId(),
                resultado.getPruebaId(),
                resultado.getEstado(),
                resultado.getMarcaNum(),
                resultado.getPosicion(),
                resultado.getRecordPersonal(),
                resultado.getRecordMundial()
        );
    }
}
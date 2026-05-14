package com.bryan_raul_proyecto.atletismo_api.service;

import com.bryan_raul_proyecto.atletismo_api.dao.PuntoJornadaDao;
import com.bryan_raul_proyecto.atletismo_api.dao.ResultadoDao;
import com.bryan_raul_proyecto.atletismo_api.dto.ResultadoDto;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Maneja el calculo y persistencia de puntos de una competicion.
 */
@Service
public class PuntuacionService {

    private final ResultadoDao resultadoDao;
    private final PuntoJornadaDao puntoJornadaDao;
    private final PuntuacionCalculator calculator;
    private final JdbcTemplate jdbcTemplate;

    public PuntuacionService(ResultadoDao resultadoDao,
                             PuntoJornadaDao puntoJornadaDao,
                             PuntuacionCalculator calculator,
                             JdbcTemplate jdbcTemplate) {
        this.resultadoDao = resultadoDao;
        this.puntoJornadaDao = puntoJornadaDao;
        this.calculator = calculator;
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * Calcula y persiste los puntos de todos los resultados de una competicion.
     * Borra puntos previos antes de insertar los nuevos.
     * @param competicionId identificador de la competicion
     * @return numero de puntuaciones calculadas e insertadas
     */
    @Transactional
    public int calcularYGuardarPuntosDeCompeticion(UUID competicionId) {
        Integer jornada = obtenerJornada(competicionId);
        if (jornada == null) {
            throw new IllegalArgumentException("Competicion no encontrada: " + competicionId);
        }

        List<ResultadoDto> resultados = resultadoDao.findResultados(null, competicionId, null);

        puntoJornadaDao.deletePorCompeticion(competicionId);

        int insertados = 0;
        for (ResultadoDto resultado : resultados) {
            int puntos = calculator.calcular(resultado);
            String descripcion = calculator.generarDescripcion(resultado);
            puntoJornadaDao.insertar(resultado.getAtletaId(), jornada, puntos, descripcion);
            insertados++;
        }

        return insertados;
    }
    /**
     * Consulta el numero de jornada asignado a una competicion.
     * @param competicionId identificador de la competicion
     * @return numero de jornada, o null si la competicion no existe
     */
    private Integer obtenerJornada(UUID competicionId) {
        String sql = "SELECT jornada FROM competiciones WHERE id = ?";
        try {
            return jdbcTemplate.queryForObject(sql, Integer.class, competicionId);
        } catch (org.springframework.dao.EmptyResultDataAccessException e) {
            return null;
        }
    }
}
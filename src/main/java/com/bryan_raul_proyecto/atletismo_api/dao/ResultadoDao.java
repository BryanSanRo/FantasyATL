package com.bryan_raul_proyecto.atletismo_api.dao;

import com.bryan_raul_proyecto.atletismo_api.dto.ResultadoDto;

import java.util.List;
import java.util.UUID;

public interface ResultadoDao {

    /**
     * Devuelve la lista de resultados con filtros opcionales.
     * @param atletaId identificador del atleta para filtrar (puede ser nulo)
     * @param competicionId identificador de la competicion para filtrar (puede ser nulo)
     * @param limit numero maximo de resultados (puede ser nulo)
     * @return lista de resultados que cumplen los filtros
     */
    List<ResultadoDto> findResultados(UUID atletaId, UUID competicionId, Integer limit);

    /**
     * Inserta un nuevo resultado en la base de datos.
     * @param resultado datos del resultado a insertar
     * @return identificador generado para el nuevo resultado
     */
    UUID insertar(ResultadoDto resultado);
}
package com.bryan_raul_proyecto.atletismo_api.dao;

import com.bryan_raul_proyecto.atletismo_api.dto.AtletaDto;

import java.util.List;
import java.util.UUID;

public interface AtletaDao {

    /**
     * Devuelve la lista de atletas con filtros y paginacion opcional.
     * @param disciplina disciplina por la que filtrar (puede ser nula)
     * @param limit numero maximo de resultados (puede ser nulo)
     * @param offset desplazamiento inicial para paginacion (puede ser nulo)
     * @return lista de atletas que cumplen los filtros
     */
    List<AtletaDto> findAtletas(String disciplina, Integer limit, Integer offset);

    /**
     * Devuelve el atleta con el identificador dado.
     * @param id identificador del atleta
     * @return el atleta encontrado, o null si no existe
     */
    AtletaDto findById(UUID id);

    /**
     * Comprueba si existe un atleta con el identificador dado.
     * @param id identificador del atleta a comprobar
     * @return true si existe, false en caso contrario
     */
    boolean existePorId(UUID id);
}
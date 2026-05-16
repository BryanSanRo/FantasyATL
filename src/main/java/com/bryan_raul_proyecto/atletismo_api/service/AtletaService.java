package com.bryan_raul_proyecto.atletismo_api.service;

import com.bryan_raul_proyecto.atletismo_api.dao.AtletaDao;
import com.bryan_raul_proyecto.atletismo_api.dto.AtletaDto;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class AtletaService {

    private final AtletaDao atletaDao;

    public AtletaService(AtletaDao atletaDao) {
        this.atletaDao = atletaDao;
    }

    /**
     * Devuelve la lista de atletas con filtros y paginacion opcional.
     * @param disciplina disciplina por la que filtrar (puede ser nula)
     * @param limit numero maximo de resultados (puede ser nulo)
     * @param offset desplazamiento inicial para paginacion (puede ser nulo)
     * @return lista de atletas que cumplen los filtros
     */
    public List<AtletaDto> getAtletas(String disciplina, Integer limit, Integer offset) {
        return atletaDao.findAtletas(disciplina, limit, offset);
    }

    /**
     * Devuelve el atleta con el identificador dado.
     * @param id identificador del atleta
     * @return el atleta encontrado, o null si no existe
     */
    public AtletaDto getAtletaById(UUID id) {
        return atletaDao.findById(id);
    }

    /**
     * Comprueba si existe un atleta con el identificador dado.
     * @param id identificador del atleta a comprobar
     * @return true si existe, false en caso contrario
     */
    public boolean existe(UUID id) {
        return atletaDao.existePorId(id);
    }
}
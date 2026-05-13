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

    public List<AtletaDto> getAtletas(String disciplina, Integer limit, Integer offset) {
        return atletaDao.findAtletas(disciplina, limit, offset);
    }

    public AtletaDto getAtletaById(UUID id) {
        return atletaDao.findById(id);
    }
}
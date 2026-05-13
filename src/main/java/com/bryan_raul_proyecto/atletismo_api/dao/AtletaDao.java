package com.bryan_raul_proyecto.atletismo_api.dao;

import com.bryan_raul_proyecto.atletismo_api.dto.AtletaDto;

import java.util.List;
import java.util.UUID;

public interface AtletaDao {

    List<AtletaDto> findAtletas(String disciplina, Integer limit, Integer offset);

    AtletaDto findById(UUID id);
}
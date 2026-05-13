package com.bryan_raul_proyecto.atletismo_api.dao;

import com.bryan_raul_proyecto.atletismo_api.dto.ResultadoDto;

import java.util.List;
import java.util.UUID;

public interface ResultadoDao {
    List<ResultadoDto> findResultados(UUID atletaId, UUID competicionId, Integer limit);
}
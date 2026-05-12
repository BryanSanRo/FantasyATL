package com.bryan_raul_proyecto.atletismo_api.dao;

import com.bryan_raul_proyecto.atletismo_api.dto.ResultadoDto;

import java.util.List;

public interface ResultadoDao {
    List<ResultadoDto> findResultados(Long atletaId, Long competicionId, Integer limit);
}
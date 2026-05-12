package com.bryan_raul_proyecto.atletismo_api.service;

import com.bryan_raul_proyecto.atletismo_api.dao.ResultadoDao;
import com.bryan_raul_proyecto.atletismo_api.dto.ResultadoDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ResultadoService {

    private final ResultadoDao resultadoDao;

    public ResultadoService(ResultadoDao resultadoDao) {
        this.resultadoDao = resultadoDao;
    }

    public List<ResultadoDto> getResultados(Long atletaId, Long competicionId, Integer limit) {
        return resultadoDao.findResultados(atletaId, competicionId, limit);
    }
}
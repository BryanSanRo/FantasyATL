package com.bryan_raul_proyecto.atletismo_api.controller;

import com.bryan_raul_proyecto.atletismo_api.dto.ResultadoDto;
import com.bryan_raul_proyecto.atletismo_api.service.ResultadoService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@RestController
public class ResultadoController {

    private final ResultadoService resultadoService;

    public ResultadoController(ResultadoService resultadoService) {
        this.resultadoService = resultadoService;
    }

    @GetMapping("/api/resultados")
    public List<ResultadoDto> getResultados(
            @RequestParam(required = false) UUID atletaId,
            @RequestParam(required = false) UUID competicionId,
            @RequestParam(required = false) Integer limit
    ) {
        if (limit != null && limit <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "El parametro limit debe ser mayor que 0");
        }

        return resultadoService.getResultados(atletaId, competicionId, limit);
    }
}
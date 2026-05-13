package com.bryan_raul_proyecto.atletismo_api.controller;

import com.bryan_raul_proyecto.atletismo_api.dto.AtletaDto;
import com.bryan_raul_proyecto.atletismo_api.service.AtletaService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api")
public class AtletaController {

    private final AtletaService atletaService;

    public AtletaController(AtletaService atletaService) {
        this.atletaService = atletaService;
    }

    @GetMapping("/atletas")
    public List<AtletaDto> listarAtletas(
            @RequestParam(required = false) String disciplina,
            @RequestParam(required = false) Integer limit,
            @RequestParam(required = false) Integer offset
    ) {
        if (limit != null && limit <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "El parametro limit debe ser mayor que 0");
        }
        if (offset != null && offset < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "El parametro offset no puede ser negativo");
        }

        return atletaService.getAtletas(disciplina, limit, offset);
    }

    @GetMapping("/atletas/{id}")
    public AtletaDto getAtleta(@PathVariable UUID id) {
        AtletaDto atleta = atletaService.getAtletaById(id);
        if (atleta == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Atleta no encontrado con id: " + id);
        }
        return atleta;
    }
}
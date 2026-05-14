package com.bryan_raul_proyecto.atletismo_api.controller;

import com.bryan_raul_proyecto.atletismo_api.service.PuntuacionService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;
import java.util.UUID;

/**
 * Operaciones administrativas del backend.
 */
@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final PuntuacionService puntuacionService;

    public AdminController(PuntuacionService puntuacionService) {
        this.puntuacionService = puntuacionService;
    }

    /**
     * Calcula y persiste los puntos de fantasy de una competicion.
     * Las invocaciones repetidas no generan duplicados.
     * @param competicionId identificador de la competicion
     * @return resumen/mapa con el numero de puntuaciones calculadas
     */
    @PostMapping("/calcular-puntos/{competicionId}")
    public Map<String, Object> calcularPuntos(@PathVariable UUID competicionId) {
        try {
            int insertados = puntuacionService.calcularYGuardarPuntosDeCompeticion(competicionId);
            return Map.of(
                    "competicionId", competicionId,
                    "puntuacionesCalculadas", insertados,
                    "mensaje", "Calculo completado correctamente"
            );
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }
}
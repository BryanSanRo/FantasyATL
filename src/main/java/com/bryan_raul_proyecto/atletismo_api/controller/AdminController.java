package com.bryan_raul_proyecto.atletismo_api.controller;

import com.bryan_raul_proyecto.atletismo_api.dto.CompeticionDto;
import com.bryan_raul_proyecto.atletismo_api.dto.LugarDto;
import com.bryan_raul_proyecto.atletismo_api.dto.PruebaDto;
import com.bryan_raul_proyecto.atletismo_api.dto.ResultadoDto;
import com.bryan_raul_proyecto.atletismo_api.service.CompeticionService;
import com.bryan_raul_proyecto.atletismo_api.service.LugarService;
import com.bryan_raul_proyecto.atletismo_api.service.PruebaService;
import com.bryan_raul_proyecto.atletismo_api.service.PuntuacionService;
import com.bryan_raul_proyecto.atletismo_api.service.ResultadoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
    private final LugarService lugarService;
    private final PruebaService pruebaService;
    private final CompeticionService competicionService;
    private final ResultadoService resultadoService;

    public AdminController(PuntuacionService puntuacionService,
                           LugarService lugarService,
                           PruebaService pruebaService,
                           CompeticionService competicionService,
                           ResultadoService resultadoService) {
        this.puntuacionService = puntuacionService;
        this.lugarService = lugarService;
        this.pruebaService = pruebaService;
        this.competicionService = competicionService;
        this.resultadoService = resultadoService;
    }

    /**
     * Calcula y persiste los puntos de fantasy de una competicion.
     * Operacion idempotente: invocaciones repetidas no generan duplicados.
     * @param competicionId identificador de la competicion
     * @return resumen con el numero de puntuaciones calculadas
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

    /**
     * Crea un nuevo lugar en el sistema.
     * @param lugar datos del lugar a crear
     * @return el lugar creado con su identificador
     */
    @PostMapping("/lugares")
    public ResponseEntity<LugarDto> crearLugar(@RequestBody LugarDto lugar) {
        UUID id = lugarService.crear(lugar);
        lugar.setId(id);
        return ResponseEntity.status(HttpStatus.CREATED).body(lugar);
    }

    /**
     * Crea una nueva prueba en el sistema.
     * @param prueba datos de la prueba a crear
     * @return la prueba creada con su identificador
     */
    @PostMapping("/pruebas")
    public ResponseEntity<PruebaDto> crearPrueba(@RequestBody PruebaDto prueba) {
        try {
            UUID id = pruebaService.crear(prueba);
            prueba.setId(id);
            return ResponseEntity.status(HttpStatus.CREATED).body(prueba);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    /**
     * Crea una nueva competicion en el sistema.
     * @param competicion datos de la competicion a crear
     * @return la competicion creada con su identificador
     */
    @PostMapping("/competiciones")
    public ResponseEntity<CompeticionDto> crearCompeticion(@RequestBody CompeticionDto competicion) {
        try {
            UUID id = competicionService.crear(competicion);
            competicion.setId(id);
            return ResponseEntity.status(HttpStatus.CREATED).body(competicion);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    /**
     * Crea un nuevo resultado en el sistema.
     * @param resultado datos del resultado a crear
     * @return el resultado creado con su identificador
     */
    @PostMapping("/resultados")
    public ResponseEntity<ResultadoDto> crearResultado(@RequestBody ResultadoDto resultado) {
        try {
            UUID id = resultadoService.crear(resultado);
            resultado.setId(id);
            return ResponseEntity.status(HttpStatus.CREATED).body(resultado);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }
}
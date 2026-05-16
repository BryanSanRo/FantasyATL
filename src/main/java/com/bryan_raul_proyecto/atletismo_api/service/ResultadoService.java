package com.bryan_raul_proyecto.atletismo_api.service;

import com.bryan_raul_proyecto.atletismo_api.dao.ResultadoDao;
import com.bryan_raul_proyecto.atletismo_api.dto.ResultadoDto;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ResultadoService {

    private final ResultadoDao resultadoDao;
    private final AtletaService atletaService;
    private final CompeticionService competicionService;
    private final PruebaService pruebaService;

    public ResultadoService(ResultadoDao resultadoDao,
                            AtletaService atletaService,
                            CompeticionService competicionService,
                            PruebaService pruebaService) {
        this.resultadoDao = resultadoDao;
        this.atletaService = atletaService;
        this.competicionService = competicionService;
        this.pruebaService = pruebaService;
    }

    /**
     * Devuelve la lista de resultados con filtros opcionales.
     * @param atletaId identificador del atleta para filtrar (puede ser nulo)
     * @param competicionId identificador de la competicion para filtrar (puede ser nulo)
     * @param limit numero maximo de resultados (puede ser nulo)
     * @return lista de resultados que cumplen los filtros
     */
    public List<ResultadoDto> getResultados(UUID atletaId, UUID competicionId, Integer limit) {
        return resultadoDao.findResultados(atletaId, competicionId, limit);
    }

    /**
     * Crea un nuevo resultado en el sistema tras validar sus campos.
     * @param resultado datos del resultado a crear
     * @return identificador generado del resultado creado
     */
    public UUID crear(ResultadoDto resultado) {
        validar(resultado);
        return resultadoDao.insertar(resultado);
    }

    /**
     * Valida las reglas de negocio del resultado:
     * estado admitido, coherencia entre estado y marca/posicion,
     * y existencia de atleta, competicion y prueba.
     * @param resultado resultado a validar
     */
    private void validar(ResultadoDto resultado) {
        if (!ResultadoConstantes.ESTADOS_VALIDOS.contains(resultado.getEstado())) {
            throw new IllegalArgumentException(
                    "Estado no valido: '" + resultado.getEstado() + "'. " +
                            "Valores admitidos: " + ResultadoConstantes.ESTADOS_VALIDOS
            );
        }
        if (ResultadoConstantes.ESTADO_OK.equals(resultado.getEstado())) {
            if (resultado.getPosicion() == null || resultado.getPosicion() <= 0) {
                throw new IllegalArgumentException(
                        "Un resultado con estado 'ok' debe tener una posicion valida (mayor que 0)"
                );
            }
            if (resultado.getMarcaNum() == null) {
                throw new IllegalArgumentException(
                        "Un resultado con estado 'ok' debe tener una marca registrada"
                );
            }
        }
        if (!atletaService.existe(resultado.getAtletaId())) {
            throw new IllegalArgumentException(
                    "No existe un atleta con id: '" + resultado.getAtletaId() + "'"
            );
        }
        if (!competicionService.existe(resultado.getCompeticionId())) {
            throw new IllegalArgumentException(
                    "No existe una competicion con id: '" + resultado.getCompeticionId() + "'"
            );
        }
        if (!pruebaService.existe(resultado.getPruebaId())) {
            throw new IllegalArgumentException(
                    "No existe una prueba con id: '" + resultado.getPruebaId() + "'"
            );
        }
    }
}
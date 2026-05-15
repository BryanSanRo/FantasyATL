package com.bryan_raul_proyecto.atletismo_api.service;

import com.bryan_raul_proyecto.atletismo_api.dao.PruebaDao;
import com.bryan_raul_proyecto.atletismo_api.dto.PruebaDto;
import org.springframework.stereotype.Service;

import java.util.UUID;


@Service
public class PruebaService {

    private final PruebaDao pruebaDao;

    public PruebaService(PruebaDao pruebaDao) {
        this.pruebaDao = pruebaDao;
    }

    /**
     * Crea una nueva prueba en el sistema tras validar sus campos.
     * @param prueba datos de la prueba a crear
     * @return identificador generado de la prueba creada
     */
    public UUID crear(PruebaDto prueba) {
        validar(prueba);
        return pruebaDao.insertar(prueba);
    }

    /**
     * Valida que los campos de la prueba cumplan las reglas de negocio:
     * sector y unidad con valores admitidos, y codigo no duplicado.
     * @param prueba prueba a validar
     */
    private void validar(PruebaDto prueba) {
        if (!PruebaConstantes.SECTORES_VALIDOS.contains(prueba.getSector())) {
            throw new IllegalArgumentException(
                    "Sector no valido: '" + prueba.getSector() + "'. " +
                            "Valores admitidos: " + PruebaConstantes.SECTORES_VALIDOS
            );
        }
        if (!PruebaConstantes.UNIDADES_VALIDAS.contains(prueba.getUnidad())) {
            throw new IllegalArgumentException(
                    "Unidad no valida: '" + prueba.getUnidad() + "'. " +
                            "Valores admitidos: " + PruebaConstantes.UNIDADES_VALIDAS
            );
        }
        if (pruebaDao.existeCodigo(prueba.getCodigo())) {
            throw new IllegalArgumentException(
                    "Ya existe una prueba con el codigo: '" + prueba.getCodigo() + "'"
            );
        }
    }
}
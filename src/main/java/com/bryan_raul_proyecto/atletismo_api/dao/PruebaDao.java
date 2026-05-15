package com.bryan_raul_proyecto.atletismo_api.dao;

import com.bryan_raul_proyecto.atletismo_api.dto.PruebaDto;

import java.util.UUID;


public interface PruebaDao {

    /**
     * Inserta una nueva prueba en la base de datos.
     * @param prueba datos de la prueba a insertar
     * @return identificador generado para la nueva prueba
     */
    UUID insertar(PruebaDto prueba);

    /**
     * Comprueba si ya existe una prueba con el codigo dado.
     * @param codigo codigo a comprobar
     * @return true si existe una prueba con ese codigo, false en caso contrario
     */
    boolean existeCodigo(String codigo);
}
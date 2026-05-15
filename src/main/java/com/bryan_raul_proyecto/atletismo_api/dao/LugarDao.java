package com.bryan_raul_proyecto.atletismo_api.dao;

import com.bryan_raul_proyecto.atletismo_api.dto.LugarDto;

import java.util.UUID;

public interface LugarDao {

    /**
     * Inserta un nuevo lugar en la base de datos.
     * @param lugar datos del lugar a insertar
     * @return identificador generado para el nuevo lugar
     */
    UUID insertar(LugarDto lugar);
}
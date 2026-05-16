package com.bryan_raul_proyecto.atletismo_api.dao;

import com.bryan_raul_proyecto.atletismo_api.dto.CompeticionDto;

import java.util.UUID;


public interface CompeticionDao {

    /**
     * Inserta una nueva competicion en la base de datos.
     * @param competicion datos de la competicion a insertar
     * @return identificador generado para la nueva competicion
     */
    UUID insertar(CompeticionDto competicion);

    /**
     * Comprueba si existe una competicion con el identificador dado.
     * @param id identificador de la competicion a comprobar
     * @return true si existe, false en caso contrario
     */
    boolean existePorId(UUID id);
}
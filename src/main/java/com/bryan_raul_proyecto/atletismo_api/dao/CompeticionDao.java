package com.bryan_raul_proyecto.atletismo_api.dao;

import com.bryan_raul_proyecto.atletismo_api.dto.CompeticionDto;
import java.time.LocalDate;
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

    /**
     * Comprueba si ya existe una competicion con el nombre y fecha dados.
     * @param nombre nombre de la competicion
     * @param fecha fecha de la competicion
     * @return true si existe, false en caso contrario
     */
    boolean existeNombreFecha(String nombre, LocalDate fecha);

}
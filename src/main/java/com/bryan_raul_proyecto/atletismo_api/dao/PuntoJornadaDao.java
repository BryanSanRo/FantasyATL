package com.bryan_raul_proyecto.atletismo_api.dao;

import java.util.UUID;

/**
 * Acceso a datos para la tabla puntos_jornada.
 */
public interface PuntoJornadaDao {

    /**
     * Borra todas las entradas de puntos asociadas a una competicion.
     * Util para recalcular puntos sin generar duplicados.
     * @param competicionId identificador de la competicion
     * @return numero de filas eliminadas
     */
    int deletePorCompeticion(UUID competicionId);

    /**
     * Inserta una nueva entrada en puntos_jornada.
     * @param atletaId atleta al que se asignan los puntos
     * @param jornada numero de jornada
     * @param puntos cantidad de puntos (puede ser negativa)
     * @param descripcion descripcion legible del calculo
     */
    void insertar(UUID atletaId, int jornada, int puntos, String descripcion);
}
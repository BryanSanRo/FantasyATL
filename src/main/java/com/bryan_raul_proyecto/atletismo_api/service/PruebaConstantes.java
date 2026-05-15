package com.bryan_raul_proyecto.atletismo_api.service;

import java.util.List;

/**
 * Constantes y valores validos para la entidad Prueba.
 */
public final class PruebaConstantes {

    private PruebaConstantes() {
        throw new UnsupportedOperationException("Clase de constantes, no instanciable");
    }

    // ============================================================
    // Sectores validos (debe coincidir con el enum 'sector' de la BD)
    // ============================================================
    public static final List<String> SECTORES_VALIDOS = List.of(
            "Velocidad",
            "MedioFondo",
            "Fondo",
            "Vallas",
            "Saltos",
            "Lanzamientos",
            "Marcha",
            "Combinadas",
            "Relevos"
    );

    // ============================================================
    // Unidades validas (debe coincidir con el enum 'unidad' de la BD)
    // ============================================================
    public static final List<String> UNIDADES_VALIDAS = List.of(
            "s",
            "m",
            "pts"
    );
}
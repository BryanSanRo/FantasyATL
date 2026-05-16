package com.bryan_raul_proyecto.atletismo_api.service;

import java.util.List;

/**
 * Constantes y valores validos para la entidad Competicion.
 */
public final class CompeticionConstantes {

    private CompeticionConstantes() {
        throw new UnsupportedOperationException("Clase de constantes, no instanciable");
    }

    // ============================================================
    // Tipos de pista validos (debe coincidir con el enum 'tipo_pista' de la BD)
    // ============================================================
    public static final List<String> TIPOS_PISTA_VALIDOS = List.of(
            "AL",
            "PC"
    );
}
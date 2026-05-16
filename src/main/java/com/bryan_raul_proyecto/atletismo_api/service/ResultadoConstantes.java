package com.bryan_raul_proyecto.atletismo_api.service;

import java.util.List;

/**
 * Constantes y valores validos para la entidad Resultado.
 */
public final class ResultadoConstantes {

    private ResultadoConstantes() {
        throw new UnsupportedOperationException("Clase de constantes, no instanciable");
    }

    // =============================
    // Estados validos
    // =============================
    public static final List<String> ESTADOS_VALIDOS = List.of(
            "ok",
            "dns",
            "dnf",
            "dq",
            "np",
            "nm",
            "ret"
    );

    public static final String ESTADO_OK = "ok";
}
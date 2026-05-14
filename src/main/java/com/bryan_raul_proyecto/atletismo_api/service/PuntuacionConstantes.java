package com.bryan_raul_proyecto.atletismo_api.service;

/**
 * Constantes del sistema de puntuacion del fantasy.
 */
public final class PuntuacionConstantes {

    private PuntuacionConstantes() {
        throw new UnsupportedOperationException("Clase de constantes, no instanciable");
    }

    // ============================================================
    // Puntos por posicion (top 8)
    // ============================================================
    public static final int PUNTOS_POSICION_1 = 25;
    public static final int PUNTOS_POSICION_2 = 18;
    public static final int PUNTOS_POSICION_3 = 15;
    public static final int PUNTOS_POSICION_4 = 12;
    public static final int PUNTOS_POSICION_5 = 10;
    public static final int PUNTOS_POSICION_6 = 8;
    public static final int PUNTOS_POSICION_7 = 6;
    public static final int PUNTOS_POSICION_8 = 4;

    public static final int PUNTOS_POSICION_NO_TOP_8 = 1;

    // ============================================================
    // Bonificaciones por records
    // ============================================================
    public static final int BONUS_RECORD_PERSONAL = 5;
    public static final int BONUS_RECORD_MUNDIAL = 20;

    // ============================================================
    // Penalizaciones por estados anomalos
    // ============================================================
    public static final int PENALIZACION_DNF = -3;
    public static final int PENALIZACION_DQ = -5;

    public static final int PUNTOS_DNS = 0;
    public static final int PUNTOS_NM = 0;
    public static final int PUNTOS_NP = 0;
    public static final int PUNTOS_RET = 0;
}
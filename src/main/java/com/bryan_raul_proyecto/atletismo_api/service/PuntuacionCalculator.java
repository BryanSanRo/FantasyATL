package com.bryan_raul_proyecto.atletismo_api.service;

import com.bryan_raul_proyecto.atletismo_api.dto.ResultadoDto;
import org.springframework.stereotype.Component;

import static com.bryan_raul_proyecto.atletismo_api.service.PuntuacionConstantes.*;

/**
 * Calcula los puntos del fantasy a partir de un resultado deportivo.
 */
@Component
public class PuntuacionCalculator {

    /**
     * Calcula los puntos totales del fantasy para un resultado.
     * @param resultado el resultado a evaluar (no nulo)
     * @return puntos totales (puede ser negativo en caso de penalizacion)
     */
    public int calcular(ResultadoDto resultado) {
        if (resultado == null || resultado.getEstado() == null) {
            return 0;
        }

        return switch (resultado.getEstado().toLowerCase()) {
            case "ok"  -> calcularPuntosOk(resultado);
            case "dnf" -> PENALIZACION_DNF;
            case "dq"  -> PENALIZACION_DQ;
            case "dns" -> PUNTOS_DNS;
            case "nm"  -> PUNTOS_NM;
            case "np"  -> PUNTOS_NP;
            case "ret" -> PUNTOS_RET;
            default    -> 0;
        };
    }

    /**
     * Genera una descripcion legible del resultado para guardar en
     * puntos_jornada.descripcion y mostrar al usuario en la app.
     * Si el resultado o su estado son nulos devuelve "Sin datos".
     * @param resultado resultado del que se quiere generar la descripcion
     * @return cadena descriptiva del resultado, nunca nula
     */
    public String generarDescripcion(ResultadoDto resultado) {
        if (resultado == null || resultado.getEstado() == null) {
            return "Sin datos";
        }

        String estado = resultado.getEstado().toLowerCase();

        if (!"ok".equals(estado)) {
            return estado.toUpperCase();
        }

        StringBuilder desc = new StringBuilder();
        Integer pos = resultado.getPosicion();

        if (pos != null) {
            desc.append(pos).append("º");
        }
        if (resultado.getNombrePrueba() != null) {
            desc.append(" en ").append(resultado.getNombrePrueba());
        }
        if (Boolean.TRUE.equals(resultado.getRecordMundial())) {
            desc.append(" (RM)");
        } else if (Boolean.TRUE.equals(resultado.getRecordPersonal())) {
            desc.append(" (RP)");
        }

        return desc.toString();
    }

    /**
     * Calcula los puntos cuando el resultado es valido (estado=ok):
     * puntos por posicion mas bonificaciones por records.
     * @param resultado el resultado con estado=ok
     * @return puntos sumados incluyendo bonificaciones
     */
    private int calcularPuntosOk(ResultadoDto resultado) {
        int puntos = puntosPorPosicion(resultado.getPosicion());

        if (Boolean.TRUE.equals(resultado.getRecordPersonal())) {
            puntos += BONUS_RECORD_PERSONAL;
        }
        if (Boolean.TRUE.equals(resultado.getRecordMundial())) {
            puntos += BONUS_RECORD_MUNDIAL;
        }

        return puntos;
    }

    /**
     * Devuelve los puntos correspondientes a una posicion final.
     * @param posicion posicion final (puede ser nula)
     * @return puntos por la posicion
     */
    private int puntosPorPosicion(Integer posicion) {
        if (posicion == null) {
            return 0;
        }
        return switch (posicion) {
            case 1 -> PUNTOS_POSICION_1;
            case 2 -> PUNTOS_POSICION_2;
            case 3 -> PUNTOS_POSICION_3;
            case 4 -> PUNTOS_POSICION_4;
            case 5 -> PUNTOS_POSICION_5;
            case 6 -> PUNTOS_POSICION_6;
            case 7 -> PUNTOS_POSICION_7;
            case 8 -> PUNTOS_POSICION_8;
            default -> PUNTOS_POSICION_NO_TOP_8;
        };
    }
}
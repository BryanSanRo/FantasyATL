package com.bryan_raul_proyecto.atletismo_api.service;

import com.bryan_raul_proyecto.atletismo_api.dto.ResultadoDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas unitarias del calculador de puntuaciones.
 */
class PuntuacionCalculatorTest {

    private PuntuacionCalculator calculator;

    @BeforeEach
    void setUp() {
        calculator = new PuntuacionCalculator();
    }

    // ============================================================
    // Calculo de puntos por posicion (estado=ok)
    // ============================================================

    @Test
    @DisplayName("Posicion 1 sin records devuelve 25 puntos")
    void calcular_posicion1SinRecords_devuelve25() {
        ResultadoDto r = resultadoOk(1, false, false);
        assertEquals(25, calculator.calcular(r));
    }

    @Test
    @DisplayName("Posicion 1 con record personal devuelve 30 puntos")
    void calcular_posicion1ConRecordPersonal_devuelve30() {
        ResultadoDto r = resultadoOk(1, true, false);
        assertEquals(30, calculator.calcular(r));
    }

    @Test
    @DisplayName("Posicion 1 con record personal y mundial devuelve 50 puntos")
    void calcular_posicion1ConAmbosRecords_devuelve50() {
        ResultadoDto r = resultadoOk(1, true, true);
        assertEquals(50, calculator.calcular(r));
    }

    @Test
    @DisplayName("Posicion 2 devuelve 18 puntos")
    void calcular_posicion2_devuelve18() {
        assertEquals(18, calculator.calcular(resultadoOk(2, false, false)));
    }

    @Test
    @DisplayName("Posicion 3 devuelve 15 puntos")
    void calcular_posicion3_devuelve15() {
        assertEquals(15, calculator.calcular(resultadoOk(3, false, false)));
    }

    @Test
    @DisplayName("Posicion 4 devuelve 12 puntos")
    void calcular_posicion4_devuelve12() {
        assertEquals(12, calculator.calcular(resultadoOk(4, false, false)));
    }

    @Test
    @DisplayName("Posicion 5 devuelve 10 puntos")
    void calcular_posicion5_devuelve10() {
        assertEquals(10, calculator.calcular(resultadoOk(5, false, false)));
    }

    @Test
    @DisplayName("Posicion 6 devuelve 8 puntos")
    void calcular_posicion6_devuelve8() {
        assertEquals(8, calculator.calcular(resultadoOk(6, false, false)));
    }

    @Test
    @DisplayName("Posicion 7 devuelve 6 puntos")
    void calcular_posicion7_devuelve6() {
        assertEquals(6, calculator.calcular(resultadoOk(7, false, false)));
    }

    @Test
    @DisplayName("Posicion 8 devuelve 4 puntos")
    void calcular_posicion8_devuelve4() {
        assertEquals(4, calculator.calcular(resultadoOk(8, false, false)));
    }

    @Test
    @DisplayName("Posicion 9 (fuera de top 8) devuelve 1 punto")
    void calcular_posicion9_devuelve1() {
        assertEquals(1, calculator.calcular(resultadoOk(9, false, false)));
    }

    @Test
    @DisplayName("Posicion 50 devuelve 1 punto")
    void calcular_posicion50_devuelve1() {
        assertEquals(1, calculator.calcular(resultadoOk(50, false, false)));
    }

    @Test
    @DisplayName("Posicion 5 con record personal devuelve 15 puntos")
    void calcular_posicion5ConRecordPersonal_devuelve15() {
        ResultadoDto r = resultadoOk(5, true, false);
        assertEquals(15, calculator.calcular(r));
    }

    @Test
    @DisplayName("Posicion 8 con record personal devuelve 9 puntos")
    void calcular_posicion8ConRecordPersonal_devuelve9() {
        ResultadoDto r = resultadoOk(8, true, false);
        assertEquals(9, calculator.calcular(r));
    }

    @Test
    @DisplayName("Posicion 50 con record personal devuelve 6 puntos")
    void calcular_posicion50ConRecordPersonal_devuelve6() {
        ResultadoDto r = resultadoOk(50, true, false);
        assertEquals(6, calculator.calcular(r));
    }

    // ============================================================
    // Estados especiales (sin posicion)
    // ============================================================

    @Test
    @DisplayName("Estado DNF aplica penalizacion de -3 puntos")
    void calcular_estadoDnf_devuelveMenos3() {
        ResultadoDto r = new ResultadoDto();
        r.setEstado("dnf");
        assertEquals(-3, calculator.calcular(r));
    }

    @Test
    @DisplayName("Estado DQ aplica penalizacion de -5 puntos")
    void calcular_estadoDq_devuelveMenos5() {
        ResultadoDto r = new ResultadoDto();
        r.setEstado("dq");
        assertEquals(-5, calculator.calcular(r));
    }

    @Test
    @DisplayName("Estado DNS no aplica puntuacion (0 puntos)")
    void calcular_estadoDns_devuelve0() {
        ResultadoDto r = new ResultadoDto();
        r.setEstado("dns");
        assertEquals(0, calculator.calcular(r));
    }

    @Test
    @DisplayName("Estado NM no aplica puntuacion (0 puntos)")
    void calcular_estadoNm_devuelve0() {
        ResultadoDto r = new ResultadoDto();
        r.setEstado("nm");
        assertEquals(0, calculator.calcular(r));
    }

    @Test
    @DisplayName("Estado RET no aplica puntuacion (0 puntos)")
    void calcular_estadoRet_devuelve0() {
        ResultadoDto r = new ResultadoDto();
        r.setEstado("ret");
        assertEquals(0, calculator.calcular(r));
    }

    // ============================================================
    // Robustez frente a entradas defectuosas
    // ============================================================

    @Test
    @DisplayName("Resultado nulo devuelve 0 puntos")
    void calcular_resultadoNulo_devuelve0() {
        assertEquals(0, calculator.calcular(null));
    }

    @Test
    @DisplayName("Estado nulo devuelve 0 puntos")
    void calcular_estadoNulo_devuelve0() {
        ResultadoDto r = new ResultadoDto();
        r.setEstado(null);
        assertEquals(0, calculator.calcular(r));
    }

    @Test
    @DisplayName("Estado ok con posicion nula devuelve 0 puntos por posicion")
    void calcular_okConPosicionNula_devuelve0() {
        ResultadoDto r = new ResultadoDto();
        r.setEstado("ok");
        r.setPosicion(null);
        assertEquals(0, calculator.calcular(r));
    }

    @Test
    @DisplayName("Estado en mayusculas se procesa correctamente")
    void calcular_estadoEnMayusculas_funciona() {
        ResultadoDto r = new ResultadoDto();
        r.setEstado("OK");
        r.setPosicion(1);
        assertEquals(25, calculator.calcular(r));
    }

    // ============================================================
    // Descripciones generadas
    // ============================================================

    @Test
    @DisplayName("Descripcion de resultado ok incluye posicion y prueba")
    void generarDescripcion_ok_incluyePosicionYPrueba() {
        ResultadoDto r = resultadoOk(1, false, false);
        r.setNombrePrueba("100m lisos");
        String desc = calculator.generarDescripcion(r);
        assertTrue(desc.contains("1"));
        assertTrue(desc.contains("100m lisos"));
    }

    @Test
    @DisplayName("Descripcion con record mundial incluye marca RM")
    void generarDescripcion_recordMundial_incluyeRM() {
        ResultadoDto r = resultadoOk(1, true, true);
        r.setNombrePrueba("800m");
        String desc = calculator.generarDescripcion(r);
        assertTrue(desc.contains("RM"));
    }

    @Test
    @DisplayName("Descripcion con solo record personal incluye marca RP")
    void generarDescripcion_recordPersonal_incluyeRP() {
        ResultadoDto r = resultadoOk(2, true, false);
        r.setNombrePrueba("800m");
        String desc = calculator.generarDescripcion(r);
        assertTrue(desc.contains("RP"));
    }

    @Test
    @DisplayName("Descripcion de DNF devuelve etiqueta del estado")
    void generarDescripcion_dnf_devuelveDNF() {
        ResultadoDto r = new ResultadoDto();
        r.setEstado("dnf");
        assertEquals("DNF", calculator.generarDescripcion(r));
    }

    // ============================================================
    // Metodo auxiliar
    // ============================================================

    /**
     * Crea un ResultadoDto valido (estado=ok) con los parametros indicados.
     * @param posicion posicion final
     * @param rp marca record personal
     * @param rm marca record mundial
     * @return resultado preparado para testear
     */
    private ResultadoDto resultadoOk(Integer posicion, boolean rp, boolean rm) {
        ResultadoDto r = new ResultadoDto();
        r.setEstado("ok");
        r.setPosicion(posicion);
        r.setRecordPersonal(rp);
        r.setRecordMundial(rm);
        return r;
    }
}
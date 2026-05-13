package com.bryan_raul_proyecto.atletismo_api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResultadoDto {

    private UUID resultadoId;
    private UUID atletaId;
    private String nombre;
    private String apellidos;
    private String prueba;
    private String competicion;
    private LocalDate fecha;
    private String lugar;
    private String tipoPista;
    private String estado;
    private BigDecimal marcaNum;
    private Integer posicion;
    private Boolean recordPersonal;
    private Boolean recordMundial;
}
package com.bryan_raul_proyecto.atletismo_api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

/**
 * DTO de la entidad Resultado.
 * Incluye los identificadores foraneos (necesarios para crear nuevos resultados)
 * y los campos derivados que se rellenan al hacer consultas con JOIN.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResultadoDto {

    // Campos propios del resultado
    private UUID id;
    private UUID atletaId;
    private UUID competicionId;
    private UUID pruebaId;
    private String estado;
    private BigDecimal marcaNum;
    private Integer posicion;
    private Boolean recordPersonal;
    private Boolean recordMundial;

    // Campos derivados (solo se rellenan en consultas con JOIN)
    private String nombreAtleta;
    private String apellidosAtleta;
    private String nombrePrueba;
    private String nombreCompeticion;
    private LocalDate fechaCompeticion;
    private String nombreLugar;
    private String tipoPista;
}
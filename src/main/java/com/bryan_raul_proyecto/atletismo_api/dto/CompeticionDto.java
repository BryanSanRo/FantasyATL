package com.bryan_raul_proyecto.atletismo_api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompeticionDto {

    private UUID id;
    private String nombre;
    private LocalDate fecha;
    private UUID lugarId;
    private String tipoPista;
    private String tipo;
    private Integer jornada;
}
package com.bryan_raul_proyecto.atletismo_api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AtletaDto {

    private UUID id;
    private String nombre;
    private String apellidos;
    private String nacionalidad;
    private String disciplina;
    private String categoria;
    private Integer valoracion;
    private Integer precio;
    private String imagenUrl;
}
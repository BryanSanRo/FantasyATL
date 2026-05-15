package com.bryan_raul_proyecto.atletismo_api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class LugarDto {

    private UUID id;
    private String nombre;
    private String ciudad;
    private String pais;
}
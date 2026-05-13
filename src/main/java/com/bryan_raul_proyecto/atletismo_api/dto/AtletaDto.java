package com.bryan_raul_proyecto.atletismo_api.dto;

import java.util.UUID;

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

    public AtletaDto() {
    }

    public AtletaDto(UUID id, String nombre, String apellidos, String nacionalidad,
                     String disciplina, String categoria, Integer valoracion,
                     Integer precio, String imagenUrl) {
        this.id = id;
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.nacionalidad = nacionalidad;
        this.disciplina = disciplina;
        this.categoria = categoria;
        this.valoracion = valoracion;
        this.precio = precio;
        this.imagenUrl = imagenUrl;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public String getNacionalidad() {
        return nacionalidad;
    }

    public void setNacionalidad(String nacionalidad) {
        this.nacionalidad = nacionalidad;
    }

    public String getDisciplina() {
        return disciplina;
    }

    public void setDisciplina(String disciplina) {
        this.disciplina = disciplina;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public Integer getValoracion() {
        return valoracion;
    }

    public void setValoracion(Integer valoracion) {
        this.valoracion = valoracion;
    }

    public Integer getPrecio() {
        return precio;
    }

    public void setPrecio(Integer precio) {
        this.precio = precio;
    }

    public String getImagenUrl() {
        return imagenUrl;
    }

    public void setImagenUrl(String imagenUrl) {
        this.imagenUrl = imagenUrl;
    }
}
package com.bryan_raul_proyecto.atletismo_api.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

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

    public ResultadoDto() {
    }

    public ResultadoDto(UUID resultadoId, UUID atletaId, String nombre, String apellidos,
                        String prueba, String competicion, LocalDate fecha, String lugar,
                        String tipoPista, String estado, BigDecimal marcaNum, Integer posicion,
                        Boolean recordPersonal, Boolean recordMundial) {
        this.resultadoId = resultadoId;
        this.atletaId = atletaId;
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.prueba = prueba;
        this.competicion = competicion;
        this.fecha = fecha;
        this.lugar = lugar;
        this.tipoPista = tipoPista;
        this.estado = estado;
        this.marcaNum = marcaNum;
        this.posicion = posicion;
        this.recordPersonal = recordPersonal;
        this.recordMundial = recordMundial;
    }

    public UUID getResultadoId() {
        return resultadoId;
    }

    public void setResultadoId(UUID resultadoId) {
        this.resultadoId = resultadoId;
    }

    public UUID getAtletaId() {
        return atletaId;
    }

    public void setAtletaId(UUID atletaId) {
        this.atletaId = atletaId;
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

    public String getPrueba() {
        return prueba;
    }

    public void setPrueba(String prueba) {
        this.prueba = prueba;
    }

    public String getCompeticion() {
        return competicion;
    }

    public void setCompeticion(String competicion) {
        this.competicion = competicion;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public String getLugar() {
        return lugar;
    }

    public void setLugar(String lugar) {
        this.lugar = lugar;
    }

    public String getTipoPista() {
        return tipoPista;
    }

    public void setTipoPista(String tipoPista) {
        this.tipoPista = tipoPista;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public BigDecimal getMarcaNum() {
        return marcaNum;
    }

    public void setMarcaNum(BigDecimal marcaNum) {
        this.marcaNum = marcaNum;
    }

    public Integer getPosicion() {
        return posicion;
    }

    public void setPosicion(Integer posicion) {
        this.posicion = posicion;
    }

    public Boolean getRecordPersonal() {
        return recordPersonal;
    }

    public void setRecordPersonal(Boolean recordPersonal) {
        this.recordPersonal = recordPersonal;
    }

    public Boolean getRecordMundial() {
        return recordMundial;
    }

    public void setRecordMundial(Boolean recordMundial) {
        this.recordMundial = recordMundial;
    }
}
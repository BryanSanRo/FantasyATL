package com.bryan_raul_proyecto.atletismo_api.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class ResultadoDto {

    private Long resultadoId;
    private Long atletaId;
    private String nombre;
    private String apellido;
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

    public ResultadoDto(Long resultadoId, Long atletaId, String nombre, String apellido,
                        String prueba, String competicion, LocalDate fecha, String lugar,
                        String tipoPista, String estado, BigDecimal marcaNum, Integer posicion,
                        Boolean recordPersonal, Boolean recordMundial) {
        this.resultadoId = resultadoId;
        this.atletaId = atletaId;
        this.nombre = nombre;
        this.apellido = apellido;
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

    public Long getResultadoId() {
        return resultadoId;
    }

    public void setResultadoId(Long resultadoId) {
        this.resultadoId = resultadoId;
    }

    public Long getAtletaId() {
        return atletaId;
    }

    public void setAtletaId(Long atletaId) {
        this.atletaId = atletaId;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
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
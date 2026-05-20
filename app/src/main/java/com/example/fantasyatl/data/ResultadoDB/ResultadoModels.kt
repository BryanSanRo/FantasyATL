package com.example.fantasyatl.data.ResultadoDB

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Resultado(
    @SerialName("id") val id: String,
    @SerialName("atleta_id") val atletaId: String,
    @SerialName("competicion_id") val competicionId: String,
    @SerialName("prueba_id") val pruebaId: String,
    @SerialName("estado") val estado: String,        // ok, dnf, dns, dq, nm
    @SerialName("marca_num") val marcaNum: Double? = null,
    @SerialName("posicion") val posicion: Int? = null,
    @SerialName("record_personal") val recordPersonal: Boolean = false,
    @SerialName("record_mundial") val recordMundial: Boolean = false
)

// Vista enriquecida para mostrar en pantalla
data class ResultadoConNombre(
    val resultado: Resultado,
    val nombreAtleta: String,
    val nombrePrueba: String,
    val puntosFantasy: Int
)

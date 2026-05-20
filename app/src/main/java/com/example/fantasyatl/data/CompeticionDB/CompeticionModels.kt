package com.example.fantasyatl.data.CompeticionDB

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Competicion(
    @SerialName("id") val id: String,
    @SerialName("nombre") val nombre: String,
    @SerialName("fecha") val fecha: String,
    @SerialName("liga_id") val ligaId: String,
    @SerialName("lugar_id") val lugarId: String? = null,
    @SerialName("tipo_pista") val tipoPista: String = "AL",
    @SerialName("tipo") val tipo: String = "",
    @SerialName("jornada") val jornada: Int = 0
)

@Serializable
data class prueba(
    @SerialName("id") val id: String,
    @SerialName("codigo") val codigo: String,
    @SerialName("nombre") val nombre: String,
    @SerialName("sector") val sector: String,
    @SerialName("unidad") val unidad: String
)

@Serializable
data class lugar(
    @SerialName("id") val id: String,
    @SerialName("nombre") val nombre: String,
    @SerialName("ciudad") val ciudad: String,
    @SerialName("pais") val pais: String
)

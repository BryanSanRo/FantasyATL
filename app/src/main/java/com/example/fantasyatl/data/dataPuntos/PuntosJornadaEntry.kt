package com.example.fantasyatl.data.dataPuntos

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PuntosJornadaEntry(
    @SerialName("id") val id: String,
    @SerialName("liga_id") val ligaId: String,
    @SerialName("atleta_id") val atletaId: String,
    @SerialName("jornada") val jornada: Int,
    @SerialName("puntos") val puntos: Int,
    @SerialName("descripcion") val descripcion: String? = null
)
package com.example.fantasyatl.data.PuntosDB

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PuntosJornadaEntry(
    @SerialName("id")
    val id: String, // uuid en postgres se recibe como String en Kotlin

    @SerialName("liga_id")
    val ligaId: String,

    @SerialName("atleta_id")
    val atletaId: String,

    @SerialName("jornada")
    val jornada: Int, // int4 en postgres es Int en Kotlin

    @SerialName("puntos")
    val puntos: Int,

    @SerialName("descripcion")
    val descripcion: String? = null,

    @SerialName("created_at")
    val createdAt: String? = null
)
package com.example.fantasyatl.data

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class LigaUsuario(
    val id: String,
    @SerialName("liga_id") val ligaId: String,
    @SerialName("email_usuario") val emailUsuario: String,
    val puntos: Int,
    val presupuesto: Int
)
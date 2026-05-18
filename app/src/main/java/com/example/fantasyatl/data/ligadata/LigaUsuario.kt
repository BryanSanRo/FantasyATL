package com.example.fantasyatl.data.ligadata

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LigaUsuario(
    @SerialName("id") val id: String,
    @SerialName("liga_id") val ligaId: String,
    @SerialName("email_usuario") val emailUsuario: String,
    @SerialName("puntos") val puntos: Int = 0,
    @SerialName("presupuesto") val presupuesto: Long = 1000000
)
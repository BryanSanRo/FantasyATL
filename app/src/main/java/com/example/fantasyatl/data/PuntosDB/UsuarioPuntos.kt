package com.example.fantasyatl.data.PuntosDB

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UsuarioPuntos(
    @SerialName("atleta_id") val atletaId: String,
    @SerialName("liga_id") val ligaId: String,
    @SerialName("email_usuario") val emailUsuario: String
)
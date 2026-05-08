package com.example.fantasyatl.data
import kotlinx.serialization.Serializable

@Serializable
data class LigaUsuario(
    val id: String? = null,
    val liga_id: String,
    val email_usuario: String,
    val puntos: Int = 0,
    val presupuesto: Int = 15000000
)
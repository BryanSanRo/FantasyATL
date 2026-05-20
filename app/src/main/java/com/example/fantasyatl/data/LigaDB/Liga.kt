package com.example.fantasyatl.data.LigaDB

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Liga(
    @SerialName("id") val id: String,
    @SerialName("nombre") val nombre: String,
    @SerialName("codigo") val codigo: String,
    @SerialName("admin_email") val adminEmail: String,
    @SerialName("max_jugadores") val maxJugadores: Int = 20
)
package com.example.fantasyatl.data

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class Liga(
    val id: String,
    val nombre: String,
    val codigo: String,
    @SerialName("admin_email") val adminEmail: String,
    @SerialName("max_jugadores") val maxJugadores: Int = 10
)
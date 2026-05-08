package com.example.fantasyatl.data

import kotlinx.serialization.Serializable

@Serializable
data class Liga(
    val id: String? = null,
    val nombre: String,
    val codigo: String,
    val admin_email: String,
    val max_jugadores: Int = 10
)


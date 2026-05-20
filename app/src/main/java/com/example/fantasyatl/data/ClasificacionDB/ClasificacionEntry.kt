package com.example.fantasyatl.data.ClasificacionDB

import kotlinx.serialization.Serializable

@Serializable
data class ClasificacionEntry(
    val email: String,
    val nombre: String,
    val puntos: Int
)
package com.example.fantasyatl.data.PujaDB

import kotlinx.serialization.Serializable

@Serializable
data class PujaDB(
    val id: String? = null,
    val atleta_id: String,
    val email_usuario: String,
    val cantidad: Int
)
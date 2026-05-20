package com.example.fantasyatl.data.MercadoDB

import kotlinx.serialization.Serializable

@Serializable
data class FicharAtletaArgs(
    val p_email: String,
    val p_liga_id: String,
    val p_atleta_id: String,
    val p_precio: Int
)

@Serializable
data class MercadoLigaRow(
    val id: String? = null,
    val liga_id: String,
    val atleta_id: String,
    val disponible: Boolean
)
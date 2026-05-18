package com.example.fantasyatl.data.dataAtleta

import kotlinx.serialization.Serializable

@Serializable
data class Atleta(
    val id: String? = null,
    val nombre: String,
    val apellidos: String,
    val nacionalidad: String? = null,
    val disciplina: String,
    val categoria: String,
    val valoracion: Int,
    val precio: Int,
    val imagen_url: String? = null
)
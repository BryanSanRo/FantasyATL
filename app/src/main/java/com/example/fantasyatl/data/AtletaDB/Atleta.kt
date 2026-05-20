package com.example.fantasyatl.data.AtletaDB

import kotlinx.serialization.Serializable

@Serializable
data class Atleta(
    val id: String? = null,
    val nombre: String = "",
    val apellidos: String = "",
    val nacionalidad: String = "",
    val disciplina: String = "",
    val categoria: String = "",
    val valoracion: Int = 0,
    val precio: Int = 0,
    val imagen_url: String? = null,
    // Campos locales auxiliares para la UI (no se guardan de forma fija en esta tabla)
    var contadorPujas: Int = 0,
    var miPujaActual: Int? = null
)
package com.example.fantasyatl.data

import kotlinx.serialization.Serializable

@Serializable
data class Usuario(
    val id: String? = null, // Obligatorio para UUID de Supabase
    val nombre: String,
    val apellidos: String,
    val email: String,
    val contrasena: String,
    val fecha_nacimiento: String? = null // Nullable por si hay registros incompletos
)
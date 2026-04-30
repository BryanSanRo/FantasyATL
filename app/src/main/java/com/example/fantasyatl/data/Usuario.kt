package com.example.fantasyatl.data
import kotlinx.serialization.Serializable
/**
 * Representa al usuario en nuestra aplicación.
 * Esta estructura debe coincidir con las columnas de tu tabla en PostgreSQL.
 */


@Serializable
data class Usuario(
    val nombre: String,
    val apellidos: String,
    val email: String,
    val contrasena: String,
    val fecha_nacimiento: String
)

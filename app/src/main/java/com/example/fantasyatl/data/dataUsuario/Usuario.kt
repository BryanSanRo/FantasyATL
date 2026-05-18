package com.example.fantasyatl.data.dataUsuario

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Usuario(
    @SerialName("id") val id: String? = null,
    @SerialName("nombre") val nombre: String,
    @SerialName("apellidos") val apellidos: String,
    @SerialName("email") val email: String,
    @SerialName("contrasena") val contrasena: String,
    @SerialName("fecha_nacimiento") val fecha_nacimiento: String? = null
)
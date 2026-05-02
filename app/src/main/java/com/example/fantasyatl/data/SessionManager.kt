package com.example.fantasyatl.data

object SessionManager {
    var usuarioActual: Usuario? = null

    fun cerrarSesion() {
        usuarioActual = null
    }

    fun estaLogueado(): Boolean = usuarioActual != null
}
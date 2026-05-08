package com.example.fantasyatl.data

object SessionManager {
    var usuarioActual: Usuario? = null
    var ligaActual: Liga? = null

    fun cerrarSesion() {
        usuarioActual = null
        ligaActual = null
    }

    fun estaLogueado(): Boolean = usuarioActual != null
    fun tieneLiga(): Boolean = ligaActual != null
}
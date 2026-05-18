package com.example.fantasyatl.data.dataSession

import com.example.fantasyatl.data.Liga
import com.example.fantasyatl.data.dataUsuario.Usuario


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
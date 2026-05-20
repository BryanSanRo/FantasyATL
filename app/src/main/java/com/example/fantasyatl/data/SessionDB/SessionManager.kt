package com.example.fantasyatl.data.SessionDB


import com.example.fantasyatl.data.LigaDB.Liga
import com.example.fantasyatl.data.UsuarioDB.Usuario


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
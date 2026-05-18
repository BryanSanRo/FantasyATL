package com.example.fantasyatl.ui.liga

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fantasyatl.data.Liga
import com.example.fantasyatl.data.LigaUsuario
import com.example.fantasyatl.data.SessionManager
import com.example.fantasyatl.data.SupabaseClient
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.launch
import java.util.UUID
import kotlin.random.Random

class LigaViewModel : ViewModel() {

    var nombreLiga = mutableStateOf("")
    var codigoLiga = mutableStateOf("")

    var isLoading = mutableStateOf(false)
    var errorMessage = mutableStateOf<String?>(null)

    var ligaActual = mutableStateOf<Liga?>(null)

    private fun generarCodigoUnico(): String {
        val caracteres = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
        return (1..6)
            .map { Random.nextInt(0, caracteres.length) }
            .map { caracteres[it] }
            .joinToString("")
    }

    fun crearLiga(onSuccess: () -> Unit) {
        val nombre = nombreLiga.value.trim()
        val emailDeAdmin = SessionManager.usuarioActual?.email

        if (nombre.isEmpty()) {
            errorMessage.value = "El nombre de la liga no puede estar vacío"
            return
        }
        if (emailDeAdmin == null) {
            errorMessage.value = "Error de sesión: No se detectó un usuario activo"
            return
        }

        isLoading.value = true
        errorMessage.value = null

        viewModelScope.launch {
            try {
                val nuevoLigaId = UUID.randomUUID().toString()
                val nuevoMembresiaId = UUID.randomUUID().toString()
                val codigoGenerado = generarCodigoUnico()

                val nuevaLiga = Liga(
                    id = nuevoLigaId,
                    nombre = nombre,
                    codigo = codigoGenerado,
                    adminEmail = emailDeAdmin,
                    maxJugadores = 20
                )

                SupabaseClient.client.from("ligas").insert(nuevaLiga)

                val membresiaAdmin = LigaUsuario(
                    id = nuevoMembresiaId,
                    ligaId = nuevoLigaId,
                    emailUsuario = emailDeAdmin,
                    puntos = 0,
                    presupuesto = 1000000
                )

                SupabaseClient.client.from("liga_usuarios").insert(membresiaAdmin)

                SessionManager.ligaActual = nuevaLiga
                ligaActual.value = nuevaLiga

                onSuccess()
            } catch (e: Exception) {
                e.printStackTrace()
                errorMessage.value = "No se pudo registrar la liga en la base de datos"
            } finally {
                isLoading.value = false
            }
        }
    }

    fun unirseALiga(onSuccess: () -> Unit) {
        val codigo = codigoLiga.value.trim().uppercase()
        val userEmail = SessionManager.usuarioActual?.email

        if (codigo.isEmpty()) {
            errorMessage.value = "Introduce el código de la liga"
            return
        }
        if (userEmail == null) {
            errorMessage.value = "Usuario no autenticado"
            return
        }

        isLoading.value = true
        errorMessage.value = null

        viewModelScope.launch {
            try {
                val ligaEncontrada = SupabaseClient.client.from("ligas")
                    .select {
                        filter { eq("codigo", codigo) }
                    }.decodeSingleOrNull<Liga>()

                if (ligaEncontrada == null) {
                    errorMessage.value = "El código de liga introducido no existe"
                    return@launch
                }

                val miembroExistente = SupabaseClient.client.from("liga_usuarios")
                    .select {
                        filter {
                            eq("liga_id", ligaEncontrada.id)
                            eq("email_usuario", userEmail)
                        }
                    }.decodeSingleOrNull<LigaUsuario>()

                if (miembroExistente != null) {
                    errorMessage.value = "Ya formas parte de esta liga"
                    return@launch
                }

                val nuevaMembresia = LigaUsuario(
                    id = UUID.randomUUID().toString(),
                    ligaId = ligaEncontrada.id,
                    emailUsuario = userEmail,
                    puntos = 0,
                    presupuesto = 1000000
                )
                SupabaseClient.client.from("liga_usuarios").insert(nuevaMembresia)

                SessionManager.ligaActual = ligaEncontrada
                ligaActual.value = ligaEncontrada

                onSuccess()
            } catch (e: Exception) {
                e.printStackTrace()
                errorMessage.value = "Error al unirse a la liga"
            } finally {
                isLoading.value = false
            }
        }
    }
}
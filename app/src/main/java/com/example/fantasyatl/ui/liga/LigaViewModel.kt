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

class LigaViewModel : ViewModel() {

    var ligaActual = mutableStateOf<Liga?>(null)
    var isLoading = mutableStateOf(false)
    var errorGeneral = mutableStateOf<String?>(null)
    var exitoso = mutableStateOf<String?>(null)

    // Campos crear liga
    var nombreLiga = mutableStateOf("")
    var nombreLigaError = mutableStateOf<String?>(null)

    // Campos unirse
    var codigoUnirse = mutableStateOf("")
    var codigoError = mutableStateOf<String?>(null)

    // -------------------------------------------------------
    // Comprobar si el usuario ya tiene liga
    // -------------------------------------------------------
    fun comprobarLiga() {
        val email = SessionManager.usuarioActual?.email ?: return
        viewModelScope.launch {
            isLoading.value = true
            try {
                // 1. Buscamos si existe una relación en liga_usuarios
                val membresia = SupabaseClient.client.from("liga_usuarios")
                    .select {
                        filter { eq("email_usuario", email) }
                    }.decodeSingleOrNull<LigaUsuario>()

                if (membresia != null) {
                    // 2. Si existe, obtenemos los datos completos de esa liga
                    val liga = SupabaseClient.client.from("ligas")
                        .select {
                            filter { eq("id", membresia.liga_id) }
                        }.decodeSingleOrNull<Liga>()

                    ligaActual.value = liga
                    // CORRECCIÓN: Actualizamos el SessionManager para que el resto de la app sepa la liga
                    SessionManager.ligaActual = liga
                } else {
                    ligaActual.value = null
                    SessionManager.ligaActual = null
                }
            } catch (e: Exception) {
                errorGeneral.value = "Error al comprobar la liga"
            } finally {
                isLoading.value = false
            }
        }
        // ERROR ELIMINADO: SessionManager.ligaActual = liga (estaba fuera del scope y mal referenciado)
    }

    // -------------------------------------------------------
    // Crear liga
    // -------------------------------------------------------
    fun crearLiga() {
        if (nombreLiga.value.isBlank()) {
            nombreLigaError.value = "El nombre es obligatorio"
            return
        }
        val email = SessionManager.usuarioActual?.email ?: return

        viewModelScope.launch {
            isLoading.value = true
            errorGeneral.value = null
            try {
                // Generar código único de 6 letras
                val codigo = (1..6)
                    .map { ('A'..'Z').random() }
                    .joinToString("")

                val nuevaLiga = Liga(
                    nombre = nombreLiga.value,
                    codigo = codigo,
                    admin_email = email
                )

                // Insertar liga y obtener el objeto creado (incluyendo el ID generado)
                val ligaCreada = SupabaseClient.client.from("ligas")
                    .insert(nuevaLiga) { select() }
                    .decodeSingle<Liga>()

                // Unir al creador automáticamente a la tabla intermedia
                SupabaseClient.client.from("liga_usuarios").insert(
                    LigaUsuario(
                        liga_id = ligaCreada.id!!,
                        email_usuario = email
                    )
                )

                ligaActual.value = ligaCreada
                SessionManager.ligaActual = ligaCreada // Sincronizamos sesión
                exitoso.value = "Liga creada. Código: ${ligaCreada.codigo}"

            } catch (e: Exception) {
                errorGeneral.value = "Error al crear la liga"
            } finally {
                isLoading.value = false
            }
        }
    }

    // -------------------------------------------------------
    // Unirse a liga por código
    // -------------------------------------------------------
    fun unirseALiga() {
        if (codigoUnirse.value.isBlank()) {
            codigoError.value = "Introduce el código"
            return
        }
        val email = SessionManager.usuarioActual?.email ?: return

        viewModelScope.launch {
            isLoading.value = true
            errorGeneral.value = null
            codigoError.value = null
            try {
                val liga = SupabaseClient.client.from("ligas")
                    .select {
                        filter { eq("codigo", codigoUnirse.value.uppercase().trim()) }
                    }.decodeSingleOrNull<Liga>()

                if (liga == null) {
                    codigoError.value = "Código incorrecto"
                    return@launch
                }

                // Comprobar si ya está en la liga
                val yaEsMiembro = SupabaseClient.client.from("liga_usuarios")
                    .select {
                        filter {
                            eq("liga_id", liga.id!!)
                            eq("email_usuario", email)
                        }
                    }.decodeSingleOrNull<LigaUsuario>()

                if (yaEsMiembro != null) {
                    codigoError.value = "Ya eres miembro de esta liga"
                    return@launch
                }

                // Unirse insertando en liga_usuarios
                SupabaseClient.client.from("liga_usuarios").insert(
                    LigaUsuario(
                        liga_id = liga.id!!,
                        email_usuario = email
                    )
                )

                ligaActual.value = liga
                SessionManager.ligaActual = liga // Sincronizamos sesión
                exitoso.value = "Te has unido a ${liga.nombre}"

            } catch (e: Exception) {
                errorGeneral.value = "Error al unirse a la liga"
            } finally {
                isLoading.value = false
            }
        }
    }

    fun salirDeLiga() {
        val email = SessionManager.usuarioActual?.email ?: return
        val liga = ligaActual.value ?: return
        viewModelScope.launch {
            isLoading.value = true
            try {
                SupabaseClient.client.from("liga_usuarios").delete {
                    filter {
                        eq("liga_id", liga.id!!)
                        eq("email_usuario", email)
                    }
                }
                ligaActual.value = null
                SessionManager.ligaActual = null // Limpiamos sesión
            } catch (e: Exception) {
                errorGeneral.value = "Error al salir de la liga"
            } finally {
                isLoading.value = false
            }
        }
    }
}
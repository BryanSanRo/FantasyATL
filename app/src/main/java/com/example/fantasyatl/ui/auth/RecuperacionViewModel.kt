package com.example.fantasyatl.ui.auth

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fantasyatl.data.SessionManager
import com.example.fantasyatl.data.SupabaseClient
import com.example.fantasyatl.data.Usuario
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import org.mindrot.jbcrypt.BCrypt
import kotlin.random.Random

@Serializable
data class TokenRecuperacion(
    val email: String,
    val token: String,
    val usado: Boolean = false
)

class RecuperacionViewModel : ViewModel() {

    // --- RECUPERACIÓN ---
    var emailRecuperacion = mutableStateOf("")
    var emailError = mutableStateOf<String?>(null)
    var tokenIntroducido = mutableStateOf("")
    var tokenError = mutableStateOf<String?>(null)
    var nuevaPassword = mutableStateOf("")
    var nuevaPasswordError = mutableStateOf<String?>(null)
    var confirmarPassword = mutableStateOf("")
    var confirmarPasswordError = mutableStateOf<String?>(null)

    // --- CAMBIO DE DATOS EN PERFIL ---
    var nuevoNombre = mutableStateOf("")
    var nuevoApellidos = mutableStateOf("")
    var nuevoEmail = mutableStateOf("")
    var passwordActual = mutableStateOf("")
    var passwordActualError = mutableStateOf<String?>(null)

    // --- ESTADOS ---
    var isLoading = mutableStateOf(false)
    var paso = mutableStateOf(1)  // 1=email, 2=token, 3=nueva password
    var exitoso = mutableStateOf(false)
    var errorGeneral = mutableStateOf<String?>(null)
    var tokenGenerado = mutableStateOf("")  // Solo para simulación sin email real

    // -------------------------------------------------------
    // RECUPERACIÓN — PASO 1: Verificar email y generar token
    // -------------------------------------------------------
    fun solicitarRecuperacion() {
        if (emailRecuperacion.value.isBlank()) {
            emailError.value = "Introduce tu email"
            return
        }
        viewModelScope.launch {
            isLoading.value = true
            errorGeneral.value = null
            try {
                // Verificar que el email existe
                val usuario = SupabaseClient.client.from("usuarios")
                    .select { filter { eq("email", emailRecuperacion.value) } }
                    .decodeSingleOrNull<Usuario>()

                if (usuario == null) {
                    emailError.value = "Este email no está registrado"
                } else {
                    // Generar token de 6 dígitos
                    val token = Random.nextInt(100000, 999999).toString()
                    tokenGenerado.value = token  // En producción esto se enviaría por email

                    // Guardar token en BD
                    SupabaseClient.client.from("recuperacion_password").insert(
                        TokenRecuperacion(
                            email = emailRecuperacion.value,
                            token = token
                        )
                    )
                    paso.value = 2  // Ir al paso 2
                }
            } catch (e: Exception) {
                errorGeneral.value = "Error al procesar la solicitud"
            } finally {
                isLoading.value = false
            }
        }
    }

    // -------------------------------------------------------
    // RECUPERACIÓN — PASO 2: Verificar token
    // -------------------------------------------------------
    fun verificarToken() {
        if (tokenIntroducido.value.isBlank()) {
            tokenError.value = "Introduce el código"
            return
        }
        viewModelScope.launch {
            isLoading.value = true
            errorGeneral.value = null
            try {
                val tokenValido = SupabaseClient.client.from("recuperacion_password")
                    .select {
                        filter {
                            eq("email", emailRecuperacion.value)
                            eq("token", tokenIntroducido.value)
                            eq("usado", false)
                        }
                    }.decodeSingleOrNull<TokenRecuperacion>()

                if (tokenValido == null) {
                    tokenError.value = "Código incorrecto o expirado"
                } else {
                    paso.value = 3  // Ir al paso 3
                }
            } catch (e: Exception) {
                tokenError.value = "Error al verificar el código"
            } finally {
                isLoading.value = false
            }
        }
    }

    // -------------------------------------------------------
    // RECUPERACIÓN — PASO 3: Cambiar contraseña
    // -------------------------------------------------------
    fun cambiarPassword() {
        val passwordPattern = "^(?=.*[0-9])(?=.*[@#\$%^&+=!/¿?]).{8,}$".toRegex()
        if (!nuevaPassword.value.matches(passwordPattern)) {
            nuevaPasswordError.value = "Mínimo 8 caracteres, un número y un símbolo"
            return
        }
        if (nuevaPassword.value != confirmarPassword.value) {
            confirmarPasswordError.value = "Las contraseñas no coinciden"
            return
        }
        viewModelScope.launch {
            isLoading.value = true
            try {
                val hashNuevo = BCrypt.hashpw(nuevaPassword.value, BCrypt.gensalt())

                // Actualizar contraseña
                SupabaseClient.client.from("usuarios").update(
                    { set("contrasena", hashNuevo) }
                ) { filter { eq("email", emailRecuperacion.value) } }

                // Marcar token como usado
                SupabaseClient.client.from("recuperacion_password").update(
                    { set("usado", true) }
                ) {
                    filter {
                        eq("email", emailRecuperacion.value)
                        eq("token", tokenIntroducido.value)
                    }
                }

                exitoso.value = true
            } catch (e: Exception) {
                errorGeneral.value = "Error al cambiar la contraseña"
            } finally {
                isLoading.value = false
            }
        }
    }

    // -------------------------------------------------------
    // PERFIL — Actualizar nombre y apellidos
    // -------------------------------------------------------
    fun actualizarDatosPerfil(onExito: () -> Unit) {
        val usuario = SessionManager.usuarioActual ?: return
        viewModelScope.launch {
            isLoading.value = true
            errorGeneral.value = null
            try {
                SupabaseClient.client.from("usuarios").update(
                    {
                        if (nuevoNombre.value.isNotBlank()) set("nombre", nuevoNombre.value)
                        if (nuevoApellidos.value.isNotBlank()) set("apellidos", nuevoApellidos.value)
                    }
                ) { filter { eq("email", usuario.email) } }

                // Actualizar sesión local
                SessionManager.usuarioActual = usuario.copy(
                    nombre = nuevoNombre.value.ifBlank { usuario.nombre },
                    apellidos = nuevoApellidos.value.ifBlank { usuario.apellidos }
                )
                onExito()
            } catch (e: Exception) {
                errorGeneral.value = "Error al actualizar los datos"
            } finally {
                isLoading.value = false
            }
        }
    }

    // -------------------------------------------------------
    // PERFIL — Cambiar contraseña desde perfil
    // -------------------------------------------------------
    fun cambiarPasswordPerfil(onExito: () -> Unit) {
        val usuario = SessionManager.usuarioActual ?: return

        if (passwordActual.value.isBlank()) {
            passwordActualError.value = "Introduce tu contraseña actual"
            return
        }
        if (!BCrypt.checkpw(passwordActual.value, usuario.contrasena)) {
            passwordActualError.value = "Contraseña actual incorrecta"
            return
        }
        val passwordPattern = "^(?=.*[0-9])(?=.*[@#\$%^&+=!/¿?]).{8,}$".toRegex()
        if (!nuevaPassword.value.matches(passwordPattern)) {
            nuevaPasswordError.value = "Mínimo 8 caracteres, un número y un símbolo"
            return
        }
        if (nuevaPassword.value != confirmarPassword.value) {
            confirmarPasswordError.value = "Las contraseñas no coinciden"
            return
        }

        viewModelScope.launch {
            isLoading.value = true
            try {
                val hashNuevo = BCrypt.hashpw(nuevaPassword.value, BCrypt.gensalt())
                SupabaseClient.client.from("usuarios").update(
                    { set("contrasena", hashNuevo) }
                ) { filter { eq("email", usuario.email) } }

                SessionManager.usuarioActual = usuario.copy(contrasena = hashNuevo)
                onExito()
            } catch (e: Exception) {
                errorGeneral.value = "Error al cambiar la contraseña"
            } finally {
                isLoading.value = false
            }
        }
    }
}
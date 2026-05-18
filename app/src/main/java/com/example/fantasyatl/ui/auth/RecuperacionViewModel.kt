package com.example.fantasyatl.ui.auth

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fantasyatl.data.EmailService
import com.example.fantasyatl.data.SessionManager
import com.example.fantasyatl.data.SupabaseClient
import com.example.fantasyatl.data.Usuario
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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

    // Variable interna para congelar el email verificado con éxito y evitar pérdidas de estado
    private var emailVerificadoRespaldado: String = ""

    // --- PERFIL ---
    var nuevoNombre = mutableStateOf("")
    var nuevoApellidos = mutableStateOf("")
    var nuevoEmail = mutableStateOf("")
    var passwordActual = mutableStateOf("")
    var passwordActualError = mutableStateOf<String?>(null)

    // --- ESTADOS ---
    var isLoading = mutableStateOf(false)
    var paso = mutableStateOf(1)
    var exitoso = mutableStateOf(false)
    var errorGeneral = mutableStateOf<String?>(null)
    var intentosFallidos = mutableStateOf(0)
    var emailEnviado = mutableStateOf(false)

    fun cargarDatosUsuarioActual() {
        val usuario = SessionManager.usuarioActual ?: return
        nuevoNombre.value = usuario.nombre
        nuevoApellidos.value = usuario.apellidos
        nuevoEmail.value = usuario.email
    }

    fun resetear() {
        emailRecuperacion.value = ""
        emailError.value = null
        tokenIntroducido.value = ""
        tokenError.value = null
        nuevaPassword.value = ""
        nuevaPasswordError.value = null
        confirmarPassword.value = ""
        confirmarPasswordError.value = null
        paso.value = 1
        exitoso.value = false
        errorGeneral.value = null
        intentosFallidos.value = 0
        emailEnviado.value = false
        isLoading.value = false
        emailVerificadoRespaldado = ""
    }

    // -------------------------------------------------------
    // PASO 1 — Verificar email y enviar código
    // -------------------------------------------------------
    fun solicitarRecuperacion() {
        if (emailRecuperacion.value.isBlank()) {
            emailError.value = "Introduce tu email"
            return
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(emailRecuperacion.value).matches()) {
            emailError.value = "Email no válido"
            return
        }

        viewModelScope.launch {
            isLoading.value = true
            errorGeneral.value = null
            emailError.value = null

            val emailLimpio = emailRecuperacion.value.trim()

            try {
                withContext(Dispatchers.IO) {
                    val usuario = SupabaseClient.client.from("usuarios")
                        .select { filter { eq("email", emailLimpio) } }
                        .decodeSingleOrNull<Usuario>()

                    if (usuario == null) {
                        withContext(Dispatchers.Main) {
                            emailError.value = "Si el email existe, recibirás un código"
                        }
                        return@withContext
                    }

                    val token = Random.nextInt(100000, 999999).toString()

                    SupabaseClient.client.from("recuperacion_password").delete {
                        filter { eq("email", emailLimpio) }
                    }

                    SupabaseClient.client.from("recuperacion_password").insert(
                        TokenRecuperacion(
                            email = emailLimpio,
                            token = token
                        )
                    )

                    val enviado = EmailService.enviarCodigoRecuperacion(
                        emailDestino = emailLimpio,
                        codigo = token,
                        nombreUsuario = usuario.nombre
                    )

                    withContext(Dispatchers.Main) {
                        if (enviado) {
                            emailVerificadoRespaldado = emailLimpio // Respaldamos el correo procesado
                            emailEnviado.value = true
                            intentosFallidos.value = 0
                            paso.value = 2
                        } else {
                            errorGeneral.value = "Error al enviar el email. Verifica tus credenciales de Resend."
                        }
                    }
                }
            } catch (e: Exception) {
                errorGeneral.value = "Error de red en el emulador: ${e.localizedMessage}"
            } finally {
                isLoading.value = false
            }
        }
    }

    // -------------------------------------------------------
    // PASO 2 — Verificar token (CORREGIDO)
    // -------------------------------------------------------
    fun verificarToken() {
        if (tokenIntroducido.value.isBlank()) {
            tokenError.value = "Introduce el código"
            return
        }
        if (intentosFallidos.value >= 5) {
            tokenError.value = "Demasiados intentos. Solicita un nuevo código"
            return
        }

        // Recuperamos el correo respaldado; si fallara por desincronización, usamos el del campo de texto
        val correoABuscar = emailVerificadoRespaldado.ifBlank { emailRecuperacion.value.trim() }

        viewModelScope.launch {
            isLoading.value = true
            errorGeneral.value = null
            tokenError.value = null
            try {
                withContext(Dispatchers.IO) {
                    val tokenValido = SupabaseClient.client.from("recuperacion_password")
                        .select {
                            filter {
                                eq("email", correoABuscar)
                                eq("token", tokenIntroducido.value.trim())
                                eq("usado", false)
                            }
                        }.decodeSingleOrNull<TokenRecuperacion>()

                    withContext(Dispatchers.Main) {
                        if (tokenValido == null) {
                            intentosFallidos.value++
                            val restantes = 5 - intentosFallidos.value
                            tokenError.value = if (restantes > 0)
                                "Código incorrecto. Te quedan $restantes intentos"
                            else
                                "Sin intentos. Solicita un nuevo código"
                        } else {
                            emailVerificadoRespaldado = correoABuscar // Aseguramos el correo para el paso 3
                            intentosFallidos.value = 0
                            tokenError.value = null
                            paso.value = 3 // Avanza al cambio de contraseña sin problemas
                        }
                    }
                }
            } catch (e: Exception) {
                tokenError.value = "Error al conectar con el servidor"
            } finally {
                isLoading.value = false
            }
        }
    }

    // -------------------------------------------------------
    // PASO 3 — Nueva contraseña (CORREGIDO)
    // -------------------------------------------------------
    fun cambiarPassword() {
        // Tu patrón de seguridad de contraseña
        val passwordPattern = "^(?=.*[0-9])(?=.*[@#\$%^&+=!/¿?]).{8,}$".toRegex()

        if (!nuevaPassword.value.matches(passwordPattern)) {
            nuevaPasswordError.value = "Mínimo 8 caracteres, un número y un símbolo"
            return
        }
        if (nuevaPassword.value != confirmarPassword.value) {
            confirmarPasswordError.value = "Las contraseñas no coinciden"
            return
        }

        // Recuperamos el correo respaldado de los pasos anteriores
        val correoAActualizar = emailVerificadoRespaldado.ifBlank { emailRecuperacion.value.trim() }

        viewModelScope.launch {
            isLoading.value = true
            errorGeneral.value = null
            nuevaPasswordError.value = null
            confirmarPasswordError.value = null

            try {
                withContext(Dispatchers.IO) {
                    // 1. Encriptamos la nueva contraseña con BCrypt
                    val hashNuevo = BCrypt.hashpw(nuevaPassword.value, BCrypt.gensalt())

                    // 2. Actualizamos la contraseña en la tabla 'usuarios' (Sintaxis corregida para Supabase-kt)
                    SupabaseClient.client.from("usuarios").update(
                        {
                            set("contrasena", hashNuevo)
                        }
                    ) {
                        filter {
                            eq("email", correoAActualizar)
                        }
                    }

                    // 3. Marcamos el token como usado en la tabla 'recuperacion_password'
                    SupabaseClient.client.from("recuperacion_password").update(
                        {
                            set("usado", true)
                        }
                    ) {
                        filter {
                            eq("email", correoAActualizar)
                            eq("token", tokenIntroducido.value.trim())
                        }
                    }

                    SessionManager.usuarioActual?.let { usuario ->
                        if (usuario.email.equals(correoAActualizar, ignoreCase = true)) {
                            SessionManager.usuarioActual = usuario.copy(contrasena = hashNuevo)
                        }
                    }

                    withContext(Dispatchers.Main) {
                        exitoso.value = true
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    errorGeneral.value = "Error al guardar la nueva contraseña en el servidor"
                }
            } finally {
                isLoading.value = false
            }
        }
    }

    // --- PERFIL ---
    fun actualizarDatosPerfil(onExito: () -> Unit) {
        val usuario = SessionManager.usuarioActual ?: return
        viewModelScope.launch {
            isLoading.value = true
            errorGeneral.value = null
            try {
                withContext(Dispatchers.IO) {
                    SupabaseClient.client.from("usuarios").update({
                        if (nuevoNombre.value.isNotBlank()) set("nombre", nuevoNombre.value.trim())
                        if (nuevoApellidos.value.isNotBlank()) set("apellidos", nuevoApellidos.value.trim())
                        if (nuevoEmail.value.isNotBlank() && nuevoEmail.value != usuario.email) set("email", nuevoEmail.value.trim())
                    }) { filter { eq("email", usuario.email) } }

                    SessionManager.usuarioActual = usuario.copy(
                        nombre = nuevoNombre.value.ifBlank { usuario.nombre }.trim(),
                        apellidos = nuevoApellidos.value.ifBlank { usuario.apellidos }.trim(),
                        email = nuevoEmail.value.ifBlank { usuario.email }.trim()
                    )
                    withContext(Dispatchers.Main) {
                        onExito()
                    }
                }
            } catch (e: Exception) {
                errorGeneral.value = "Error: ${e.javaClass.simpleName} - ${e.message?.take(100)}"
            } finally {
                isLoading.value = false
            }
        }
    }

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
                withContext(Dispatchers.IO) {
                    val hashNuevo = BCrypt.hashpw(nuevaPassword.value, BCrypt.gensalt())
                    SupabaseClient.client.from("usuarios").update(
                        { set("contrasena", hashNuevo) }
                    ) { filter { eq("email", usuario.email) } }

                    SessionManager.usuarioActual = usuario.copy(contrasena = hashNuevo)
                    withContext(Dispatchers.Main) {
                        onExito()
                    }
                }
            } catch (e: Exception) {
                errorGeneral.value = "Error al actualizar la contraseña"
            } finally {
                isLoading.value = false
            }
        }
    }
}
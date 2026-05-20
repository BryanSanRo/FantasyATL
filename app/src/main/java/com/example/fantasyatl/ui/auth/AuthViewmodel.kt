package com.example.fantasyatl.ui.auth

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fantasyatl.data.SessionDB.SessionManager
import com.example.fantasyatl.data.SessionDB.SupabaseClient
import com.example.fantasyatl.data.UsuarioDB.Usuario

import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.launch
import org.mindrot.jbcrypt.BCrypt

class AuthViewModel : ViewModel() {

    var username = mutableStateOf("")
    var usernameError = mutableStateOf<String?>(null)
    var nombre = mutableStateOf("")
    var apellidos = mutableStateOf("")
    var fechaNacimiento = mutableStateOf("")
    var email = mutableStateOf("")
    var password = mutableStateOf("")
    var confirmPassword = mutableStateOf("")

    var nombreError = mutableStateOf<String?>(null)
    var apellidosError = mutableStateOf<String?>(null)
    var fechaError = mutableStateOf<String?>(null)
    var passwordError = mutableStateOf<String?>(null)
    var confirmPasswordError = mutableStateOf<String?>(null)

    var isLoading = mutableStateOf(false)
    var loginExitoso = mutableStateOf(false)
    var registroExitoso = mutableStateOf(false)

    // Mensaje de error general (no ligado a un campo)
    var errorGeneral = mutableStateOf<String?>(null)

    fun validateLogin(): Boolean {
        var isValid = true

        // Validar Email independientemente
        if (username.value.isBlank()) {
            usernameError.value = "El email es obligatorio"
            isValid = false
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(username.value.trim()).matches()) {
            // 🟢 CORREGIDO: validar formato del email antes de hacer la query
            usernameError.value = "Email no válido"
            isValid = false
        } else {
            usernameError.value = null
        }

        // Validar Contraseña independientemente
        if (password.value.isBlank()) {
            passwordError.value = "La contraseña es obligatoria"
            isValid = false
        } else {
            passwordError.value = null
        }

        return isValid
    }

    fun validateRegister(): Boolean {
        var isValid = true
        if (nombre.value.isBlank()) {
            nombreError.value = "Obligatorio"; isValid = false
        } else nombreError.value = null
        if (apellidos.value.isBlank()) {
            apellidosError.value = "Obligatorio"; isValid = false
        } else apellidosError.value = null
        if (fechaNacimiento.value.isBlank()) {
            fechaError.value = "Introduce fecha"; isValid = false
        } else fechaError.value = null

        // 🟢 CORREGIDO: validar formato del email en el registro (antes no se validaba nada)
        if (email.value.isBlank()) {
            usernameError.value = "El email es obligatorio"
            isValid = false
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email.value.trim()).matches()) {
            usernameError.value = "Email no válido"
            isValid = false
        } else {
            usernameError.value = null
        }

        val passwordPattern = "^(?=.*[0-9])(?=.*[@#\$%^&+=!/¿?]).{8,}$".toRegex()
        if (!password.value.matches(passwordPattern)) {
            passwordError.value = "Mínimo 8 caracteres, un número y un símbolo"
            isValid = false
        } else passwordError.value = null

        if (confirmPassword.value != password.value) {
            confirmPasswordError.value = "Las contraseñas no coinciden"
            isValid = false
        } else confirmPasswordError.value = null

        return isValid
    }

    fun registrarUsuario() {
        if (!validateRegister()) return
        viewModelScope.launch {
            isLoading.value = true
            errorGeneral.value = null
            try {
                val contrasenaHasheada = BCrypt.hashpw(password.value, BCrypt.gensalt())
                val nuevoUsuario = Usuario(
                    nombre = nombre.value,
                    apellidos = apellidos.value,
                    email = email.value,
                    contrasena = contrasenaHasheada,
                    fecha_nacimiento = fechaNacimiento.value
                )
                SupabaseClient.client.from("usuarios").insert(nuevoUsuario)
                registroExitoso.value = true
            } catch (e: Exception) {
                errorGeneral.value = traducirError(e)
            } finally {
                isLoading.value = false
            }
        }
    }

    fun loginUsuario() {
        if (!validateLogin()) return
        viewModelScope.launch {
            isLoading.value = true
            errorGeneral.value = null
            try {
                val usuarioEncontrado = SupabaseClient.client.from("usuarios")
                    .select {
                        filter { eq("email", username.value) }
                    }.decodeSingleOrNull<Usuario>()

                when {
                    usuarioEncontrado == null ->
                        usernameError.value = "El usuario no existe"

                    !BCrypt.checkpw(password.value, usuarioEncontrado.contrasena) ->
                        passwordError.value = "Contraseña incorrecta"

                    else -> {
                        SessionManager.usuarioActual = usuarioEncontrado
                        loginExitoso.value = true
                    }
                }
            } catch (e: Exception) {
                errorGeneral.value = traducirError(e)
            } finally {
                isLoading.value = false
            }
        }
    }

    private fun traducirError(e: Exception): String {
        val mensaje = e.message ?: return "Error desconocido"
        return when {
            mensaje.contains("row-level security") -> "No tienes permiso para realizar esta acción"
            mensaje.contains("unique") ||
                    mensaje.contains("duplicate") -> "Este email ya está registrado"

            mensaje.contains("Unable to resolve host") ||
                    mensaje.contains("network") -> "Sin conexión a internet"

            mensaje.contains("timeout") -> "Conexión lenta, inténtalo de nuevo"
            mensaje.contains("unauthorized") ||
                    mensaje.contains("401") -> "Sesión expirada, vuelve a iniciar sesión"

            else -> "Error al conectar con el servidor"
        }
    }
}
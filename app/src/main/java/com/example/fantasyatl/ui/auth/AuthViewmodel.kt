package com.example.fantasyatl.ui.auth

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fantasyatl.data.SupabaseClient
import com.example.fantasyatl.data.Usuario
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

    fun validateLogin(): Boolean {
        var isValid = true
        if (username.value.isBlank()) {
            usernameError.value = "El email es obligatorio"
            isValid = false
        } else usernameError.value = null
        if (password.value.isBlank()) {
            passwordError.value = "La contraseña es obligatoria"
            isValid = false
        } else passwordError.value = null
        return isValid
    }

    fun validateRegister(): Boolean {
        var isValid = true
        if (nombre.value.isBlank()) { nombreError.value = "Obligatorio"; isValid = false } else nombreError.value = null
        if (apellidos.value.isBlank()) { apellidosError.value = "Obligatorio"; isValid = false } else apellidosError.value = null
        if (fechaNacimiento.value.isBlank()) { fechaError.value = "Introduce fecha"; isValid = false } else fechaError.value = null

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
            try {
                // ✅ Hashear la contraseña antes de guardar
                val contrasenaHasheada = BCrypt.hashpw(password.value, BCrypt.gensalt())

                val nuevoUsuario = Usuario(
                    nombre = nombre.value,
                    apellidos = apellidos.value,
                    email = email.value,
                    contrasena = contrasenaHasheada,   // ✅ Nunca texto plano
                    fecha_nacimiento = fechaNacimiento.value
                )
                SupabaseClient.client.from("usuarios").insert(nuevoUsuario)
                println("✅ Usuario registrado")
                registroExitoso.value = true
            } catch (e: Exception) {
                println("❌ Error completo: ${e.message}")
                // ✅ Muestra el error real en vez de mensaje genérico
                nombreError.value = "Error: ${e.message}"

            } finally {
                isLoading.value = false
            }
        }
    }

    fun loginUsuario() {
        if (!validateLogin()) return
        viewModelScope.launch {
            isLoading.value = true
            try {
                val usuarioEncontrado = SupabaseClient.client.from("usuarios")
                    .select {
                        filter { eq("email", username.value) }
                    }.decodeSingleOrNull<Usuario>()

                when {
                    usuarioEncontrado == null -> {
                        usernameError.value = "El usuario no existe"
                    }
                    // ✅ BCrypt compara el password con el hash guardado
                    !BCrypt.checkpw(password.value, usuarioEncontrado.contrasena) -> {
                        passwordError.value = "Contraseña incorrecta"
                    }
                    else -> {
                        println("✅ Login exitoso: ${usuarioEncontrado.nombre}")
                        loginExitoso.value = true
                    }
                }
            } catch (e: Exception) {
                println("❌ Error de conexión: ${e.message}")
                usernameError.value = "Error al conectar"
            } finally {
                isLoading.value = false
            }
        }
    }
}
package com.example.fantasyatl.ui.liga

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fantasyatl.data.Liga
import com.example.fantasyatl.data.dataSession.SessionManager
import com.example.fantasyatl.data.dataSession.SupabaseClient
import com.example.fantasyatl.data.ligadata.LigaUsuario
import com.example.fantasyatl.data.dataPlantilla.PlantillaEntry
import com.example.fantasyatl.data.dataAtleta.Atleta
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.launch
import java.util.UUID

class LigaViewModel : ViewModel() {

    var nombreLiga = mutableStateOf("")
    var codigoLiga = mutableStateOf("")
    var errorMessage = mutableStateOf<String?>(null)
    var isLoading = mutableStateOf(false)

    fun crearLiga(onExito: () -> Unit) {
        val emailAdmin = SessionManager.usuarioActual?.email ?: run {
            errorMessage.value = "No hay un usuario activo en la sesión"
            return
        }

        val nombre = nombreLiga.value.trim()
        if (nombre.isBlank()) {
            errorMessage.value = "El nombre de la liga no puede estar vacío"
            return
        }

        viewModelScope.launch {
            isLoading.value = true
            errorMessage.value = null
            try {
                val nuevoIdLiga = UUID.randomUUID().toString()
                val codigoInvitacion = nuevoIdLiga.take(6).uppercase()

                val nuevaLiga = Liga(
                    id = nuevoIdLiga,
                    nombre = nombre,
                    codigo = codigoInvitacion,
                    adminEmail = emailAdmin
                )

                // 1. Insertar la nueva liga en Supabase
                SupabaseClient.client.from("ligas").insert(nuevaLiga)

                // 2. Crear la membresía con los 100 Millones iniciales
                val membresiaAdmin = LigaUsuario(
                    id = UUID.randomUUID().toString(),
                    ligaId = nuevoIdLiga,
                    emailUsuario = emailAdmin,
                    presupuesto = 100000000L
                )
                SupabaseClient.client.from("liga_usuarios").insert(membresiaAdmin)

                // 3. Entregar los 6 atletas de regalo para ESTA liga concreta
                asignarAtletasAleatorios(nuevoIdLiga, emailAdmin)

                // 4. Actualizar la sesión activa con la liga que se acaba de fabricar
                SessionManager.ligaActual = nuevaLiga
                onExito()

            } catch (e: Exception) {
                errorMessage.value = "Error al crear la liga: ${e.localizedMessage}"
            } finally {
                isLoading.value = false
            }
        }
    }

    fun unirseALigaPorCodigo(onExito: () -> Unit) {
        val email = SessionManager.usuarioActual?.email ?: return
        val codigo = codigoLiga.value.trim().uppercase()

        if (codigo.isBlank()) {
            errorMessage.value = "Introduce un código válido"
            return
        }

        viewModelScope.launch {
            isLoading.value = true
            errorMessage.value = null
            try {
                val ligaEncontrada = SupabaseClient.client.from("ligas")
                    .select { filter { eq("codigo", codigo) } }
                    .decodeSingleOrNull<Liga>()

                if (ligaEncontrada != null) {
                    val miembroExistente = SupabaseClient.client.from("liga_usuarios")
                        .select {
                            filter {
                                eq("email_usuario", email)
                                eq("liga_id", ligaEncontrada.id)
                            }
                        }.decodeSingleOrNull<LigaUsuario>()

                    if (miembroExistente == null) {
                        val nuevaMembresia = LigaUsuario(
                            id = UUID.randomUUID().toString(),
                            ligaId = ligaEncontrada.id,
                            emailUsuario = email,
                            presupuesto = 100000000L
                        )
                        SupabaseClient.client.from("liga_usuarios").insert(nuevaMembresia)

                        // Entregar los 6 atletas aleatorios al nuevo miembro
                        asignarAtletasAleatorios(ligaEncontrada.id, email)
                    }

                    // Establecer como liga activa actual de la sesión
                    SessionManager.ligaActual = ligaEncontrada
                    onExito()
                } else {
                    errorMessage.value = "El código de la liga no existe"
                }
            } catch (e: Exception) {
                errorMessage.value = "Error al unirse: ${e.localizedMessage}"
            } finally {
                isLoading.value = false
            }
        }
    }

    private suspend fun asignarAtletasAleatorios(ligaId: String, email: String) {
        try {
            val todosLosAtletas = SupabaseClient.client.from("atletas")
                .select().decodeList<Atleta>()

            if (todosLosAtletas.isNotEmpty()) {
                val seleccionados = todosLosAtletas.shuffled().take(6)

                seleccionados.forEachIndexed { index, atleta ->
                    val esTitular = index < 3 // 3 titulares y 3 suplentes

                    val nuevaEntrada = PlantillaEntry(
                        id = UUID.randomUUID().toString(),
                        ligaId = ligaId,
                        emailUsuario = email,
                        atletaId = atleta.id,
                        esTitular = esTitular
                    )
                    SupabaseClient.client.from("plantilla_usuario").insert(nuevaEntrada)
                }
            }
        } catch (e: Exception) {
            println("Error al repartir futbolistas: ${e.localizedMessage}")
        }
    }
}
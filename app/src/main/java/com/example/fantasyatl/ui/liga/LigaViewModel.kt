package com.example.fantasyatl.ui.liga

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fantasyatl.data.AtletaDB.Atleta
import com.example.fantasyatl.data.LigaDB.Liga
import com.example.fantasyatl.data.LigaDB.LigaUsuario
import com.example.fantasyatl.data.PlantillaDB.PlantillaEntry
import com.example.fantasyatl.data.SessionDB.SessionManager
import com.example.fantasyatl.data.SessionDB.SupabaseClient

import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.launch
import java.util.UUID

class LigaViewModel : ViewModel() {

    // Estados de UI
    var nombreLiga = mutableStateOf("")
    var codigoLiga = mutableStateOf("")
    var errorMessage = mutableStateOf<String?>(null)
    var isLoading = mutableStateOf(false)

    /**
     * CREAR LIGA
     */
    fun crearLiga(onExito: () -> Unit) {

        val emailAdmin = SessionManager.usuarioActual?.email ?: run {
            errorMessage.value = "No hay un usuario activo"
            return
        }

        val nombre = nombreLiga.value.trim()

        if (nombre.isBlank()) {
            errorMessage.value = "El nombre no puede estar vacío"
            return
        }

        viewModelScope.launch {
            isLoading.value = true
            errorMessage.value = null

            try {

                val nuevoIdLiga = UUID.randomUUID().toString()
                val codigoInvitacion =
                    nuevoIdLiga.take(6).uppercase()

                // 1️⃣ Crear liga
                val nuevaLiga = Liga(
                    id = nuevoIdLiga,
                    nombre = nombre,
                    codigo = codigoInvitacion,
                    adminEmail = emailAdmin
                )

                SupabaseClient.client
                    .from("ligas")
                    .insert(nuevaLiga)

                // 2️⃣ Crear membresía del admin
                val membresiaAdmin = LigaUsuario(
                    id = UUID.randomUUID().toString(),
                    ligaId = nuevoIdLiga,
                    emailUsuario = emailAdmin,
                    presupuesto = 100000000L
                )

                SupabaseClient.client
                    .from("liga_usuarios")
                    .insert(membresiaAdmin)

                // 3️⃣ Repartir atletas aleatorios
                asignarAtletasAleatorios(
                    ligaId = nuevoIdLiga,
                    email = emailAdmin
                )

                // 4️⃣ Guardar liga activa
                SessionManager.ligaActual =
                    nuevaLiga

                onExito()

            } catch (e: Exception) {
                errorMessage.value =
                    "Error al crear liga: ${e.localizedMessage}"
            } finally {
                isLoading.value = false
            }
        }
    }

    /**
     * UNIRSE A UNA LIGA
     */
    fun unirseALigaPorCodigo(
        codigoIgnorado: String = "",
        onExito: () -> Unit
    ) {

        val email =
            SessionManager.usuarioActual?.email ?: return

        val codigo =
            codigoLiga.value.trim().uppercase()

        if (codigo.isBlank()) {
            errorMessage.value =
                "Introduce un código válido"
            return
        }

        viewModelScope.launch {
            isLoading.value = true
            errorMessage.value = null

            try {

                // Buscar liga
                val ligaEncontrada =
                    SupabaseClient.client
                        .from("ligas")
                        .select {
                            filter {
                                eq("codigo", codigo)
                            }
                        }
                        .decodeSingleOrNull<Liga>()

                if (ligaEncontrada == null) {
                    errorMessage.value =
                        "Código no encontrado"
                    return@launch
                }

                // Verificar si ya es miembro
                val miembroExistente =
                    SupabaseClient.client
                        .from("liga_usuarios")
                        .select {
                            filter {
                                eq(
                                    "email_usuario",
                                    email
                                )
                                eq(
                                    "liga_id",
                                    ligaEncontrada.id
                                )
                            }
                        }
                        .decodeSingleOrNull<LigaUsuario>()

                // Si no es miembro, crearlo
                if (miembroExistente == null) {

                    val nuevaMembresia =
                        LigaUsuario(
                            id = UUID.randomUUID()
                                .toString(),
                            ligaId = ligaEncontrada.id,
                            emailUsuario = email,
                            presupuesto = 100000000L
                        )

                    SupabaseClient.client
                        .from("liga_usuarios")
                        .insert(nuevaMembresia)

                    // Repartir atletas iniciales
                    asignarAtletasAleatorios(
                        ligaId = ligaEncontrada.id,
                        email = email
                    )
                }

                // Guardar liga activa
                SessionManager.ligaActual =
                    ligaEncontrada

                onExito()

            } catch (e: Exception) {
                errorMessage.value =
                    "Error al unirse: ${e.localizedMessage}"
            } finally {
                isLoading.value = false
            }
        }
    }

    /**
     * REPARTIR ATLETAS ALEATORIOS
     *
     * - 6 atletas por usuario
     * - únicos dentro de la liga
     * - persistentes
     * - no se regeneran
     * - 3 titulares y 3 suplentes
     */
    private suspend fun asignarAtletasAleatorios(
        ligaId: String,
        email: String
    ) {

        try {

            // 1️⃣ Verificar si ya tiene plantilla
            val plantillaExistente =
                SupabaseClient.client
                    .from("plantilla_usuario")
                    .select {
                        filter {
                            eq("liga_id", ligaId)
                            eq(
                                "email_usuario",
                                email
                            )
                        }
                    }
                    .decodeList<PlantillaEntry>()

            // Si ya tiene equipo -> salir
            if (plantillaExistente.isNotEmpty()) {
                return
            }

            // 2️⃣ Obtener atletas ocupados
            val atletasOcupados =
                SupabaseClient.client
                    .from("plantilla_usuario")
                    .select {
                        filter {
                            eq("liga_id", ligaId)
                        }
                    }
                    .decodeList<PlantillaEntry>()
                    .mapNotNull {
                        it.atletaId
                    }

            // 3️⃣ Obtener TODOS los atletas
            val todosLosAtletas =
                SupabaseClient.client
                    .from("atletas")
                    .select()
                    .decodeList<Atleta>()

            // 4️⃣ Filtrar solo libres
            val atletasDisponibles =
                todosLosAtletas.filter {
                    it.id !in atletasOcupados
                }

            if (atletasDisponibles.size < 6) {

                errorMessage.value =
                    "No hay suficientes atletas disponibles"

                return
            }

            // 5️⃣ Elegir 6 aleatorios
            val seleccionados =
                atletasDisponibles
                    .shuffled()
                    .take(6)

            // 6️⃣ Guardarlos
            seleccionados.forEachIndexed { index, atleta ->

                val nuevaEntrada =
                    PlantillaEntry(
                        id = UUID.randomUUID()
                            .toString(),
                        ligaId = ligaId,
                        emailUsuario = email,
                        atletaId = atleta.id ?: "",
                        esTitular = index < 3
                    )

                SupabaseClient.client
                    .from("plantilla_usuario")
                    .insert(nuevaEntrada)
            }

        } catch (e: Exception) {

            errorMessage.value =
                "Error al repartir atletas: ${e.localizedMessage}"
        }
    }
}
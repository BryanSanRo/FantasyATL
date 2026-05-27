package com.example.fantasyatl.ui.liga

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fantasyatl.data.AtletaDB.Atleta
import com.example.fantasyatl.data.LigaDB.Liga
import com.example.fantasyatl.data.LigaDB.LigaUsuario
import com.example.fantasyatl.data.MercadoDB.MercadoLigaRow
import com.example.fantasyatl.data.PlantillaDB.PlantillaEntry
import com.example.fantasyatl.data.SessionDB.SessionManager
import com.example.fantasyatl.data.SessionDB.SupabaseClient
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.launch
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import java.util.UUID

class LigaViewModel : ViewModel() {

    var nombreLiga   = mutableStateOf("")
    var codigoLiga   = mutableStateOf("")
    var errorMessage = mutableStateOf<String?>(null)
    var isLoading    = mutableStateOf(false)

    // -------------------------------------------------------
    // CREAR LIGA
    // -------------------------------------------------------
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
            if (isLoading.value) return@launch  // ✅ evita doble ejecución
            isLoading.value = true
            errorMessage.value = null
            try {
                val nuevoIdLiga      = UUID.randomUUID().toString()
                val codigoInvitacion = nuevoIdLiga.take(6).uppercase()

                val nuevaLiga = Liga(
                    id         = nuevoIdLiga,
                    nombre     = nombre,
                    codigo     = codigoInvitacion,
                    adminEmail = emailAdmin
                )
                SupabaseClient.client.from("ligas").insert(nuevaLiga)

                val membresia = LigaUsuario(
                    id           = UUID.randomUUID().toString(),
                    ligaId       = nuevoIdLiga,
                    emailUsuario = emailAdmin,
                    presupuesto  = 100000000L
                )
                SupabaseClient.client.from("liga_usuarios").insert(membresia)

                // ✅ Poblar mercado_liga con TODOS los atletas disponibles
                poblarMercadoLiga(nuevoIdLiga)

                // ✅ Asignar 6 atletas iniciales y marcarlos como no disponibles
                asignarAtletasAleatorios(ligaId = nuevoIdLiga, email = emailAdmin)

                SessionManager.ligaActual = nuevaLiga
                onExito()

            } catch (e: Exception) {
                errorMessage.value = "Error al crear liga: ${e.localizedMessage}"
            } finally {
                isLoading.value = false
            }
        }
    }

    // -------------------------------------------------------
    // UNIRSE A LIGA
    // -------------------------------------------------------
    fun unirseALigaPorCodigo(onExito: () -> Unit) {
        val email  = SessionManager.usuarioActual?.email ?: return
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

                if (ligaEncontrada == null) {
                    errorMessage.value = "Código no encontrado"
                    return@launch
                }

                val yaEsMiembro = SupabaseClient.client.from("liga_usuarios")
                    .select {
                        filter {
                            eq("email_usuario", email)
                            eq("liga_id", ligaEncontrada.id)
                        }
                    }.decodeSingleOrNull<LigaUsuario>()

                if (yaEsMiembro == null) {
                    SupabaseClient.client.from("liga_usuarios").insert(
                        LigaUsuario(
                            id           = UUID.randomUUID().toString(),
                            ligaId       = ligaEncontrada.id,
                            emailUsuario = email,
                            presupuesto  = 100000000L
                        )
                    )

                    // ✅ Si el mercado está vacío para esta liga, poblarlo
                    val mercadoExistente = SupabaseClient.client.from("mercado_liga")
                        .select { filter { eq("liga_id", ligaEncontrada.id) } }
                        .decodeList<MercadoLigaRow>()

                    if (mercadoExistente.isEmpty()) {
                        poblarMercadoLiga(ligaEncontrada.id)
                    }

                    // ✅ Asignar 6 atletas iniciales
                    asignarAtletasAleatorios(ligaId = ligaEncontrada.id, email = email)
                }

                SessionManager.ligaActual = ligaEncontrada
                onExito()

            } catch (e: Exception) {
                errorMessage.value = "Error al unirse: ${e.localizedMessage}"
            } finally {
                isLoading.value = false
            }
        }
    }

    // -------------------------------------------------------
    // ✅ POBLAR MERCADO — inserta todos los atletas como disponibles
    // Se llama al crear una liga nueva
    // -------------------------------------------------------
    private suspend fun poblarMercadoLiga(ligaId: String) {
        val todosAtletas = SupabaseClient.client.from("atletas")
            .select().decodeList<Atleta>()

        todosAtletas.forEach { atleta ->
            val id = atleta.id ?: return@forEach
            SupabaseClient.client.from("mercado_liga").insert(
                MercadoLigaRow(
                    id         = UUID.randomUUID().toString(),
                    liga_id    = ligaId,
                    atleta_id  = id,
                    disponible = true
                )
            )
        }
    }

    // -------------------------------------------------------
    // ASIGNAR ATLETAS INICIALES
    // Inserta 6 atletas en plantilla y los marca como no disponibles en mercado
    // -------------------------------------------------------
    private suspend fun asignarAtletasAleatorios(ligaId: String, email: String) {
        try {
            // Salir si ya tiene plantilla
            val yaExiste = SupabaseClient.client.from("plantilla_usuario")
                .select {
                    filter {
                        eq("liga_id", ligaId)
                        eq("email_usuario", email)
                    }
                }.decodeList<PlantillaEntry>()

            if (yaExiste.isNotEmpty()) return

            // Atletas ya ocupados en esta liga
            val ocupados = SupabaseClient.client.from("plantilla_usuario")
                .select { filter { eq("liga_id", ligaId) } }
                .decodeList<PlantillaEntry>()
                .mapNotNull { it.atletaId }
                .toSet()

            // Atletas disponibles en mercado para esta liga
            val disponiblesEnMercado = SupabaseClient.client.from("mercado_liga")
                .select {
                    filter {
                        eq("liga_id", ligaId)
                        eq("disponible", true)
                    }
                }.decodeList<MercadoLigaRow>()
                .map { it.atleta_id }
                .toSet()

            // Todos los atletas libres
            val todosAtletas = SupabaseClient.client.from("atletas")
                .select().decodeList<Atleta>()

            val libres = todosAtletas.filter {
                it.id != null && it.id !in ocupados && it.id in disponiblesEnMercado
            }

            if (libres.size < 4) {
                errorMessage.value = "No hay suficientes atletas disponibles"
                return
            }

            // ✅ 4 atletas: 2 titulares + 2 suplentes
            val seleccionados = libres.shuffled().take(4)

            seleccionados.forEachIndexed { index, atleta ->
                val atletaId = atleta.id ?: return@forEachIndexed

                // ✅ Insertar en plantilla (índices 0,1 = titulares / 2,3 = suplentes)
                SupabaseClient.client.from("plantilla_usuario").insert(
                    PlantillaEntry(
                        id           = UUID.randomUUID().toString(),
                        ligaId       = ligaId,
                        emailUsuario = email,
                        atletaId     = atletaId,
                        esTitular    = index < 2
                    )
                )

                // ✅ Marcar como NO disponible en mercado
                SupabaseClient.client.from("mercado_liga").update(
                    buildJsonObject { put("disponible", false) }
                ) {
                    filter {
                        eq("liga_id", ligaId)
                        eq("atleta_id", atletaId)
                    }
                }
            }

        } catch (e: Exception) {
            errorMessage.value = "Error al repartir atletas: ${e.localizedMessage}"
        }
    }
}

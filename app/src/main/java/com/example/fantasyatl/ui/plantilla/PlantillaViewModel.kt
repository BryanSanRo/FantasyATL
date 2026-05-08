package com.example.fantasyatl.ui.plantilla

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fantasyatl.data.Atleta
import com.example.fantasyatl.data.SessionManager
import com.example.fantasyatl.data.SupabaseClient
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable

@Serializable
data class PlantillaEntry(
    val id: String? = null,
    val liga_id: String,
    val email_usuario: String,
    val atleta_id: String,
    val es_titular: Boolean = false
)

@Serializable
data class PlantillaConAtleta(
    val id: String? = null,
    val liga_id: String,
    val email_usuario: String,
    val atleta_id: String,
    val es_titular: Boolean = false,
    val atletas: Atleta? = null
)

class PlantillaViewModel : ViewModel() {

    var titulares = mutableStateOf<List<PlantillaConAtleta>>(emptyList())
    var suplentes = mutableStateOf<List<PlantillaConAtleta>>(emptyList())
    var isLoading = mutableStateOf(false)
    var error = mutableStateOf<String?>(null)
    var presupuesto = mutableStateOf(15000000)

    fun cargarPlantilla() {
        val email = SessionManager.usuarioActual?.email ?: return
        val ligaId = SessionManager.ligaActual?.id ?: return

        viewModelScope.launch {
            isLoading.value = true
            try {
                val todos = SupabaseClient.client.from("plantilla_usuario")
                    .select {
                        filter {
                            eq("email_usuario", email)
                            eq("liga_id", ligaId)
                        }
                    }.decodeList<PlantillaEntry>()

                // Cargar info del atleta para cada entrada
                val conAtletas = todos.map { entry ->
                    val atleta = SupabaseClient.client.from("atletas")
                        .select { filter { eq("id", entry.atleta_id) } }
                        .decodeSingleOrNull<Atleta>()
                    PlantillaConAtleta(
                        id = entry.id,
                        liga_id = entry.liga_id,
                        email_usuario = entry.email_usuario,
                        atleta_id = entry.atleta_id,
                        es_titular = entry.es_titular,
                        atletas = atleta
                    )
                }

                titulares.value = conAtletas.filter { it.es_titular }
                suplentes.value = conAtletas.filter { !it.es_titular }

                // Cargar presupuesto
                val membresia = SupabaseClient.client.from("liga_usuarios")
                    .select {
                        filter {
                            eq("email_usuario", email)
                            eq("liga_id", ligaId)
                        }
                    }.decodeSingleOrNull<com.example.fantasyatl.data.LigaUsuario>()
                presupuesto.value = membresia?.presupuesto ?: 15000000

            } catch (e: Exception) {
                error.value = "Error al cargar la plantilla"
            } finally {
                isLoading.value = false
            }
        }
    }

    fun ficharAtleta(atleta: Atleta, onExito: () -> Unit, onError: (String) -> Unit) {
        val email = SessionManager.usuarioActual?.email ?: return
        val ligaId = SessionManager.ligaActual?.id ?: return

        if (atleta.precio > presupuesto.value) {
            onError("Presupuesto insuficiente")
            return
        }

        val totalAtletas = titulares.value.size + suplentes.value.size
        if (totalAtletas >= 10) {
            onError("Plantilla completa (máx. 10 atletas)")
            return
        }

        viewModelScope.launch {
            isLoading.value = true
            try {
                // Añadir a plantilla
                SupabaseClient.client.from("plantilla_usuario").insert(
                    PlantillaEntry(
                        liga_id = ligaId,
                        email_usuario = email,
                        atleta_id = atleta.id!!,
                        es_titular = titulares.value.size < 6  // Primeros 6 son titulares
                    )
                )

                // Descontar presupuesto
                val nuevoPres = presupuesto.value - atleta.precio
                SupabaseClient.client.from("liga_usuarios").update(
                    { set("presupuesto", nuevoPres) }
                ) {
                    filter {
                        eq("email_usuario", email)
                        eq("liga_id", ligaId)
                    }
                }

                presupuesto.value = nuevoPres
                cargarPlantilla()
                onExito()

            } catch (e: Exception) {
                onError("Error al fichar atleta")
            } finally {
                isLoading.value = false
            }
        }
    }

    fun cambiarTitularidad(entry: PlantillaConAtleta) {
        viewModelScope.launch {
            try {
                SupabaseClient.client.from("plantilla_usuario").update(
                    { set("es_titular", !entry.es_titular) }
                ) { filter { eq("id", entry.id!!) } }
                cargarPlantilla()
            } catch (e: Exception) {
                error.value = "Error al cambiar titular"
            }
        }
    }

    fun venderAtleta(entry: PlantillaConAtleta) {
        val email = SessionManager.usuarioActual?.email ?: return
        val ligaId = SessionManager.ligaActual?.id ?: return
        val precio = entry.atletas?.precio ?: 0

        viewModelScope.launch {
            try {
                SupabaseClient.client.from("plantilla_usuario").delete {
                    filter { eq("id", entry.id!!) }
                }
                val nuevoPres = presupuesto.value + precio
                SupabaseClient.client.from("liga_usuarios").update(
                    { set("presupuesto", nuevoPres) }
                ) {
                    filter {
                        eq("email_usuario", email)
                        eq("liga_id", ligaId)
                    }
                }
                presupuesto.value = nuevoPres
                cargarPlantilla()
            } catch (e: Exception) {
                error.value = "Error al vender atleta"
            }
        }
    }
}
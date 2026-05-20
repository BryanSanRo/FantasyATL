package com.example.fantasyatl.ui.plantilla

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fantasyatl.data.AtletaDB.Atleta
import com.example.fantasyatl.data.LigaDB.LigaUsuario
import com.example.fantasyatl.data.PlantillaDB.PlantillaConAtleta
import com.example.fantasyatl.data.PlantillaDB.PlantillaEntry
import com.example.fantasyatl.data.SessionDB.SessionManager
import com.example.fantasyatl.data.SessionDB.SupabaseClient
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.launch
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

class PlantillaViewModel : ViewModel() {

    var titulares   = mutableStateOf<List<PlantillaConAtleta>>(emptyList())
    var suplentes   = mutableStateOf<List<PlantillaConAtleta>>(emptyList())
    var presupuesto = mutableStateOf(0L)
    var isLoading   = mutableStateOf(false)
    var error       = mutableStateOf<String?>(null)

    fun cargarPlantilla() {
        val email  = SessionManager.usuarioActual?.email ?: run {
            android.util.Log.d("PLANTILLA", "ERROR: usuarioActual null")
            return
        }
        val ligaId = SessionManager.ligaActual?.id ?: run {
            android.util.Log.d("PLANTILLA", "ERROR: ligaActual null")
            return
        }

        android.util.Log.d("PLANTILLA", "Cargando liga=$ligaId email=$email")

        viewModelScope.launch {
            isLoading.value = true
            error.value     = null
            try {
                // 1. Presupuesto
                val membresia = SupabaseClient.client.from("liga_usuarios")
                    .select {
                        filter {
                            eq("email_usuario", email)
                            eq("liga_id", ligaId)
                        }
                    }.decodeSingleOrNull<LigaUsuario>()

                presupuesto.value = membresia?.presupuesto ?: 100000000L

                // 2. Entradas de plantilla
                val todos = SupabaseClient.client.from("plantilla_usuario")
                    .select {
                        filter {
                            eq("email_usuario", email)
                            eq("liga_id", ligaId)
                        }
                    }.decodeList<PlantillaEntry>()

                android.util.Log.d("PLANTILLA", "Entradas: ${todos.size}")

                // 3. Enriquecer con datos del atleta
                val conAtletas = todos.map { entry ->
                    val idBuscar = entry.atletaId ?: ""
                    val atletaObj = if (idBuscar.isNotEmpty()) {
                        SupabaseClient.client.from("atletas")
                            .select { filter { eq("id", idBuscar) } }
                            .decodeSingleOrNull<Atleta>()
                    } else null

                    PlantillaConAtleta(
                        id           = entry.id,
                        ligaId       = entry.ligaId,
                        emailUsuario = entry.emailUsuario,
                        atletaId     = entry.atletaId,
                        esTitular    = entry.esTitular,
                        atleta       = atletaObj
                    )
                }

                titulares.value = conAtletas.filter { it.esTitular }
                suplentes.value = conAtletas.filter { !it.esTitular }

                android.util.Log.d("PLANTILLA", "Titulares=${titulares.value.size} Suplentes=${suplentes.value.size}")

            } catch (e: Exception) {
                error.value = "Error al cargar plantilla: ${e.localizedMessage}"
                android.util.Log.e("PLANTILLA", "Error: ${e.localizedMessage}")
            } finally {
                isLoading.value = false
            }
        }
    }

    fun venderAtleta(entry: PlantillaConAtleta) {
        val email      = SessionManager.usuarioActual?.email ?: return
        val ligaId     = SessionManager.ligaActual?.id ?: return
        val idPlantilla = entry.id ?: return
        val precioAtleta = entry.atleta?.precio?.toLong() ?: 0L

        viewModelScope.launch {
            isLoading.value = true
            try {
                SupabaseClient.client.from("plantilla_usuario").delete {
                    filter { eq("id", idPlantilla) }
                }

                val nuevoPresupuesto = presupuesto.value + precioAtleta
                SupabaseClient.client.from("liga_usuarios").update(
                    buildJsonObject { put("presupuesto", nuevoPresupuesto) }
                ) {
                    filter {
                        eq("email_usuario", email)
                        eq("liga_id", ligaId)
                    }
                }

                // Volver a poner disponible en mercado
                entry.atletaId?.let { atletaId ->
                    SupabaseClient.client.from("mercado_liga").update(
                        buildJsonObject { put("disponible", true) }
                    ) {
                        filter {
                            eq("liga_id", ligaId)
                            eq("atleta_id", atletaId)
                        }
                    }
                }

                cargarPlantilla()

            } catch (e: Exception) {
                error.value = "No se pudo completar la venta: ${e.localizedMessage}"
            } finally {
                isLoading.value = false
            }
        }
    }

    fun cambiarTitularidad(entry: PlantillaConAtleta) {
        val idPlantilla = entry.id ?: return
        viewModelScope.launch {
            isLoading.value = true
            try {
                SupabaseClient.client.from("plantilla_usuario").update(
                    buildJsonObject { put("es_titular", !entry.esTitular) }
                ) {
                    filter { eq("id", idPlantilla) }
                }
                cargarPlantilla()
            } catch (e: Exception) {
                error.value = "Error al cambiar titularidad: ${e.localizedMessage}"
            } finally {
                isLoading.value = false
            }
        }
    }
}
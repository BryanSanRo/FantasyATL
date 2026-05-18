package com.example.fantasyatl.ui.plantilla

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fantasyatl.data.dataSession.SessionManager
import com.example.fantasyatl.data.dataSession.SupabaseClient
import com.example.fantasyatl.data.dataPlantilla.PlantillaEntry
import com.example.fantasyatl.data.dataPlantilla.PlantillaConAtleta
import com.example.fantasyatl.data.dataAtleta.Atleta
import com.example.fantasyatl.data.ligadata.LigaUsuario
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.launch
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import java.util.UUID

class PlantillaViewModel : ViewModel() {

    var titulares = mutableStateOf<List<PlantillaConAtleta>>(emptyList())
    var suplentes = mutableStateOf<List<PlantillaConAtleta>>(emptyList())
    var presupuesto = mutableStateOf<Long>(0L)

    var isLoading = mutableStateOf(false)
    var error = mutableStateOf<String?>(null)

    fun cargarPlantilla() {
        val email = SessionManager.usuarioActual?.email ?: return
        val ligaId = SessionManager.ligaActual?.id ?: return

        viewModelScope.launch {
            isLoading.value = true
            error.value = null
            try {
                val membresia = SupabaseClient.client.from("liga_usuarios")
                    .select {
                        filter {
                            eq("email_usuario", email)
                            eq("liga_id", ligaId)
                        }
                    }.decodeSingleOrNull<LigaUsuario>()

                presupuesto.value = membresia?.presupuesto ?: 100000000L

                val todos = SupabaseClient.client.from("plantilla_usuario")
                    .select {
                        filter {
                            eq("email_usuario", email)
                            eq("liga_id", ligaId)
                        }
                    }.decodeList<PlantillaEntry>()

                val conAtletas = todos.map { entry ->
                    val idBuscar = entry.atletaId ?: ""

                    val atletaObj = if (idBuscar.isNotEmpty()) {
                        SupabaseClient.client.from("atletas")
                            .select { filter { eq("id", idBuscar) } }
                            .decodeSingleOrNull<Atleta>()
                    } else null

                    PlantillaConAtleta(
                        id = entry.id,
                        ligaId = entry.ligaId,
                        emailUsuario = entry.emailUsuario,
                        atletaId = entry.atletaId,
                        esTitular = entry.esTitular,
                        atleta = atletaObj
                    )
                }

                titulares.value = conAtletas.filter { it.esTitular }
                suplentes.value = conAtletas.filter { !it.esTitular }

            } catch (e: Exception) {
                error.value = "Error al cargar la plantilla: ${e.localizedMessage}"
            } finally {
                isLoading.value = false
            }
        }
    }

    /**
     * 267323f
     * 🟢 NUEVA FUNCIÓN: Permite comprar un Atleta del mercado, restar su precio
     * e insertarlo directamente como Suplente (esTitular = false) en la liga actual.
     */
    fun ficharAtleta(atleta: Atleta, onExito: () -> Unit, onError: (String) -> Unit) {
        val email = SessionManager.usuarioActual?.email ?: return
        val ligaId = SessionManager.ligaActual?.id ?: return

        // Validación de dinero local por seguridad
        if (presupuesto.value < atleta.precio.toLong()) {
            onError("No tienes suficiente presupuesto para fichar a este atleta")
            return
        }

        viewModelScope.launch {
            try {
                // 1. Insertar el nuevo Atleta en la plantilla del usuario como suplente
                val nuevaEntrada = PlantillaEntry(
                    id = UUID.randomUUID().toString(),
                    ligaId = ligaId,
                    emailUsuario = email,
                    atletaId = atleta.id,
                    esTitular = false // Entra al banquillo por defecto
                )
                SupabaseClient.client.from("plantilla_usuario").insert(nuevaEntrada)

                // 2. Restar el coste del presupuesto actual
                val nuevoPresupuesto = presupuesto.value - atleta.precio.toLong()

                // 3. Actualizar el saldo en Supabase de forma segura
                val updateData = buildJsonObject {
                    put("presupuesto", nuevoPresupuesto)
                }
                SupabaseClient.client.from("liga_usuarios").update(updateData) {
                    filter {
                        eq("email_usuario", email)
                        eq("liga_id", ligaId)
                    }
                }

                // 4. Forzar la recarga local de datos y avisar a la pantalla
                cargarPlantilla()
                onExito()

            } catch (e: Exception) {
                onError("Error al tramitar el fichaje: ${e.localizedMessage}")
            }
        }
    }

    fun venderAtleta(entry: PlantillaConAtleta) {
        val email = SessionManager.usuarioActual?.email ?: return
        val ligaId = SessionManager.ligaActual?.id ?: return
        val idPlantilla = entry.id ?: return
        val precioAtleta = entry.atleta?.precio ?: 0L

        viewModelScope.launch {
            isLoading.value = true
            try {
                SupabaseClient.client.from("plantilla_usuario").delete {
                    filter { eq("id", idPlantilla) }
                }

                val nuevoPresupuesto = presupuesto.value

                val updateData = buildJsonObject {
                    put("presupuesto", nuevoPresupuesto)
                }

                SupabaseClient.client.from("liga_usuarios").update(updateData) {
                    filter {
                        eq("email_usuario", email)
                        eq("liga_id", ligaId)
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
        val nuevoEstadoTitular = !entry.esTitular

        viewModelScope.launch {
            isLoading.value = true
            try {
                val updateData = buildJsonObject {
                    put("es_titular", nuevoEstadoTitular)
                }

                SupabaseClient.client.from("plantilla_usuario").update(updateData) {
                    filter { eq("id", idPlantilla) }
                }

                cargarPlantilla()

            } catch (e: Exception) {
                error.value = "Error al cambiar la alineación: ${e.localizedMessage}"
            } finally {
                isLoading.value = false
            }
        }
    }
}
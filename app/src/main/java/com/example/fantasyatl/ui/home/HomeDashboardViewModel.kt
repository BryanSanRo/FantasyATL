package com.example.fantasyatl.ui.home

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fantasyatl.data.LigaDB.Liga
import com.example.fantasyatl.data.LigaDB.LigaUsuario
import com.example.fantasyatl.data.SessionDB.SessionManager
import com.example.fantasyatl.data.SessionDB.SupabaseClient

import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.launch

class HomeDashboardViewModel : ViewModel() {

    var isLoading = mutableStateOf(false)
    var avisoError = mutableStateOf<String?>(null)
    var ligaNombre = mutableStateOf("Sin Liga")
    var posicionUsuario = mutableStateOf("-")
    var puntosUsuario = mutableStateOf(0)
    var misLigasDisponibles = mutableStateOf<List<Liga>>(emptyList())

    fun cargarDatosDashboardSilencioso() {
        val email = SessionManager.usuarioActual?.email ?: return
        viewModelScope.launch {
            try {
                val membresias = SupabaseClient.client
                    .from("liga_usuarios")
                    .select { filter { eq("email_usuario", email) } }
                    .decodeList<LigaUsuario>()

                val ids = membresias.map { it.ligaId }
                if (ids.isEmpty()) return@launch

                val ligasCompletas = SupabaseClient.client
                    .from("ligas")
                    .select { filter { isIn("id", ids) } }
                    .decodeList<Liga>()

                misLigasDisponibles.value = ligasCompletas

                val ligaActual = SessionManager.ligaActual ?: ligasCompletas.firstOrNull()
                if (ligaActual != null) {
                    SessionManager.ligaActual = ligaActual
                    ligaNombre.value = ligaActual.nombre
                    calcularClasificacion(ligaActual.id, email)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun cambiarDeLigaActiva(nuevaLiga: Liga, onCambioCompleto: () -> Unit = {}) {
        val email = SessionManager.usuarioActual?.email ?: return
        SessionManager.ligaActual = nuevaLiga
        ligaNombre.value = nuevaLiga.nombre
        viewModelScope.launch {
            try {
                calcularClasificacion(nuevaLiga.id, email)
                onCambioCompleto()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private suspend fun calcularClasificacion(ligaId: String, email: String) {
        val miembros = SupabaseClient.client
            .from("liga_usuarios")
            .select { filter { eq("liga_id", ligaId) } }
            .decodeList<LigaUsuario>()
            .sortedByDescending { it.puntos }

        val indice = miembros.indexOfFirst { it.emailUsuario == email }
        val misDatos = miembros.find { it.emailUsuario == email }

        posicionUsuario.value = if (indice != -1) "#${indice + 1}" else "-"
        puntosUsuario.value = misDatos?.puntos ?: 0
    }
}
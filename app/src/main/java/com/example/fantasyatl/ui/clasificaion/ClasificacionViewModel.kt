package com.example.fantasyatl.ui.clasificaion

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fantasyatl.data.dataClasificacion.ClasificacionEntry
import com.example.fantasyatl.data.ligadata.LigaUsuario
import com.example.fantasyatl.data.dataSession.SessionManager
import com.example.fantasyatl.data.dataSession.SupabaseClient
import com.example.fantasyatl.data.dataUsuario.Usuario
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.launch

class ClasificacionViewModel : ViewModel() {

    // Lista reactiva para pintar las posiciones en la UI
    var clasificacion = mutableStateOf<List<ClasificacionEntry>>(emptyList())
    var isLoading = mutableStateOf(false)

    fun cargarClasificacion() {
        val ligaId = SessionManager.ligaActual?.id ?: return

        isLoading.value = true
        viewModelScope.launch {
            try {
                // 1. Obtenemos todos los miembros de esta liga en Supabase
                val miembros = SupabaseClient.client.from("liga_usuarios")
                    .select {
                        filter { eq("liga_id", ligaId) }
                    }.decodeList<LigaUsuario>()

                // 2. Por cada miembro, buscamos su nombre y apellidos reales en la tabla 'usuarios'
                val entries = miembros.map { miembro ->
                    val usuario = SupabaseClient.client.from("usuarios")
                        .select {
                            filter { eq("email", miembro.emailUsuario) }
                        }.decodeSingleOrNull<Usuario>()

                    ClasificacionEntry(
                        email = miembro.emailUsuario,
                        nombre = "${usuario?.nombre ?: "Usuario"} ${usuario?.apellidos ?: ""}".trim(),
                        puntos = miembro.puntos
                    )
                }.sortedByDescending { it.puntos } // 🔥 Ordenamos de mayor a menor puntuación

                clasificacion.value = entries
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                isLoading.value = false
            }
        }
    }
}
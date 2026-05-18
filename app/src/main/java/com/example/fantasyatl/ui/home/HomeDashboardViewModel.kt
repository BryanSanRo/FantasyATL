package com.example.fantasyatl.ui.home

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fantasyatl.data.Liga
import com.example.fantasyatl.data.ligadata.LigaUsuario
import com.example.fantasyatl.data.dataSession.SessionManager
import com.example.fantasyatl.data.dataSession.SupabaseClient
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.launch

class HomeDashboardViewModel : ViewModel() {

    var isLoading = mutableStateOf(false)
    var avisoError = mutableStateOf<String?>(null)

    fun verificarYEntrarALigas(onSuccess: () -> Unit) {
        val email = SessionManager.usuarioActual?.email
        if (email == null) {
            avisoError.value = "Error: Sesión de usuario no detectada"
            return
        }

        isLoading.value = true
        avisoError.value = null

        viewModelScope.launch {
            try {
                val misLigas = SupabaseClient.client.from("liga_usuarios")
                    .select {
                        filter { eq("email_usuario", email) }
                    }.decodeList<LigaUsuario>()

                if (misLigas.isEmpty()) {
                    avisoError.value = "No tienes ninguna liga asociada. ¡Crea una o únete primero!"
                } else {
                    val primeraLiga = SupabaseClient.client.from("ligas")
                        .select {
                            filter { eq("id", misLigas.first().ligaId) }
                        }.decodeSingleOrNull<Liga>()

                    if (primeraLiga != null) {
                        SessionManager.ligaActual = primeraLiga
                    }
                    onSuccess()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                avisoError.value = "Error al conectar con la base de datos: ${e.localizedMessage}"
            } finally {
                isLoading.value = false
            }
        }
    }
}
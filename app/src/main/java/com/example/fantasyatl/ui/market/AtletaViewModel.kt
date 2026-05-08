package com.example.fantasyatl.ui.market

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fantasyatl.data.Atleta
import com.example.fantasyatl.data.SupabaseClient
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import kotlinx.coroutines.launch

class AtletaViewModel : ViewModel() {

    var atletasMercado = mutableStateOf<List<Atleta>>(emptyList())
    var atletasEquipo = mutableStateOf<List<Atleta>>(emptyList())
    var isLoading = mutableStateOf(false)
    var error = mutableStateOf<String?>(null)


    fun cargarMercado() {
        viewModelScope.launch {
            isLoading.value = true
            error.value = null
            try {
                val todos = SupabaseClient.client
                    .from("atletas")
                    .select()
                    .decodeList<Atleta>()

                // ✅ Aleatorios en el cliente
                atletasMercado.value = todos.shuffled().take(6)

            } catch (e: Exception) {
                error.value = "Error al cargar el mercado"
            } finally {
                isLoading.value = false
            }
        }
    }

    // ✅ Carga los atletas del equipo del usuario
    fun cargarEquipo(emailUsuario: String) {
        viewModelScope.launch {
            isLoading.value = true
            error.value = null
            try {
                atletasEquipo.value = SupabaseClient.client
                    .from("equipo_usuario")
                    .select(Columns.raw("atletas(*)"))  {
                        filter { eq("email_usuario", emailUsuario) }
                    }
                    .decodeList<Atleta>()
            } catch (e: Exception) {
                error.value = "Error al cargar el equipo"
            } finally {
                isLoading.value = false
            }
        }
    }
}
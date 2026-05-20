package com.example.fantasyatl.ui.market

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fantasyatl.data.AtletaDB.Atleta
import com.example.fantasyatl.data.MercadoDB.FicharAtletaArgs
import com.example.fantasyatl.data.MercadoDB.MercadoLigaRow
import com.example.fantasyatl.data.SessionDB.SupabaseClient
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.rpc
import kotlinx.coroutines.launch

class AtletaViewModel : ViewModel() {

    var atletasMercado = mutableStateOf<List<Atleta>>(emptyList())
    var isLoading      = mutableStateOf(false)
    var error          = mutableStateOf<String?>(null)

    fun cargarMercado(ligaId: String) {
        if (ligaId.isBlank()) return
        viewModelScope.launch {
            isLoading.value = true
            error.value = null
            try {
                // 1. Obtener ids disponibles en mercado_liga
                val mercado = SupabaseClient.client
                    .from("mercado_liga")
                    .select { filter {
                        eq("liga_id", ligaId)
                        eq("disponible", true)
                    }}
                    .decodeList<MercadoLigaRow>()

                val idsDisponibles = mercado.map { it.atleta_id }.toSet()

                if (idsDisponibles.isEmpty()) {
                    atletasMercado.value = emptyList()
                    return@launch
                }

                // 2. Obtener atletas y filtrar solo los disponibles
                val todosAtletas = SupabaseClient.client
                    .from("atletas")
                    .select()
                    .decodeList<Atleta>()

                atletasMercado.value = todosAtletas.filter { it.id in idsDisponibles }

            } catch (e: Exception) {
                error.value = "Error al cargar mercado: ${e.localizedMessage}"
            } finally {
                isLoading.value = false
            }
        }
    }

    fun ficharAtletaDirecto(
        emailUsuario: String,
        ligaId: String,
        atleta: Atleta,
        onExito: (String) -> Unit,
        onError: (String) -> Unit
    ) {
        val atletaId = atleta.id ?: run { onError("Atleta sin ID"); return }

        viewModelScope.launch {
            try {
                val args = FicharAtletaArgs(
                    p_email     = emailUsuario,
                    p_liga_id   = ligaId,
                    p_atleta_id = atletaId,
                    p_precio    = atleta.precio
                )

                SupabaseClient.client.postgrest.rpc(
                    function   = "fichar_atleta",
                    parameters = args
                )

                // ✅ Recargar mercado para que desaparezca el atleta fichado
                cargarMercado(ligaId)

                onExito("✅ Has fichado a ${atleta.nombre} ${atleta.apellidos} por ${"%,d".format(atleta.precio)} €")

            } catch (e: Exception) {
                val msg = e.localizedMessage ?: "Error desconocido"
                when {
                    msg.contains("Saldo insuficiente", true)   -> onError("Saldo insuficiente.")
                    msg.contains("Atleta no disponible", true) -> onError("Este atleta ya no está disponible.")
                    msg.contains("Atleta ya fichado", true)    -> onError("Este atleta ya está en tu plantilla.")
                    else                                        -> onError("Error: $msg")
                }
            }
        }
    }
}

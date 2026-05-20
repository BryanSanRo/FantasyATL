package com.example.fantasyatl.ui.competiciones


import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fantasyatl.data.AtletaDB.Atleta
import com.example.fantasyatl.data.CompeticionDB.Competicion
import com.example.fantasyatl.data.CompeticionDB.lugar
import com.example.fantasyatl.data.CompeticionDB.prueba
import com.example.fantasyatl.data.ResultadoDB.Resultado
import com.example.fantasyatl.data.ResultadoDB.ResultadoConNombre
import com.example.fantasyatl.data.SessionDB.SessionManager
import com.example.fantasyatl.data.SessionDB.SupabaseClient

import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CompeticionViewModel : ViewModel() {

    var competiciones = mutableStateOf<List<Competicion>>(emptyList())
    var lugares = mutableStateOf<Map<String, lugar>>(emptyMap())
    var pruebas = mutableStateOf<Map<String, prueba>>(emptyMap())
    var resultados = mutableStateOf<List<ResultadoConNombre>>(emptyList())

    var isLoading = mutableStateOf(false)
    var error = mutableStateOf<String?>(null)

    private fun hoyString(): String =
        SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

    fun cargarCompeticiones() {
        val ligaId = SessionManager.ligaActual?.id ?: run {
            error.value = "Error al cargar competiciones"
            return
        }
        viewModelScope.launch {
            isLoading.value = true
            error.value = null
            try {
                val listaLugares = SupabaseClient.client.from("lugares")
                    .select().decodeList<lugar>()
                lugares.value = listaLugares.associateBy { it.id }

                val listaPruebas = SupabaseClient.client.from("pruebas")
                    .select().decodeList<prueba>()
                pruebas.value = listaPruebas.associateBy { it.id }

                val lista = SupabaseClient.client.from("competiciones")
                    .select { filter { eq("liga_id", ligaId) } }
                    .decodeList<Competicion>()
                    .sortedBy { it.fecha }

                competiciones.value = lista

            } catch (e: Exception) {
                error.value = "Error al cargar competiciones"
            } finally {
                isLoading.value = false
            }
        }
    }

    fun cargarResultados(competicionId: String) {
        viewModelScope.launch {
            isLoading.value = true
            try {
                val listaResultados = SupabaseClient.client.from("resultados")
                    .select { filter { eq("competicion_id", competicionId) } }
                    .decodeList<Resultado>()
                    .sortedWith(compareBy({ it.posicion ?: 999 }, { it.estado }))

                val enriquecidos = listaResultados.map { res ->
                    val atleta = SupabaseClient.client.from("atletas")
                        .select { filter { eq("id", res.atletaId) } }
                        .decodeSingleOrNull<Atleta>()
                    val prueba = pruebas.value[res.pruebaId]
                    ResultadoConNombre(
                        resultado = res,
                        nombreAtleta = if (atleta != null) "${atleta.nombre} ${atleta.apellidos}" else "Atleta",
                        nombrePrueba = prueba?.nombre ?: "Prueba",
                        puntosFantasy = calcularPuntos(res.posicion)
                    )
                }
                resultados.value = enriquecidos
            } catch (e: Exception) {
                error.value = "Error al cargar resultados"
            } finally {
                isLoading.value = false
            }
        }
    }

    private fun calcularPuntos(posicion: Int?): Int = when (posicion) {
        1 -> 25; 2 -> 18; 3 -> 15; 4 -> 12; 5 -> 10
        6 -> 8; 7 -> 6; 8 -> 4; 9 -> 2; 10 -> 1
        else -> 0
    }

    fun proximasCompeticiones(): List<Competicion> =
        competiciones.value.filter { it.fecha >= hoyString() }.take(3)

    fun competicionesPasadas(): List<Competicion> =
        competiciones.value.filter { it.fecha < hoyString() }
}

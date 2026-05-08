package com.example.fantasyatl.ui.puntos

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.fantasyatl.data.SessionManager
import com.example.fantasyatl.data.SupabaseClient
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable

@Serializable
data class PuntoJornada(
    val id: String? = null,
    val liga_id: String,
    val atleta_id: String,
    val jornada: Int,
    val puntos: Int,
    val descripcion: String? = null
)

class PuntosViewModel : ViewModel() {
    var puntosTotales = mutableStateOf(0)
    var puntosDetalle = mutableStateOf<List<PuntoJornada>>(emptyList())
    var isLoading = mutableStateOf(false)

    fun cargarPuntos() {
        val email = SessionManager.usuarioActual?.email ?: return
        val ligaId = SessionManager.ligaActual?.id ?: return

        viewModelScope.launch {
            isLoading.value = true
            try {
                // Obtener atletas titulares del usuario
                val titulares = SupabaseClient.client.from("plantilla_usuario")
                    .select {
                        filter {
                            eq("email_usuario", email)
                            eq("liga_id", ligaId)
                            eq("es_titular", true)
                        }
                    }.decodeList<com.example.fantasyatl.ui.plantilla.PlantillaEntry>()

                val atletaIds = titulares.map { it.atleta_id }

                // Obtener puntos de esos atletas en esta liga
                val puntos = mutableListOf<PuntoJornada>()
                var total = 0

                for (atletaId in atletaIds) {
                    val puntosAtleta = SupabaseClient.client.from("puntos_jornada")
                        .select {
                            filter {
                                eq("liga_id", ligaId)
                                eq("atleta_id", atletaId)
                            }
                        }.decodeList<PuntoJornada>()
                    puntos.addAll(puntosAtleta)
                    total += puntosAtleta.sumOf { it.puntos }
                }

                puntosDetalle.value = puntos.sortedByDescending { it.jornada }
                puntosTotales.value = total

            } catch (e: Exception) {
                // Error silencioso
            } finally {
                isLoading.value = false
            }
        }
    }
}

@Composable
fun PuntosScreen(
    paddingValues: PaddingValues,
    viewModel: PuntosViewModel = viewModel()
) {
    LaunchedEffect(Unit) { viewModel.cargarPuntos() }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text("Mis Puntos", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        }

        // Total de puntos
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1A237E))
            ) {
                Column(
                    modifier = Modifier.padding(24.dp).fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Puntos totales", color = Color.White.copy(alpha = 0.7f), fontSize = 14.sp)
                    Text(
                        "${viewModel.puntosTotales.value}",
                        color = Color.White,
                        fontSize = 48.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Text(
                        "Solo cuentan los titulares",
                        color = Color.White.copy(alpha = 0.5f),
                        fontSize = 12.sp
                    )
                }
            }
        }

        if (viewModel.isLoading.value) {
            item { CircularProgressIndicator() }
        } else if (viewModel.puntosDetalle.value.isEmpty()) {
            item {
                Text(
                    "Aún no hay puntos registrados para tus atletas titulares.",
                    color = Color.Gray,
                    fontSize = 13.sp
                )
            }
        } else {
            item {
                Text(
                    "Detalle por jornada",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
            items(viewModel.puntosDetalle.value) { punto ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                "Jornada ${punto.jornada}",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                            punto.descripcion?.let {
                                Text(it, fontSize = 12.sp, color = Color.Gray)
                            }
                        }
                        Text(
                            "+${punto.puntos} pts",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 18.sp,
                            color = Color(0xFF2E7D32)
                        )
                    }
                }
            }
        }
    }
}
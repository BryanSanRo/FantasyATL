package com.example.fantasyatl.ui.puntos

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.fantasyatl.data.dataSession.SessionManager
import com.example.fantasyatl.data.dataSession.SupabaseClient

// 🟢 IMPORTS CORREGIDOS SEGÚN TU ESTRUCTURA DE ARCHIVOS
import com.example.fantasyatl.data.dataPlantilla.PlantillaEntry
import com.example.fantasyatl.data.dataPuntos.PuntosJornadaEntry
import com.example.fantasyatl.data.dataPuntos.UsuarioPuntos

import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.launch

class PuntosViewModel : ViewModel() {
    var puntosTotales = mutableStateOf(0)
    var isLoading = mutableStateOf(false)
    var error = mutableStateOf<String?>(null)

    fun calcularPuntosUsuario(email: String, ligaId: String) {
        viewModelScope.launch {
            isLoading.value = true
            error.value = null
            try {
                // Paso A: Buscamos qué atletas tiene comprados el usuario en esta liga concreta
                val misAtletasEnPlantilla = SupabaseClient.client
                    .from("plantilla_usuario")
                    .select {
                        filter {
                            eq("email_usuario", email)
                            eq("liga_id", ligaId)
                        }
                    }.decodeList<PlantillaEntry>()

                if (misAtletasEnPlantilla.isEmpty()) {
                    puntosTotales.value = 0
                    return@launch
                }

                // Guardamos solo los ID de los futbolistas/atletas del usuario
                val listaIdsAtletas = misAtletasEnPlantilla.map { it.atletaId ?: "" }

                // Paso B: Traemos todas las puntuaciones registradas en la liga actual
                val todosLosPuntosLiga = SupabaseClient.client
                    .from("puntos_jornada")
                    .select {
                        filter {
                            eq("liga_id", ligaId)
                        }
                    }.decodeList<PuntosJornadaEntry>() // 🟢 Usando tu clase exacta

                // Paso C: Filtramos en memoria sumando los puntos de los atletas que sí posee el usuario
                val sumaPuntos = todosLosPuntosLiga
                    .filter { listaIdsAtletas.contains(it.atletaId) }
                    .sumOf { it.puntos }

                puntosTotales.value = sumaPuntos

            } catch (e: Exception) {
                error.value = "Error al calcular puntos: ${e.localizedMessage}"
            } finally {
                isLoading.value = false
            }
        }
    }
}

@Composable
fun PuntosScreen(
    paddingValues: PaddingValues,
    puntosViewModel: PuntosViewModel = viewModel()
) {
    val emailUsuario = SessionManager.usuarioActual?.email ?: ""
    val idLiga = SessionManager.ligaActual?.id ?: ""

    LaunchedEffect(emailUsuario, idLiga) {
        if (emailUsuario.isNotEmpty() && idLiga.isNotEmpty()) {
            puntosViewModel.calcularPuntosUsuario(emailUsuario, idLiga)
        } else {
            puntosViewModel.error.value = "Selecciona una liga para ver tus puntuaciones acumuladas."
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues),
        contentAlignment = Alignment.Center
    ) {
        when {
            puntosViewModel.isLoading.value -> {
                CircularProgressIndicator()
            }
            puntosViewModel.error.value != null -> {
                Text(
                    text = puntosViewModel.error.value ?: "Error",
                    color = Color.Red,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(16.dp)
                )
            }
            else -> {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(24.dp)
                ) {
                    Text(
                        text = "Mis Puntos Totales",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1A237E)
                    )
                    Spacer(modifier = Modifier.height(24.dp))

                    Card(
                        modifier = Modifier.size(160.dp),
                        shape = androidx.compose.foundation.shape.RoundedCornerShape(80.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Text(
                                text = "${puntosViewModel.puntosTotales.value}",
                                fontSize = 54.sp,
                                fontWeight = FontWeight.Black,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Suma de los puntos de jornada obtenidos por tus atletas en esta liga",
                        fontSize = 14.sp,
                        color = Color.Gray,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
            }
        }
    }
}
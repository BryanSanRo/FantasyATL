package com.example.fantasyatl.ui.clasificacion

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
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
import com.example.fantasyatl.data.LigaUsuario
import com.example.fantasyatl.data.SessionManager
import com.example.fantasyatl.data.SupabaseClient
import com.example.fantasyatl.data.Usuario
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable

@Serializable
data class ClasificacionEntry(
    val email: String,
    val nombre: String,
    val puntos: Int
)

class ClasificacionViewModel : ViewModel() {
    var clasificacion = mutableStateOf<List<ClasificacionEntry>>(emptyList())
    var isLoading = mutableStateOf(false)

    fun cargarClasificacion() {
        val ligaId = SessionManager.ligaActual?.id ?: return

        viewModelScope.launch {
            isLoading.value = true
            try {
                val miembros = SupabaseClient.client.from("liga_usuarios")
                    .select { filter { eq("liga_id", ligaId) } }
                    .decodeList<LigaUsuario>()

                val entries = miembros.map { miembro ->
                    val usuario = SupabaseClient.client.from("usuarios")
                        .select { filter { eq("email", miembro.email_usuario) } }
                        .decodeSingleOrNull<Usuario>()

                    ClasificacionEntry(
                        email = miembro.email_usuario,
                        nombre = "${usuario?.nombre ?: "?"} ${usuario?.apellidos ?: ""}",
                        puntos = miembro.puntos
                    )
                }.sortedByDescending { it.puntos }

                clasificacion.value = entries

            } catch (e: Exception) {
                // Error silencioso
            } finally {
                isLoading.value = false
            }
        }
    }
}

@Composable
fun ClasificacionScreen(
    paddingValues: PaddingValues,
    viewModel: ClasificacionViewModel = viewModel()
) {
    val emailActual = SessionManager.usuarioActual?.email ?: ""

    LaunchedEffect(Unit) { viewModel.cargarClasificacion() }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Text("Clasificación", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Text(
                SessionManager.ligaActual?.nombre ?: "",
                fontSize = 14.sp,
                color = Color.Gray
            )
        }

        if (viewModel.isLoading.value) {
            item { CircularProgressIndicator() }
        } else {
            itemsIndexed(viewModel.clasificacion.value) { index, entry ->
                val esTuyo = entry.email == emailActual
                val medallaColor = when (index) {
                    0 -> Color(0xFFFFD700)   // Oro
                    1 -> Color(0xFFC0C0C0)   // Plata
                    2 -> Color(0xFFCD7F32)   // Bronce
                    else -> Color.Gray
                }

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (esTuyo) Color(0xFFE8F5E9) else MaterialTheme.colorScheme.surface
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            // Posición
                            Text(
                                text = when (index) {
                                    0 -> "🥇"
                                    1 -> "🥈"
                                    2 -> "🥉"
                                    else -> "${index + 1}º"
                                },
                                fontSize = 20.sp,
                                modifier = Modifier.width(40.dp)
                            )
                            Column(modifier = Modifier.padding(start = 8.dp)) {
                                Text(
                                    entry.nombre,
                                    fontWeight = if (esTuyo) FontWeight.ExtraBold else FontWeight.Bold,
                                    fontSize = 15.sp
                                )
                                if (esTuyo) {
                                    Text("(Tú)", fontSize = 11.sp, color = Color(0xFF2E7D32))
                                }
                            }
                        }
                        Text(
                            "${entry.puntos} pts",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 18.sp,
                            color = Color(0xFF1A237E)
                        )
                    }
                }
            }
        }
    }
}
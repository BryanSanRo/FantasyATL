package com.example.fantasyatl.ui.puntos

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.fantasyatl.data.LigaDB.LigaUsuario
import com.example.fantasyatl.data.PlantillaDB.PlantillaEntry
import com.example.fantasyatl.data.PuntosDB.PuntosJornadaEntry
import com.example.fantasyatl.data.SessionDB.SessionManager
import com.example.fantasyatl.data.SessionDB.SupabaseClient
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable

@Serializable
data class PerfilUsuario(
    val email: String,
    val nombre: String
)

data class ItemClasificacion(
    val posicion: Int,
    val nombre: String,
    val email: String,
    val puntos: Int,
    val esUsuarioActual: Boolean
)

class PuntosViewModel : ViewModel() {
    var clasificacion = mutableStateOf<List<ItemClasificacion>>(emptyList())
    var puntosUsuarioActual = mutableStateOf(0)
    var posicionUsuarioActual = mutableStateOf(0)
    var isLoading = mutableStateOf(false)
    var error = mutableStateOf<String?>(null)
    var nombreLiga = mutableStateOf("Mi Liga")

    fun calcularClasificacionYMisPuntos(ligaId: String) {
        viewModelScope.launch {
            isLoading.value = true
            error.value = null
            try {
                // 1. Obtener todos los usuarios de la liga actual
                val miembrosLiga = SupabaseClient.client
                    .from("liga_usuarios")
                    .select {
                        filter { eq("liga_id", ligaId) }
                    }.decodeList<LigaUsuario>()

                if (miembrosLiga.isEmpty()) {
                    clasificacion.value = emptyList()
                    return@launch
                }

                // 2. Obtener perfiles para los nombres reales
                val todosLosPerfiles = SupabaseClient.client
                    .from("usuarios")
                    .select()
                    .decodeList<PerfilUsuario>()
                    .associateBy { it.email }

                // 3. Obtener todas las puntuaciones de la liga actual (Tu paso B original)
                val todosLosPuntosLiga = SupabaseClient.client
                    .from("puntos_jornada")
                    .select {
                        filter { eq("liga_id", ligaId) }
                    }.decodeList<PuntosJornadaEntry>()

                val emailLogueado = SessionManager.usuarioActual?.email ?: ""

                // 4. Calcular los puntos de cada participante (Tu paso A y C en bucle paralelo)
                val rankingProcesado = miembrosLiga.map { miembro ->
                    async {
                        val plantillaMiembro = SupabaseClient.client
                            .from("plantilla_usuario")
                            .select {
                                filter {
                                    eq("email_usuario", miembro.emailUsuario)
                                    eq("liga_id", ligaId)
                                }
                            }.decodeList<PlantillaEntry>()

                        val listaIdsAtletas = plantillaMiembro.map { it.atletaId ?: "" }

                        val sumaPuntosMiembro = todosLosPuntosLiga
                            .filter { listaIdsAtletas.contains(it.atletaId) }
                            .sumOf { it.puntos }

                        val nombreMostrar = todosLosPerfiles[miembro.emailUsuario]?.nombre
                            ?: miembro.emailUsuario.substringBefore("@")

                        ItemClasificacion(
                            posicion = 0,
                            nombre = nombreMostrar,
                            email = miembro.emailUsuario,
                            puntos = sumaPuntosMiembro,
                            esUsuarioActual = miembro.emailUsuario == emailLogueado
                        )
                    }
                }.awaitAll()

                // 5. Ordenar de mayor a menor y asignar puestos del ranking
                val rankingFinal = rankingProcesado
                    .sortedByDescending { it.puntos }
                    .mapIndexed { index, item ->
                        item.copy(posicion = index + 1)
                    }

                clasificacion.value = rankingFinal

                // 6. Extraer los datos específicos del usuario logueado para la tarjeta superior
                val datosMisPuntos = rankingFinal.find { it.esUsuarioActual }
                puntosUsuarioActual.value = datosMisPuntos?.puntos ?: 0
                posicionUsuarioActual.value = datosMisPuntos?.posicion ?: 0

            } catch (e: Exception) {
                error.value = "Error al obtener la clasificación: ${e.localizedMessage}"
            } finally {
                isLoading.value = false
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PuntosScreen(
    paddingValues: PaddingValues,
    puntosViewModel: PuntosViewModel = viewModel()
) {
    val idLiga = SessionManager.ligaActual?.id ?: ""
    val nombreLiga = SessionManager.ligaActual?.nombre ?: "Mi Liga"

    LaunchedEffect(idLiga) {
        if (idLiga.isNotEmpty()) {
            puntosViewModel.nombreLiga.value = nombreLiga
            puntosViewModel.calcularClasificacionYMisPuntos(idLiga)
        } else {
            puntosViewModel.error.value = "Selecciona una liga para ver el ranking."
        }
    }

    val fondoPremium = Brush.verticalGradient(
        colors = listOf(Color(0xFF0F1B3E), Color(0xFF080F26))
    )

    // Eliminamos el Scaffold interno de esta pantalla porque el contenedor padre
    // ya dibuja la barra de navegación inferior (sección 0, 1, 2, 3)
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = fondoPremium)
            .padding(paddingValues)
            .padding(horizontal = 16.dp)
    ) {
        when {
            puntosViewModel.isLoading.value -> {
                CircularProgressIndicator(
                    color = Color.White,
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            puntosViewModel.error.value != null -> {
                Text(
                    text = puntosViewModel.error.value!!,
                    color = Color.Red,
                    modifier = Modifier.align(Alignment.Center),
                    textAlign = TextAlign.Center
                )
            }

            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Título superior de la Liga
                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "${puntosViewModel.nombreLiga.value} • Clasificación",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                    }

                    // TARJETA SUPERIOR: "Mis Puntos Totales"
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White)
                        ) {
                            Column(
                                modifier = Modifier.padding(20.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "Mis Puntos Totales",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    color = Color(0xFF0F1B3E)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "${puntosViewModel.puntosUsuarioActual.value}",
                                    fontSize = 56.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color(0xFF1A237E)
                                )
                                Text(
                                    text = "puntos en la liga",
                                    fontSize = 12.sp,
                                    color = Color.Gray
                                )
                                Spacer(modifier = Modifier.height(12.dp))

                                // Píldora de posición de medalla
                                SuggestionChip(
                                    onClick = { },
                                    label = {
                                        Text(
                                            text = "🎖️ Posición #${puntosViewModel.posicionUsuarioActual.value} de ${puntosViewModel.clasificacion.value.size}",
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold
                                        )
                                    },
                                    colors = SuggestionChipDefaults.suggestionChipColors(
                                        containerColor = Color(0xFF1A237E)
                                    ),
                                    border = null,
                                    shape = RoundedCornerShape(50.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "Clasificación de la liga",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = Color.White
                        )
                        Text(
                            text = "Suma de puntos de jornada por jugador",
                            fontSize = 12.sp,
                            color = Color.White.copy(alpha = 0.6f)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    // FILAS DEL RANKING DINÁMICO
                    items(puntosViewModel.clasificacion.value) { jugador ->
                        FilaPodioPremium(jugador = jugador)
                    }

                    item { Spacer(modifier = Modifier.height(24.dp)) }
                }
            }
        }
    }
}

@Composable
fun FilaPodioPremium(jugador: ItemClasificacion) {
    val colorFondoRow = when (jugador.posicion) {
        1 -> Color(0xFFFFD54F) // Oro brillante
        2 -> Color(0xFFCFD8DC) // Plata
        3 -> Color(0xFFFFCC80) // Bronce
        else -> Color(0xFF18254B) // Resto de usuarios azul oscuro
    }

    val colorTextoPrincipal = if (jugador.posicion in 1..3) Color(0xFF0F1B3E) else Color.White
    val colorTextoSecundario =
        if (jugador.posicion in 1..3) Color(0xFF424242) else Color.White.copy(alpha = 0.6f)

    val modifierCard = if (jugador.esUsuarioActual && jugador.posicion > 3) {
        Modifier
            .fillMaxWidth()
            .border(2.dp, Color(0xFFFFD54F), RoundedCornerShape(14.dp))
    } else {
        Modifier.fillMaxWidth()
    }

    Card(
        modifier = modifierCard,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = colorFondoRow)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val prefijoPosicion = when (jugador.posicion) {
                1 -> "🥇 #"
                2 -> "🥈 #"
                3 -> "🥉 #"
                else -> "  #"
            }
            Text(
                text = "$prefijoPosicion${jugador.posicion}",
                fontSize = 18.sp,
                fontWeight = FontWeight.Black,
                color = colorTextoPrincipal,
                modifier = Modifier.width(65.dp)
            )

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = if (jugador.esUsuarioActual) "${jugador.nombre} • TÚ" else jugador.nombre,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = colorTextoPrincipal
                )
                Text(
                    text = jugador.email,
                    fontSize = 12.sp,
                    color = colorTextoSecundario
                )
            }

            Text(
                text = "${jugador.puntos} pts",
                fontSize = 16.sp,
                fontWeight = FontWeight.Black,
                color = colorTextoPrincipal,
                textAlign = TextAlign.End
            )
        }
    }
}
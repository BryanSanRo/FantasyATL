package com.example.fantasyatl.ui.market

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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.fantasyatl.data.dataAtleta.Atleta
import com.example.fantasyatl.data.dataSession.SupabaseClient
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fantasyatl.ui.plantilla.PlantillaViewModel
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.launch

class AtletaViewModel : ViewModel() {
    var atletasMercado = mutableStateOf<List<Atleta>>(emptyList())
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

                atletasMercado.value = todos.shuffled().take(8)
            } catch (e: Exception) {
                error.value = "Error al cargar el mercado"
            } finally {
                isLoading.value = false
            }
        }
    }
}

@Composable
fun MercadoScreen(
    paddingValues: PaddingValues,
    plantillaViewModel: PlantillaViewModel,
    atletaViewModel: AtletaViewModel = viewModel()
) {
    var tabSeleccionada by remember { mutableIntStateOf(0) }
    val titulosTabs = listOf("En Venta", "Operaciones")
    var mensajeAccion by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        atletaViewModel.cargarMercado()
        plantillaViewModel.cargarPlantilla()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
    ) {
        TabRow(selectedTabIndex = tabSeleccionada) {
            titulosTabs.forEachIndexed { index, titulo ->
                Tab(
                    selected = tabSeleccionada == index,
                    onClick = { tabSeleccionada = index },
                    text = { Text(titulo) }
                )
            }
        }

        // Mensaje acción (fichar/error)
        mensajeAccion?.let { msg ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (msg.trim().startsWith("✅") || msg.contains("correctamente"))
                        Color(0xFFE8F5E9) else Color(0xFFFFEBEE)
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = msg,
                    modifier = Modifier.padding(12.dp),
                    color = if (msg.trim().startsWith("✅") || msg.contains("correctamente")) Color(0xFF2E7D32) else Color.Red,
                    fontSize = 13.sp
                )
            }
        }

        when (tabSeleccionada) {

            // --- PESTAÑA 1: Atletas en venta ---
            0 -> {
                when {
                    atletaViewModel.isLoading.value -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) { CircularProgressIndicator() }
                    }
                    atletaViewModel.error.value != null -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                atletaViewModel.error.value ?: "",
                                color = Color.Red
                            )
                        }
                    }
                    else -> {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            items(atletaViewModel.atletasMercado.value) { atleta ->
                                AtletaMercadoCard(
                                    atleta = atleta,
                                    presupuesto = plantillaViewModel.presupuesto.value,
                                    onFichar = {
                                        plantillaViewModel.ficharAtleta(
                                            atleta = atleta,
                                            onExito = {
                                                mensajeAccion = " ${atleta.nombre} fichado correctamente"
                                                atletaViewModel.cargarMercado()
                                            },
                                            onError = { msg ->
                                                mensajeAccion = " $msg"
                                            }
                                        )
                                    }
                                )
                            }
                        }
                    }
                }
            }

            // --- PESTAÑA 2: Operaciones ---
            1 -> {
                val titulares = plantillaViewModel.titulares.value
                val suplentes = plantillaViewModel.suplentes.value
                val todos = titulares + suplentes

                if (todos.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "No tienes atletas en tu plantilla.\nFicha atletas en la pestaña 'En Venta'.",
                            color = Color.Gray,
                            textAlign = TextAlign.Center
                        )
                    }
                } else {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Columna de Titulares
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                "Titulares",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            titulares.forEach { entry ->
                                // Extraemos el atleta de forma segura en singular (.atleta)
                                val atleta = entry.atleta
                                if (atleta != null) {
                                    Card(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(bottom = 6.dp),
                                        colors = CardDefaults.cardColors(
                                            containerColor = Color(0xFFE8F5E9)
                                        )
                                    ) {
                                        Text(
                                            "${atleta.nombre} ${atleta.apellidos}",
                                            modifier = Modifier.padding(8.dp),
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                }
                            }
                        }

                        VerticalDivider(color = Color.LightGray)

                        // Columna de Suplentes
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                "Suplentes",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            suplentes.forEach { entry ->
                                // Extraemos el atleta de forma segura en singular (.atleta)
                                val atleta = entry.atleta
                                if (atleta != null) {
                                    Card(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(bottom = 6.dp),
                                        colors = CardDefaults.cardColors(
                                            containerColor = Color(0xFFFFFDE7)
                                        )
                                    ) {
                                        Text(
                                            "${atleta.nombre} ${atleta.apellidos}",
                                            modifier = Modifier.padding(8.dp),
                                            fontSize = 13.sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AtletaMercadoCard(
    atleta: Atleta,
    presupuesto: Long,
    onFichar: () -> Unit
) {
    val puedeFichar = atleta.precio.toLong() <= presupuesto

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(3.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Círculo con inicial
            Surface(
                shape = RoundedCornerShape(50),
                color = Color(0xFF1A237E),
                modifier = Modifier.size(48.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = if (atleta.nombre.isNotEmpty()) atleta.nombre.first().toString() else "A",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    "${atleta.nombre} ${atleta.apellidos}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
                Text(atleta.disciplina, fontSize = 12.sp, color = Color.Gray)
                Text(
                    "⭐ ${atleta.valoracion}  |  ${"%,d".format(atleta.precio)} €",
                    fontSize = 12.sp,
                    color = Color(0xFF1A237E),
                    fontWeight = FontWeight.SemiBold
                )
            }

            Button(
                onClick = onFichar,
                enabled = puedeFichar,
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (puedeFichar) Color(0xFF2E7D32) else Color.Gray
                ),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text(
                    if (puedeFichar) "Fichar" else "Sin €",
                    fontSize = 12.sp,
                    color = Color.White
                )
            }
        }
    }
}
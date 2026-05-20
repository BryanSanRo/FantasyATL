package com.example.fantasyatl.ui.market

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.fantasyatl.data.AtletaDB.Atleta
import com.example.fantasyatl.data.SessionDB.SessionManager
import com.example.fantasyatl.ui.plantilla.PlantillaViewModel

@Composable
fun MercadoScreen(
    paddingValues: PaddingValues,
    plantillaViewModel: PlantillaViewModel,
    atletaViewModel: AtletaViewModel = viewModel()
) {
    // ✅ Email y ligaId siempre desde SessionManager — misma fuente que PlantillaViewModel
    val emailUsuario = remember { SessionManager.usuarioActual?.email ?: "" }
    val ligaId       = remember { SessionManager.ligaActual?.id ?: "" }

    var mensajeAccion  by remember { mutableStateOf<String?>(null) }
    var errorAccion    by remember { mutableStateOf<String?>(null) }
    var atletaParaFichar by remember { mutableStateOf<Atleta?>(null) }

    val presupuestoDisponible = plantillaViewModel.presupuesto.value

    LaunchedEffect(ligaId) {
        if (ligaId.isNotEmpty()) {
            plantillaViewModel.cargarPlantilla()
            atletaViewModel.cargarMercado(ligaId)
        } else {
            errorAccion = "No tienes una liga activa. Vuelve al Dashboard."
        }
    }

    val fondo = Brush.verticalGradient(colors = listOf(Color(0xFF0F1B3E), Color(0xFF080F26)))

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = fondo)
            .padding(paddingValues)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {

            mensajeAccion?.let { msg ->
                Card(
                    modifier = Modifier.fillMaxWidth().padding(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1B5E20))
                ) {
                    Text(msg, modifier = Modifier.padding(12.dp), color = Color.White, fontSize = 14.sp)
                }
            }

            errorAccion?.let { err ->
                Card(
                    modifier = Modifier.fillMaxWidth().padding(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFC62828))
                ) {
                    Text(err, modifier = Modifier.padding(12.dp), color = Color.White, fontSize = 14.sp)
                }
            }

            Box(modifier = Modifier.weight(1f)) {
                when {
                    ligaId.isEmpty() -> {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("Selecciona una liga en el Dashboard.", color = Color.White.copy(alpha = 0.5f), textAlign = TextAlign.Center)
                        }
                    }
                    atletaViewModel.isLoading.value -> {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(color = Color.White)
                        }
                    }
                    atletaViewModel.atletasMercado.value.isEmpty() -> {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("No hay atletas disponibles en el mercado.", color = Color.White.copy(alpha = 0.5f), textAlign = TextAlign.Center)
                        }
                    }
                    else -> {
                        val listaMercado = atletaViewModel.atletasMercado.value
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            itemsIndexed(
                                items = listaMercado,
                                key   = { index, atleta -> "${atleta.id ?: "a"}_$index" }
                            ) { _, atleta ->
                                AtletaCompraCard(
                                    atleta = atleta,
                                    onFicharClick = {
                                        mensajeAccion = null
                                        errorAccion   = null
                                        atletaParaFichar = atleta
                                    }
                                )
                            }
                        }
                    }
                }
            }

            // Barra presupuesto
            Box(
                modifier = Modifier.fillMaxWidth().background(Color(0xFF004D40)).padding(vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Presupuesto Disponible", color = Color.White.copy(alpha = 0.7f), fontSize = 13.sp)
                    Text(
                        "${"%,d".format(presupuestoDisponible)} €",
                        color = Color(0xFFFFD54F),
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
            }
        }

        // Diálogo confirmación fichaje
        atletaParaFichar?.let { atleta ->
            AlertDialog(
                onDismissRequest = { atletaParaFichar = null },
                containerColor   = Color(0xFF131F46),
                title = {
                    Text("Confirmar Fichaje", color = Color.White, fontWeight = FontWeight.Bold)
                },
                text = {
                    Column {
                        Text("¿Fichar a ${atleta.nombre} ${atleta.apellidos}?", color = Color.White)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "Precio: ${"%,d".format(atleta.precio)} €",
                            color = Color(0xFFFFD54F),
                            fontWeight = FontWeight.Bold
                        )
                        if (presupuestoDisponible < atleta.precio) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("❌ Saldo insuficiente", color = Color.Red)
                        }
                    }
                },
                confirmButton = {
                    Button(
                        enabled = presupuestoDisponible >= atleta.precio,
                        colors  = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                        onClick = {
                            atletaViewModel.ficharAtletaDirecto(
                                emailUsuario = emailUsuario,
                                ligaId       = ligaId,
                                atleta       = atleta,
                                onExito = { msg ->
                                    mensajeAccion = msg
                                    atletaParaFichar = null
                                    // ✅ Recargar plantilla con la misma ligaId
                                    plantillaViewModel.cargarPlantilla()
                                },
                                onError = { err ->
                                    errorAccion   = "❌ $err"
                                    atletaParaFichar = null
                                }
                            )
                        }
                    ) {
                        Text("Confirmar", color = Color.White)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { atletaParaFichar = null }) {
                        Text("Cancelar", color = Color.White.copy(alpha = 0.5f))
                    }
                }
            )
        }
    }
}

@Composable
fun AtletaCompraCard(atleta: Atleta, onFicharClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape  = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF132046))
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Surface(
                shape  = RoundedCornerShape(50),
                color  = Color(0xFF1A237E),
                modifier = Modifier.size(40.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        atleta.nombre.firstOrNull()?.toString() ?: "A",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text("${atleta.nombre} ${atleta.apellidos}", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color.White)
                Text("${atleta.disciplina} • ⭐ ${atleta.valoracion}", fontSize = 12.sp, color = Color.White.copy(alpha = 0.6f))
                Text("${"%,d".format(atleta.precio)} €", fontSize = 14.sp, color = Color(0xFFFFD54F), fontWeight = FontWeight.Bold)
            }

            Button(
                onClick = onFicharClick,
                colors  = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E3A8A)),
                shape   = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Icon(Icons.Default.ShoppingCart, null, tint = Color.White, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Fichar", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

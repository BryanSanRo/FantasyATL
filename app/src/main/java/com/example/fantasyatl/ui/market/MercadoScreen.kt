package com.example.fantasyatl.ui.market

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
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
    var tabSeleccionada by remember { mutableIntStateOf(0) }
    val titulosTabs = listOf("En Venta", "Operaciones")
    var mensajeAccion by remember { mutableStateOf<String?>(null) }
    var atletaParaPujar by remember { mutableStateOf<Atleta?>(null) }

    val tiempo by atletaViewModel.tiempoRestante
    val emailUsuario = remember { SessionManager.usuarioActual?.email ?: "" }

    // Acceso directo y seguro a los estados observables de los ViewModels
    val presupuestoReal = plantillaViewModel.presupuesto.value
    val retenidoProvisional = atletaViewModel.dineroRetenidoEnPujas.value
    val saldoDisponibleReal = maxOf(0L, presupuestoReal - retenidoProvisional)

    val idsYaFichados = remember(
        plantillaViewModel.titulares.value,
        plantillaViewModel.suplentes.value
    ) {
        (plantillaViewModel.titulares.value + plantillaViewModel.suplentes.value)
            .mapNotNull { it.atletaId }.toSet()
    }

    LaunchedEffect(Unit) {
        plantillaViewModel.cargarPlantilla()
    }

    LaunchedEffect(emailUsuario) {
        if (emailUsuario.isNotEmpty()) {
            atletaViewModel.cargarMercado(idsYaFichados, emailUsuario)
        }
    }

    // CORRECCIÓN REVENTÓN DE COMPILACIÓN: Escuchamos el cambio de valor de forma limpia
    LaunchedEffect(atletaViewModel.mensajeResolucion.value) {
        atletaViewModel.mensajeResolucion.value?.let { msg ->
            mensajeAccion = msg
            plantillaViewModel.cargarPlantilla()
            if (emailUsuario.isNotEmpty()) {
                atletaViewModel.cargarMercado(idsYaFichados, emailUsuario)
            }
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

            TabRow(
                selectedTabIndex = tabSeleccionada,
                containerColor = Color(0xFF131F46),
                contentColor = Color.White
            ) {
                titulosTabs.forEachIndexed { index, titulo ->
                    Tab(
                        selected = tabSeleccionada == index,
                        onClick = { tabSeleccionada = index },
                        text = {
                            Text(
                                titulo,
                                fontWeight = FontWeight.Bold,
                                color = if (tabSeleccionada == index) Color.White else Color.White.copy(alpha = 0.5f)
                            )
                        }
                    )
                }
            }

            // Barra de Cierre del Mercado
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp)
                    .background(Color(0xFF132046), shape = RoundedCornerShape(10.dp))
                    .padding(vertical = 12.dp, horizontal = 16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Timer, null, tint = Color(0xFFFFD54F), modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("El mercado cierra en:", color = Color.White, fontSize = 14.sp)
                    }
                    Text(tiempo, color = Color(0xFFFFD54F), fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }

            // Alertas de la Acción Ejecutada
            mensajeAccion?.let { msg ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (msg.contains("✅") || msg.contains("éxito") || msg.contains("registrada")) Color(0xFF1B5E20) else Color(0xFFC62828)
                    )
                ) {
                    Text(
                        msg,
                        modifier = Modifier.fillMaxWidth().padding(12.dp),
                        color = Color.White,
                        fontSize = 13.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }

            // Contenedor principal de Listas
            Box(modifier = Modifier.weight(1f)) {
                if (tabSeleccionada == 0) {
                    if (atletaViewModel.isLoading.value) {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(color = Color.White)
                        }
                    } else {
                        val listaVenta = atletaViewModel.atletasMercado.value
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            itemsIndexed(
                                items = listaVenta,
                                key = { index, atleta -> "${atleta.id ?: "atleta"}_${index}" }
                            ) { _, atleta ->
                                AtletaMercadoCard(
                                    atleta = atleta,
                                    onPujarClick = { atletaParaPujar = atleta }
                                )
                            }
                        }
                    }
                } else {
                    val listaOps = atletaViewModel.misPujasActivas.value
                    if (listaOps.isEmpty()) {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text(
                                "No tienes ninguna puja activa.",
                                color = Color.White.copy(alpha = 0.5f),
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(32.dp)
                            )
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            itemsIndexed(
                                items = listaOps,
                                key = { index, atleta -> "op_${atleta.id ?: "atleta"}_${index}" }
                            ) { _, atleta ->
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(14.dp),
                                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1A2A56))
                                ) {
                                    Column(modifier = Modifier.padding(14.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Column {
                                                Text(
                                                    "${atleta.nombre} ${atleta.apellidos}",
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 16.sp,
                                                    color = Color.White
                                                )
                                                Text(
                                                    atleta.disciplina,
                                                    fontSize = 12.sp,
                                                    color = Color.White.copy(alpha = 0.6f)
                                                )
                                            }
                                            Text(tiempo, color = Color(0xFFFFD54F), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                        }
                                        Spacer(modifier = Modifier.height(10.dp))
                                        HorizontalDivider(color = Color.White.copy(alpha = 0.1f))
                                        Spacer(modifier = Modifier.height(10.dp))
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Column {
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Icon(Icons.Default.People, null, tint = Color.LightGray, modifier = Modifier.size(14.dp))
                                                    Spacer(modifier = Modifier.width(4.dp))
                                                    Text("${atleta.contadorPujas} usuarios pujando", color = Color.LightGray, fontSize = 12.sp)
                                                }
                                                Spacer(modifier = Modifier.height(2.dp))
                                                Text(
                                                    "Tu oferta: ${"%,d".format(atleta.miPujaActual ?: 0)} €",
                                                    color = Color(0xFF4CAF50),
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 14.sp
                                                )
                                                Text("Reservado — solo se descuenta si ganas", color = Color(0xFFFFD54F).copy(alpha = 0.7f), fontSize = 11.sp)
                                            }
                                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                                IconButton(
                                                    onClick = { atletaParaPujar = atleta },
                                                    colors = IconButtonDefaults.iconButtonColors(containerColor = Color(0xFF1A237E))
                                                ) {
                                                    Icon(Icons.Default.Edit, "Modificar", tint = Color.White)
                                                }
                                                IconButton(
                                                    onClick = {
                                                        atletaViewModel.eliminarPuja(
                                                            atleta.id ?: "",
                                                            emailUsuario,
                                                            idsYaFichados,
                                                            onExito = { mensajeAccion = "✅ Puja eliminada." },
                                                            onError = { err -> mensajeAccion = "❌ $err" }
                                                        )
                                                    },
                                                    colors = IconButtonDefaults.iconButtonColors(containerColor = Color(0xFFB71C1C))
                                                ) {
                                                    Icon(Icons.Default.Delete, "Borrar", tint = Color.White)
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

            // PANEL DE CONTROL INFERIOR VERDE (CORREGIDO DE CORREÍDO Y SINTAXIS)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF004D40))
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        "Presupuesto Total: ${"%,d".format(presupuestoReal)} €",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    // CORRECCIÓN AQUÍ: Paréntesis cerrado correctamente e if-else inline limpio
                    Text(
                        text = if (retenidoProvisional > 0) {
                            "Saldo Disponible: ${"%,d".format(saldoDisponibleReal)} € (Retenido: ${"%,d".format(retenidoProvisional)} €)"
                        } else {
                            "Saldo Disponible: ${"%,d".format(saldoDisponibleReal)} €"
                        },
                        color = Color(0xFFFFD54F),
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 12.sp
                    )
                }
            }
        }

        // Dialog de Entrada de Pujas
        atletaParaPujar?.let { atleta ->
            var cantidadInput by remember { mutableStateOf(atleta.miPujaActual?.toString() ?: atleta.precio.toString()) }
            AlertDialog(
                onDismissRequest = { atletaParaPujar = null },
                containerColor = Color(0xFF131F46),
                title = { Text(if (atleta.miPujaActual != null) "Modificar Puja" else "Puja Secreta", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp) },
                text = {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text("${atleta.nombre} ${atleta.apellidos}", color = Color.White, fontWeight = FontWeight.Bold)
                        Text("Precio mínimo: ${"%,d".format(atleta.precio)} €", color = Color.White.copy(alpha = 0.6f), fontSize = 12.sp)
                        val maxPuja = presupuestoReal + (atleta.miPujaActual?.toLong() ?: 0L)
                        Text("Máximo: ${"%,d".format(maxPuja)} €", color = Color(0xFF4CAF50), fontSize = 12.sp)
                        Spacer(modifier = Modifier.height(12.dp))
                        OutlinedTextField(
                            value = cantidadInput,
                            onValueChange = { cantidadInput = it },
                            label = { Text("Tu oferta (€)", color = Color.White.copy(alpha = 0.6f)) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = Color(0xFFFFD54F),
                                unfocusedBorderColor = Color.White.copy(alpha = 0.3f)
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                },
                confirmButton = {
                    Button(
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                        onClick = {
                            val oferta = cantidadInput.toIntOrNull() ?: 0
                            val maxPuja = presupuestoReal + (atleta.miPujaActual?.toLong() ?: 0L)
                            when {
                                oferta < atleta.precio -> {
                                    mensajeAccion = "❌ Mínimo requerido: ${"%,d".format(atleta.precio)} €"
                                    atletaParaPujar = null
                                }
                                oferta.toLong() > maxPuja -> {
                                    mensajeAccion = "❌ Fondos insuficientes. Máximo: ${"%,d".format(maxPuja)} €"
                                    atletaParaPujar = null
                                }
                                else -> {
                                    atletaViewModel.enviarOModificarPuja(
                                        atletaId = atleta.id ?: "",
                                        emailUsuario = emailUsuario,
                                        oferta = oferta,
                                        idsYaFichados = idsYaFichados,
                                        onExito = { msg ->
                                            mensajeAccion = "✅ $msg"
                                            atletaParaPujar = null
                                        },
                                        onError = { err ->
                                            mensajeAccion = "❌ $err"
                                            atletaParaPujar = null
                                        }
                                    )
                                }
                            }
                        }
                    ) { Text("Guardar Puja", color = Color.White) }
                },
                dismissButton = {
                    TextButton(onClick = { atletaParaPujar = null }) {
                        Text("Cerrar", color = Color.White.copy(alpha = 0.5f))
                    }
                }
            )
        }
    }
}

@Composable
fun AtletaMercadoCard(atleta: Atleta, onPujarClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF132046))
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Surface(
                shape = RoundedCornerShape(50),
                color = Color(0xFF1A237E),
                modifier = Modifier.size(46.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        atleta.nombre.firstOrNull()?.toString() ?: "A",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                }
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text("${atleta.nombre} ${atleta.apellidos}", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color.White)
                Text(atleta.disciplina, fontSize = 12.sp, color = Color.White.copy(alpha = 0.6f))
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("${"%,d".format(atleta.precio)} €", fontSize = 13.sp, color = Color(0xFFFFD54F), fontWeight = FontWeight.Bold)
                    if (atleta.miPujaActual != null) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Ofertado", color = Color(0xFF4CAF50), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }

                // MUESTRA EL CONTADOR DE PERSONAS PUJANDO EN "EN VENTA"
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.People,
                        contentDescription = null,
                        tint = Color.White.copy(alpha = 0.6f),
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${atleta.contadorPujas} usuarios pujando",
                        color = Color.White.copy(alpha = 0.6f),
                        fontSize = 12.sp
                    )
                }
            }
            Button(
                onClick = onPujarClick,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E3A8A)),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Icon(Icons.Default.Gavel, null, tint = Color.White, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(if (atleta.miPujaActual != null) "Editar" else "Pujar", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}
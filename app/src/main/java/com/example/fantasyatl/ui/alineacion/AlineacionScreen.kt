package com.example.fantasyatl.ui.alineacion

import androidx.compose.foundation.background
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
import com.example.fantasyatl.ui.plantilla.PlantillaViewModel

@Composable
fun AlineacionScreen(
    paddingValues: PaddingValues,
    viewModel: PlantillaViewModel = viewModel()
) {
    LaunchedEffect(Unit) { viewModel.cargarPlantilla() }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                "Mi Alineación",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }

        item {
            Text(
                "TITULARES",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2E7D32),
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        if (viewModel.titulares.value.isEmpty()) {
            item {
                Text(
                    "Sin titulares — ficha atletas en el Mercado",
                    color = Color.Gray,
                    fontSize = 13.sp
                )
            }
        } else {
            items(viewModel.titulares.value) { entry ->
                AtletaAlineacionCard(
                    nombre = "${entry.atletas?.nombre} ${entry.atletas?.apellidos}",
                    disciplina = entry.atletas?.disciplina ?: "",
                    valoracion = entry.atletas?.valoracion ?: 0,
                    esTitular = true,
                    onCambiar = { viewModel.cambiarTitularidad(entry) }
                )
            }
        }

        item {
            Text(
                "SUPLENTES",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFF9A825),
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        if (viewModel.suplentes.value.isEmpty()) {
            item {
                Text(
                    "Sin suplentes",
                    color = Color.Gray,
                    fontSize = 13.sp
                )
            }
        } else {
            items(viewModel.suplentes.value) { entry ->
                AtletaAlineacionCard(
                    nombre = "${entry.atletas?.nombre} ${entry.atletas?.apellidos}",
                    disciplina = entry.atletas?.disciplina ?: "",
                    valoracion = entry.atletas?.valoracion ?: 0,
                    esTitular = false,
                    onCambiar = { viewModel.cambiarTitularidad(entry) }
                )
            }
        }
    }
}

@Composable
fun AtletaAlineacionCard(
    nombre: String,
    disciplina: String,
    valoracion: Int,
    esTitular: Boolean,
    onCambiar: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (esTitular) Color(0xFFE8F5E9) else Color(0xFFFFFDE7)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Círculo inicial
            Surface(
                shape = RoundedCornerShape(50),
                color = if (esTitular) Color(0xFF2E7D32) else Color(0xFFF9A825),
                modifier = Modifier.size(44.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = nombre.first().toString(),
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                }
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 12.dp)
            ) {
                Text(nombre, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text(disciplina, fontSize = 12.sp, color = Color.Gray)
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("⭐ $valoracion", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                TextButton(
                    onClick = onCambiar,
                    contentPadding = PaddingValues(4.dp)
                ) {
                    Text(
                        if (esTitular) "→ Suplente" else "→ Titular",
                        fontSize = 10.sp,
                        color = Color(0xFF1A237E)
                    )
                }
            }
        }
    }
}
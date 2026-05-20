package com.example.fantasyatl.ui.plantilla

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun PlantillaScreen(
    paddingValues: PaddingValues,
    viewModel: PlantillaViewModel = viewModel()
) {
    LaunchedEffect(Unit) { viewModel.cargarPlantilla() }

    val total = viewModel.titulares.value.size + viewModel.suplentes.value.size

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Mi Plantilla", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Text("$total / 10 atletas", fontSize = 13.sp, color = Color.Gray)
            }
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1A237E)),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text(
                    text = "Presupuesto: ${"%,d".format(viewModel.presupuesto.value)} €",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(12.dp)
                )
            }
        }

        val todos = viewModel.titulares.value + viewModel.suplentes.value
        if (todos.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "Tu plantilla está vacía.\nFicha atletas en el Mercado.",
                        color = Color.Gray,
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            items(todos) { entry ->
                val atleta = entry.atleta
                if (atleta != null) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    "${atleta.nombre} ${atleta.apellidos}",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                                Text(
                                    atleta.disciplina,
                                    fontSize = 12.sp,
                                    color = Color.Gray
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    // 🟢 CORREGIDO: esTitular en lugar de es_titular
                                    if (entry.esTitular) "🟢 Titular" else "🟡 Suplente",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    "⭐ ${atleta.valoracion}",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF2E7D32)
                                )
                                Text(
                                    "${"%,d".format(atleta.precio)} €",
                                    fontSize = 11.sp,
                                    color = Color.Gray
                                )
                                TextButton(
                                    onClick = { viewModel.venderAtleta(entry) },
                                    contentPadding = PaddingValues(4.dp)
                                ) {
                                    Text("Vender", fontSize = 11.sp, color = Color.Red)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
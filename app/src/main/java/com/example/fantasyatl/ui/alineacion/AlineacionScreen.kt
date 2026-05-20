package com.example.fantasyatl.ui.alineacion

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.fantasyatl.ui.plantilla.PlantillaViewModel

@Composable
fun AlineacionScreen(
    paddingValues: PaddingValues,
    viewModel: PlantillaViewModel = viewModel()
) {
    // Forzamos la carga inicial de los atletas al renderizar la pantalla
    LaunchedEffect(Unit) {
        viewModel.cargarPlantilla()
    }

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
            // Mapeamos directamente con la lista utilizando tu clase Atleta limpia
            items(viewModel.titulares.value) { entry ->
                // Usamos el operador elvis (?) por seguridad en caso de que tarde en llegar de Supabase
                val atleta = entry.atleta
                if (atleta != null) {
                    AtletaAlineacionCard(
                        nombre = "${atleta.nombre} ${atleta.apellidos}",
                        disciplina = atleta.disciplina,
                        valoracion = atleta.valoracion,
                        esTitular = true,
                        onCambiar = { viewModel.cambiarTitularidad(entry) }
                    )
                }
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
            // Mapeamos los suplentes de igual forma con tu clase Atleta limpia
            items(viewModel.suplentes.value) { entry ->
                val atleta = entry.atleta
                if (atleta != null) {
                    AtletaAlineacionCard(
                        nombre = "${atleta.nombre} ${atleta.apellidos}",
                        disciplina = atleta.disciplina,
                        valoracion = atleta.valoracion,
                        esTitular = false,
                        onCambiar = { viewModel.cambiarTitularidad(entry) }
                    )
                }
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
            // Inicial redondeada
            Surface(
                shape = RoundedCornerShape(50),
                color = if (esTitular) Color(0xFF2E7D32) else Color(0xFFF9A825),
                modifier = Modifier.size(44.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = if (nombre.isNotEmpty()) nombre.first().toString() else "A",
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
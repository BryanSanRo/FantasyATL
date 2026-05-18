package com.example.fantasyatl.ui.clasificaion

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun ClasificacionScreen(
    // 🌟 CORRECCIÓN CRUCIAL: El tipo debe ser ClasificacionViewModel, no PaddingValues
    viewModel: ClasificacionViewModel = viewModel()
) {
    // Al entrar a la pestaña, se descargan los datos en tiempo real de Supabase
    LaunchedEffect(Unit) {
        viewModel.cargarClasificacion()
    }

    val gradientBackground = Brush.verticalGradient(
        colors = listOf(Color(0xFF1A237E), Color(0xFF121212))
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = gradientBackground)
            .padding(16.dp)
    ) {
        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Clasificación General",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        if (viewModel.isLoading.value) { //
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Color.White)
            }
        } else if (viewModel.clasificacion.value.isEmpty()) { //
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No hay jugadores registrados en esta liga.", color = Color.Gray)
            }
        } else {
            // Lista scrollable con las posiciones de los usuarios
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                itemsIndexed(viewModel.clasificacion.value) { index, jugador -> //
                    val posicion = index + 1

                    // Color especial si estás en el podio (Top 3)
                    val cardColor = when (posicion) {
                        1 -> Color(0xFFFFD700) // Oro
                        2 -> Color(0xFFC0C0C0) // Plata
                        3 -> Color(0xFFCD7F32) // Bronce
                        else -> Color.White.copy(alpha = 0.15f) //
                    }

                    val textColor = if (posicion <= 3) Color.Black else Color.White //

                    Card(
                        modifier = Modifier.fillMaxWidth(), //
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = cardColor)
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(16.dp)
                                .fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "#$posicion",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = textColor,
                                    modifier = Modifier.width(45.dp)
                                )
                                Column {
                                    Text(
                                        text = jugador.nombre,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = textColor
                                    )
                                    Text(
                                        text = jugador.email,
                                        fontSize = 12.sp,
                                        color = textColor.copy(alpha = 0.7f)
                                    )
                                }
                            }
                            Text(
                                text = "${jugador.puntos} pts",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = textColor
                            )
                        }
                    }
                }
            }
        }
    }
}
package com.example.fantasyatl.ui.liga

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
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

@Composable
fun LigaScreen(
    onVolverAlDashboard: () -> Unit,
    viewModel: LigaViewModel = viewModel()
) {
    var mostrarCrear by remember { mutableStateOf(false) }

    val gradientBackground = Brush.verticalGradient(
        colors = listOf(Color(0xFF1A237E), Color(0xFF121212))
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = gradientBackground),
        contentAlignment = Alignment.Center
    ) {
        if (viewModel.isLoading.value) {
            CircularProgressIndicator(color = Color.White)
        } else {
            Card(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .padding(16.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier
                        .padding(24.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = if (mostrarCrear) "Crear Tu Liga" else "Unirse a una Liga",
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1A237E)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = if (mostrarCrear)
                            "Se generará un código único para que tus amigos puedan unirse."
                        else "Introduce el código de la liga para empezar a competir.",
                        textAlign = TextAlign.Center,
                        color = Color.Gray,
                        fontSize = 14.sp
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    if (mostrarCrear) {
                        OutlinedTextField(
                            value = viewModel.nombreLiga.value,
                            onValueChange = { viewModel.nombreLiga.value = it },
                            label = { Text("Nombre de la liga") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp)
                        )
                    } else {
                        OutlinedTextField(
                            value = viewModel.codigoLiga.value,
                            onValueChange = { viewModel.codigoLiga.value = it.uppercase() },
                            label = { Text("Código de acceso") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp)
                        )
                    }

                    viewModel.errorMessage.value?.let { error ->
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = error,
                            color = Color.Red,
                            fontSize = 13.sp,
                            textAlign = TextAlign.Center
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = {
                            if (mostrarCrear) {
                                viewModel.crearLiga { onVolverAlDashboard() }
                            } else {
                                viewModel.unirseALiga { onVolverAlDashboard() }
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32))
                    ) {
                        Text(if (mostrarCrear) "Crear liga" else "Unirse ahora", color = Color.White)
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    TextButton(onClick = onVolverAlDashboard) {
                        Text("← Volver al Panel", color = Color.Gray)
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    TextButton(onClick = { mostrarCrear = !mostrarCrear }) {
                        Text(
                            text = if (mostrarCrear) "¿Ya tienes código? Únete aquí" else "¿No tienes liga? Crea una",
                            color = Color(0xFF1A237E)
                        )
                    }
                }
            }
        }
    }
}
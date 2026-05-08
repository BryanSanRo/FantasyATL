package com.example.fantasyatl.ui.liga

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun LigaScreen(
    onLigaConfigurada: () -> Unit,         // ✅ Cuando tiene liga, activa las pantallas
    viewModel: LigaViewModel = viewModel()
) {
    LaunchedEffect(Unit) {
        viewModel.comprobarLiga()
    }

    LaunchedEffect(viewModel.ligaActual.value) {
        if (viewModel.ligaActual.value != null) onLigaConfigurada()
    }

    var mostrarCrear by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        when {
            viewModel.isLoading.value -> CircularProgressIndicator()

            viewModel.ligaActual.value != null -> {
                // Ya tiene liga — se activa la navegación principal
                onLigaConfigurada()
            }

            mostrarCrear -> {
                // --- CREAR LIGA ---
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            "Crear Liga",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        OutlinedTextField(
                            value = viewModel.nombreLiga.value,
                            onValueChange = {
                                viewModel.nombreLiga.value = it
                                viewModel.nombreLigaError.value = null
                            },
                            label = { Text("Nombre de la liga") },
                            isError = viewModel.nombreLigaError.value != null,
                            supportingText = {
                                viewModel.nombreLigaError.value?.let {
                                    Text(it, color = Color.Red)
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        if (viewModel.isLoading.value) {
                            CircularProgressIndicator()
                        } else {
                            Button(
                                onClick = { viewModel.crearLiga() },
                                modifier = Modifier.fillMaxWidth().height(50.dp),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF2E7D32)
                                )
                            ) {
                                Text(
                                    "Crear liga",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        TextButton(onClick = { mostrarCrear = false }) {
                            Text("← Volver", color = Color(0xFF1A237E))
                        }

                        viewModel.errorGeneral.value?.let {
                            Text(it, color = Color.Red, fontSize = 13.sp)
                        }
                    }
                }
            }

            else -> {
                // --- PANTALLA INICIAL SIN LIGA ---
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("⚽", fontSize = 60.sp)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        "No tienes ninguna liga",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        "Crea una liga o únete a una existente con un código",
                        fontSize = 14.sp,
                        color = Color.Gray,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 8.dp)
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    // --- BOTÓN CREAR ---
                    Button(
                        onClick = { mostrarCrear = true },
                        modifier = Modifier.fillMaxWidth().height(55.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF1A237E)
                        )
                    ) {
                        Text(
                            "Crear una liga",
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // --- UNIRSE CON CÓDIGO ---
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                "Unirse con código",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                            Spacer(modifier = Modifier.height(8.dp))

                            OutlinedTextField(
                                value = viewModel.codigoUnirse.value,
                                onValueChange = {
                                    viewModel.codigoUnirse.value = it.uppercase()
                                    viewModel.codigoError.value = null
                                },
                                label = { Text("Código de 6 letras") },
                                isError = viewModel.codigoError.value != null,
                                supportingText = {
                                    viewModel.codigoError.value?.let {
                                        Text(it, color = Color.Red)
                                    }
                                },
                                keyboardOptions = KeyboardOptions(
                                    capitalization = KeyboardCapitalization.Characters
                                ),
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(10.dp)
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            if (viewModel.isLoading.value) {
                                Box(
                                    modifier = Modifier.fillMaxWidth(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator()
                                }
                            } else {
                                Button(
                                    onClick = { viewModel.unirseALiga() },
                                    modifier = Modifier.fillMaxWidth().height(50.dp),
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(0xFF2E7D32)
                                    )
                                ) {
                                    Text(
                                        "Unirse",
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            viewModel.errorGeneral.value?.let {
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(it, color = Color.Red, fontSize = 13.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}
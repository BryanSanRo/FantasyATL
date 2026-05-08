package com.example.fantasyatl.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun RecuperacionScreen(
    onVolver: () -> Unit,
    viewModel: RecuperacionViewModel = viewModel()
) {
    val gradientBackground = Brush.verticalGradient(
        colors = listOf(Color(0xFF1A237E), Color(0xFF004D40))
    )

    LaunchedEffect(viewModel.exitoso.value) {
        if (viewModel.exitoso.value) onVolver()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = gradientBackground),
        contentAlignment = Alignment.Center
    ) {
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
                when (viewModel.paso.value) {

                    // PASO 1 — Introducir email
                    1 -> {
                        Text(
                            "¿Olvidaste tu contraseña?",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "Introduce tu email y te enviaremos un código de verificación",
                            fontSize = 14.sp,
                            color = Color.Gray,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(24.dp))

                        OutlinedTextField(
                            value = viewModel.emailRecuperacion.value,
                            onValueChange = {
                                viewModel.emailRecuperacion.value = it
                                viewModel.emailError.value = null
                            },
                            label = { Text("Correo electrónico") },
                            isError = viewModel.emailError.value != null,
                            supportingText = {
                                viewModel.emailError.value?.let { Text(it, color = Color.Red) }
                            },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        if (viewModel.isLoading.value) {
                            CircularProgressIndicator()
                        } else {
                            Button(
                                onClick = { viewModel.solicitarRecuperacion() },
                                modifier = Modifier.fillMaxWidth().height(50.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF2E7D32)
                                )
                            ) {
                                Text("Enviar código", color = Color.White, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    // PASO 2 — Introducir código
                    2 -> {
                        Text(
                            "Introduce el código",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        // ⚠️ Solo para desarrollo — mostrar el token
                        if (viewModel.tokenGenerado.value.isNotBlank()) {
                            Card(
                                colors = CardDefaults.cardColors(
                                    containerColor = Color(0xFFFFF9C4)
                                ),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = "⚠️ Código (solo desarrollo):\n${viewModel.tokenGenerado.value}",
                                    modifier = Modifier.padding(12.dp),
                                    fontSize = 14.sp,
                                    color = Color(0xFF5D4037),
                                    textAlign = TextAlign.Center
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        OutlinedTextField(
                            value = viewModel.tokenIntroducido.value,
                            onValueChange = {
                                viewModel.tokenIntroducido.value = it
                                viewModel.tokenError.value = null
                            },
                            label = { Text("Código de 6 dígitos") },
                            isError = viewModel.tokenError.value != null,
                            supportingText = {
                                viewModel.tokenError.value?.let { Text(it, color = Color.Red) }
                            },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        if (viewModel.isLoading.value) {
                            CircularProgressIndicator()
                        } else {
                            Button(
                                onClick = { viewModel.verificarToken() },
                                modifier = Modifier.fillMaxWidth().height(50.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF2E7D32)
                                )
                            ) {
                                Text("Verificar código", color = Color.White, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    // PASO 3 — Nueva contraseña
                    3 -> {
                        Text(
                            "Nueva contraseña",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        OutlinedTextField(
                            value = viewModel.nuevaPassword.value,
                            onValueChange = {
                                viewModel.nuevaPassword.value = it
                                viewModel.nuevaPasswordError.value = null
                            },
                            label = { Text("Nueva contraseña") },
                            isError = viewModel.nuevaPasswordError.value != null,
                            supportingText = {
                                viewModel.nuevaPasswordError.value?.let { Text(it, color = Color.Red) }
                            },
                            visualTransformation = PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = viewModel.confirmarPassword.value,
                            onValueChange = {
                                viewModel.confirmarPassword.value = it
                                viewModel.confirmarPasswordError.value = null
                            },
                            label = { Text("Confirmar contraseña") },
                            isError = viewModel.confirmarPasswordError.value != null,
                            supportingText = {
                                viewModel.confirmarPasswordError.value?.let { Text(it, color = Color.Red) }
                            },
                            visualTransformation = PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        if (viewModel.isLoading.value) {
                            CircularProgressIndicator()
                        } else {
                            Button(
                                onClick = { viewModel.cambiarPassword() },
                                modifier = Modifier.fillMaxWidth().height(50.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF2E7D32)
                                )
                            ) {
                                Text("Guardar contraseña", color = Color.White, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                viewModel.errorGeneral.value?.let {
                    Text(it, color = Color.Red, fontSize = 13.sp, textAlign = TextAlign.Center)
                }

                TextButton(onClick = onVolver) {
                    Text("Volver al login", color = Color(0xFF1A237E))
                }
            }
        }
    }
}
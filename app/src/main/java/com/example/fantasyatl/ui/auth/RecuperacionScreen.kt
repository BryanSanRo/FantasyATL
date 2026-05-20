package com.example.fantasyatl.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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

    // ✅ Navegar cuando sea exitoso
    LaunchedEffect(viewModel.exitoso.value) {
        if (viewModel.exitoso.value) {
            viewModel.resetear()
            onVolver()
        }
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
                // ✅ Indicador de pasos
                PasoIndicador(pasoActual = viewModel.paso.value)

                Spacer(modifier = Modifier.height(20.dp))

                when (viewModel.paso.value) {

                    // --- PASO 1: Email ---
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
                                viewModel.errorGeneral.value = null
                            },
                            label = { Text("Correo electrónico") },
                            isError = viewModel.emailError.value != null,
                            supportingText = {
                                viewModel.emailError.value?.let {
                                    Text(it, color = Color.Red)
                                }
                            },
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Email
                            ),
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        if (viewModel.isLoading.value) {
                            CircularProgressIndicator(color = Color(0xFF1A237E))
                        } else {
                            Button(
                                onClick = { viewModel.solicitarRecuperacion() },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF2E7D32)
                                )
                            ) {
                                Text(
                                    "Enviar código al email",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    // --- PASO 2: Código ---
                    2 -> {
                        Text(
                            "Revisa tu email",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        // ✅ Confirmación de envío — sin mostrar el código
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = Color(0xFFE8F5E9)
                            ),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("✅ ", fontSize = 20.sp)
                                Column {
                                    Text(
                                        "Código enviado a:",
                                        fontSize = 12.sp,
                                        color = Color.Gray
                                    )
                                    Text(
                                        viewModel.emailRecuperacion.value,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF2E7D32)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        OutlinedTextField(
                            value = viewModel.tokenIntroducido.value,
                            onValueChange = {
                                if (it.length <= 6) {
                                    viewModel.tokenIntroducido.value = it
                                    viewModel.tokenError.value = null
                                }
                            },
                            label = { Text("Código de 6 dígitos") },
                            isError = viewModel.tokenError.value != null,
                            supportingText = {
                                viewModel.tokenError.value?.let {
                                    Text(it, color = Color.Red)
                                }
                            },
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Number
                            ),
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // ✅ Reenviar código
                        TextButton(
                            onClick = {
                                viewModel.tokenIntroducido.value = ""
                                viewModel.tokenError.value = null
                                viewModel.intentosFallidos.value = 0
                                viewModel.paso.value = 1
                            }
                        ) {
                            Text(
                                "¿No recibiste el código? Reenviar",
                                color = Color(0xFF1A237E),
                                fontSize = 13.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        if (viewModel.isLoading.value) {
                            CircularProgressIndicator(color = Color(0xFF1A237E))
                        } else {
                            Button(
                                onClick = { viewModel.verificarToken() },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF2E7D32)
                                ),
                                // ✅ Desactivar si demasiados intentos
                                enabled = viewModel.intentosFallidos.value < 5
                            ) {
                                Text(
                                    "Verificar código",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    // --- PASO 3: Nueva contraseña ---
                    3 -> {
                        Text(
                            "Nueva contraseña",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            "Mínimo 8 caracteres, un número y un símbolo",
                            fontSize = 12.sp,
                            color = Color.Gray,
                            textAlign = TextAlign.Center
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
                                viewModel.nuevaPasswordError.value?.let {
                                    Text(it, color = Color.Red)
                                }
                            },
                            visualTransformation = PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Password
                            ),
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true
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
                                viewModel.confirmarPasswordError.value?.let {
                                    Text(it, color = Color.Red)
                                }
                            },
                            visualTransformation = PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Password
                            ),
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        if (viewModel.isLoading.value) {
                            CircularProgressIndicator(color = Color(0xFF1A237E))
                        } else {
                            Button(
                                onClick = { viewModel.cambiarPassword() },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF2E7D32)
                                )
                            ) {
                                Text(
                                    "Guardar nueva contraseña",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Error general
                viewModel.errorGeneral.value?.let {
                    Text(
                        it,
                        color = Color.Red,
                        fontSize = 13.sp,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }

                // Botón volver
                TextButton(onClick = {
                    viewModel.resetear()
                    onVolver()
                }) {
                    Text("← Volver al login", color = Color(0xFF1A237E))
                }
            }
        }
    }
}

// ✅ Indicador visual de los 3 pasos
@Composable
fun PasoIndicador(pasoActual: Int) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        listOf("Email", "Código", "Password").forEachIndexed { index, label ->
            val paso = index + 1
            val activo = paso == pasoActual
            val completado = paso < pasoActual

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Surface(
                    shape = RoundedCornerShape(50),
                    color = when {
                        completado -> Color(0xFF2E7D32)
                        activo -> Color(0xFF1A237E)
                        else -> Color.LightGray
                    },
                    modifier = Modifier.size(32.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = if (completado) "✓" else "$paso",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }
                Text(
                    label,
                    fontSize = 10.sp,
                    color = if (activo) Color(0xFF1A237E) else Color.Gray
                )
            }

            if (index < 2) {
                HorizontalDivider(
                    modifier = Modifier
                        .width(40.dp)
                        .padding(bottom = 16.dp),
                    color = if (completado) Color(0xFF2E7D32) else Color.LightGray,
                    thickness = 2.dp
                )
            }
        }
    }
}
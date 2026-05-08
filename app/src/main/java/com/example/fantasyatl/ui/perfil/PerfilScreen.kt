package com.example.fantasyatl.ui.perfil

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.fantasyatl.data.SessionManager
import com.example.fantasyatl.ui.auth.RecuperacionViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PerfilScreen(
    onVolver: () -> Unit,
    viewModel: RecuperacionViewModel = viewModel()
) {
    val usuario = SessionManager.usuarioActual
    var mostrarExito by remember { mutableStateOf("") }

    // Rellenar campos con datos actuales
    LaunchedEffect(Unit) {
        viewModel.nuevoNombre.value = usuario?.nombre ?: ""
        viewModel.nuevoApellidos.value = usuario?.apellidos ?: ""
        viewModel.nuevoEmail.value = usuario?.email ?: ""
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Mi Cuenta", color = Color.White, fontWeight = FontWeight.Bold)
                },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Volver",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF1A237E)
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // --- CABECERA ---
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1A237E))
            ) {
                Column(
                    modifier = Modifier
                        .padding(20.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Surface(
                        shape = RoundedCornerShape(50),
                        color = Color.White.copy(alpha = 0.2f),
                        modifier = Modifier.size(70.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = usuario?.nombre?.first()?.toString() ?: "U",
                                color = Color.White,
                                fontSize = 30.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "${usuario?.nombre} ${usuario?.apellidos}",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = usuario?.email ?: "",
                        color = Color.White.copy(alpha = 0.7f),
                        fontSize = 13.sp
                    )
                }
            }

            // --- MENSAJE ÉXITO ---
            if (mostrarExito.isNotBlank()) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "✅ $mostrarExito",
                        modifier = Modifier.padding(12.dp),
                        color = Color(0xFF2E7D32),
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            // --- EDITAR NOMBRE Y APELLIDOS ---
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "Editar datos personales",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = viewModel.nuevoNombre.value,
                        onValueChange = { viewModel.nuevoNombre.value = it },
                        label = { Text("Nombre") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = viewModel.nuevoApellidos.value,
                        onValueChange = { viewModel.nuevoApellidos.value = it },
                        label = { Text("Apellidos") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = viewModel.nuevoEmail.value,
                        onValueChange = { viewModel.nuevoEmail.value = it },
                        label = { Text("Correo electrónico") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = {
                            mostrarExito = ""
                            viewModel.actualizarDatosPerfil {
                                mostrarExito = "Datos actualizados correctamente"
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF1A237E)
                        )
                    ) {
                        Text(
                            "Guardar cambios",
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // --- CAMBIAR CONTRASEÑA ---
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "Cambiar contraseña",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    Text(
                        "Mínimo 8 caracteres, un número y un símbolo",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = viewModel.passwordActual.value,
                        onValueChange = {
                            viewModel.passwordActual.value = it
                            viewModel.passwordActualError.value = null
                        },
                        label = { Text("Contraseña actual") },
                        isError = viewModel.passwordActualError.value != null,
                        supportingText = {
                            viewModel.passwordActualError.value?.let {
                                Text(it, color = Color.Red)
                            }
                        },
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))

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
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = viewModel.confirmarPassword.value,
                        onValueChange = {
                            viewModel.confirmarPassword.value = it
                            viewModel.confirmarPasswordError.value = null
                        },
                        label = { Text("Confirmar nueva contraseña") },
                        isError = viewModel.confirmarPasswordError.value != null,
                        supportingText = {
                            viewModel.confirmarPasswordError.value?.let {
                                Text(it, color = Color.Red)
                            }
                        },
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = {
                            mostrarExito = ""
                            viewModel.cambiarPasswordPerfil {
                                mostrarExito = "Contraseña cambiada correctamente"
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF2E7D32)
                        )
                    ) {
                        Text(
                            "Cambiar contraseña",
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    viewModel.errorGeneral.value?.let {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(it, color = Color.Red, fontSize = 13.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
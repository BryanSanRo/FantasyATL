package com.example.fantasyatl.ui.auth

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    viewModel: AuthViewModel = viewModel(),
    onRegisterSuccess: () -> Unit,
    onNavigateToLogin: () -> Unit
) {
    val gradientBackground = Brush.verticalGradient(
        colors = listOf(Color(0xFF1A237E), Color(0xFF004D40))
    )

    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    val selectedDate = datePickerState.selectedDateMillis?.let {
                        SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date(it))
                    } ?: ""
                    viewModel.fechaNacimiento.value = selectedDate
                    showDatePicker = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("Cancelar") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    LaunchedEffect(viewModel.registroExitoso.value) {
        if (viewModel.registroExitoso.value) onRegisterSuccess()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = gradientBackground)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(40.dp))

            Text(
                text = "Crear Cuenta",
                color = Color.White,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(24.dp))

            CustomInputField(
                label = "Nombre",
                value = viewModel.nombre.value,
                error = viewModel.nombreError.value,
                onValueChange = { viewModel.nombre.value = it },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
            )

            CustomInputField(
                label = "Apellidos",
                value = viewModel.apellidos.value,
                error = viewModel.apellidosError.value,
                onValueChange = { viewModel.apellidos.value = it },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
            )

            CustomInputField(
                label = "Correo Electrónico",
                value = viewModel.email.value,
                // 🟢 CORREGIDO: ahora mostramos el error de formato de email
                error = viewModel.usernameError.value,
                onValueChange = {
                    viewModel.email.value = it
                    viewModel.usernameError.value = null
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                )
            )

            // Fecha de Nacimiento
            Column(modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)) {
                Text(
                    "Fecha de Nacimiento",
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold
                )
                OutlinedTextField(
                    value = viewModel.fechaNacimiento.value,
                    onValueChange = { },
                    readOnly = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showDatePicker = true },
                    enabled = false,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        disabledTextColor = Color.Black,
                        disabledContainerColor = Color.White.copy(alpha = 0.8f),
                        disabledBorderColor = Color.Transparent
                    ),
                    placeholder = { Text("Selecciona tu fecha") }
                )
                // Error de fecha
                viewModel.fechaError.value?.let {
                    Text(text = it, color = Color(0xFFFFCDD2), fontSize = 12.sp)
                }
            }

            // --- CAMPO CONTRASEÑA CON REQUISITOS EN EL FOCO ---
            CustomInputField(
                label = "Contraseña",
                value = viewModel.password.value,
                error = viewModel.passwordError.value,
                onValueChange = { viewModel.password.value = it },
                isPassword = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Next
                ),
                requirements = listOf(
                    "• Mínimo 8 caracteres",
                    "• Al menos un número (0-9)",
                    "• Al menos un símbolo especial (@#\$%^&+=!/¿?)"
                )
            )

            CustomInputField(
                label = "Confirmar Contraseña",
                value = viewModel.confirmPassword.value,
                error = viewModel.confirmPasswordError.value,
                onValueChange = { viewModel.confirmPassword.value = it },
                isPassword = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                )
            )

            // Error general
            viewModel.errorGeneral.value?.let { error ->
                Text(
                    text = error,
                    color = Color(0xFFFFCDD2),
                    fontSize = 14.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            if (viewModel.isLoading.value) {
                CircularProgressIndicator(color = Color.White)
            } else {
                Button(
                    onClick = { viewModel.registrarUsuario() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(55.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32))
                ) {
                    Text("Finalizar Registro", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            TextButton(
                onClick = { onNavigateToLogin() },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            ) {
                Text(
                    text = "¿Ya tienes cuenta? Inicia sesión",
                    color = Color.White.copy(alpha = 0.9f),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
fun CustomInputField(
    label: String,
    value: String,
    error: String? = null,
    isPassword: Boolean = false,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    requirements: List<String>? = null,
    onValueChange: (String) -> Unit
) {
    var isFocused by remember { mutableStateOf(false) }

    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 8.dp)) {
        Text(
            text = label,
            color = Color.White,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            isError = error != null,
            keyboardOptions = keyboardOptions,
            supportingText = {
                if (error != null) Text(text = error, color = Color(0xFFFFCDD2))
            },
            visualTransformation = if (isPassword) PasswordVisualTransformation()
            else VisualTransformation.None,
            modifier = Modifier
                .fillMaxWidth()
                .onFocusChanged { focusState ->
                    isFocused = focusState.isFocused // Detectamos si tiene el foco
                },
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.White.copy(alpha = 0.9f),
                unfocusedContainerColor = Color.White.copy(alpha = 0.8f),
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black,
                errorContainerColor = Color.White.copy(alpha = 0.9f),
                focusedBorderColor = Color(0xFF80CBC4),
                unfocusedBorderColor = Color.Transparent
            )
        )

        AnimatedVisibility(visible = isFocused && !requirements.isNullOrEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp, start = 6.dp)
            ) {
                Text(
                    text = "Requisitos de la contraseña:",
                    color = Color(0xFFB0BEC5), // Un tono gris claro legible
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 2.dp)
                )
                requirements?.forEach { requisito ->
                    Text(
                        text = requisito,
                        color = Color.White.copy(alpha = 0.85f),
                        fontSize = 12.sp,
                        modifier = Modifier.padding(vertical = 1.dp)
                    )
                }
            }
        }
    }
}
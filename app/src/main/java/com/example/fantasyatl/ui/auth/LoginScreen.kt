package com.example.fantasyatl.ui.auth

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.fantasyatl.R

@Composable
fun LoginScreen(
    viewModel: AuthViewModel = viewModel(),
    onLoginSuccess: () -> Unit,
    onNavigateToRegister: () -> Unit,
    onOlvidePassword: () -> Unit
) {
    LaunchedEffect(viewModel.loginExitoso.value) {
        if (viewModel.loginExitoso.value) onLoginSuccess()
    }

    val gradientBackground = Brush.verticalGradient(
        colors = listOf(Color(0xFF1A237E), Color(0xFF004D40))
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = gradientBackground)
    ) {
        // Logo
        Image(
            painter = painterResource(id = R.drawable.logo_fantasi),
            contentDescription = "Logo",
            modifier = Modifier
                .size(200.dp)
                .align(Alignment.TopCenter)
                .padding(top = 40.dp),
            contentScale = ContentScale.Fit
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp)
                .verticalScroll(rememberScrollState()), // Añadido para evitar desbordamiento con teclado
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.height(160.dp))

            // Email
            CustomLoginField(
                label = "Correo Electrónico",
                value = viewModel.username.value,
                onValueChange = {
                    viewModel.username.value = it
                    viewModel.usernameError.value = null
                },
                error = viewModel.usernameError.value,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Contraseña
            CustomLoginField(
                label = "Contraseña",
                value = viewModel.password.value,
                onValueChange = {
                    viewModel.password.value = it
                    viewModel.passwordError.value = null
                },
                error = viewModel.passwordError.value,
                isPassword = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                )
            )

            // Botón Olvidé
            TextButton(
                onClick = onOlvidePassword,
                modifier = Modifier.align(Alignment.End)
            ) {
                Text(text = "¿Olvidaste tu contraseña?", color = Color.White.copy(alpha = 0.8f))
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Error de conexión/servidor
            viewModel.errorGeneral.value?.let {
                Text(
                    text = it,
                    color = Color(0xFFFFCDD2),
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
            }

            // Botón Entrar
            if (viewModel.isLoading.value) {
                CircularProgressIndicator(color = Color.White)
            } else {
                Button(
                    onClick = { viewModel.loginUsuario() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(55.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32))
                ) {
                    Text("Entrar", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Botón Registro
            OutlinedButton(
                onClick = onNavigateToRegister,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(2.dp, Color.White)
            ) {
                Text("Crear cuenta nueva", color = Color.White, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun CustomLoginField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    error: String?,
    isPassword: Boolean = false,
    keyboardOptions: KeyboardOptions
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            isError = error != null,
            supportingText = {
                if (error != null) {
                    Text(
                        text = error,
                        color = Color(0xFFFFCDD2),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            },
            visualTransformation = if (isPassword) PasswordVisualTransformation() else androidx.compose.ui.text.input.VisualTransformation.None,
            keyboardOptions = keyboardOptions,
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White.copy(alpha = 0.8f),
                errorContainerColor = Color.White.copy(alpha = 0.9f),
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black,
                errorTextColor = Color.Black,
                focusedBorderColor = Color(0xFF80CBC4),
                unfocusedBorderColor = Color.Transparent
            )
        )
    }
}
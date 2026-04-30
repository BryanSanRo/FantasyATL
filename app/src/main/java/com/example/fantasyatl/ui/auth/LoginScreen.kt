package com.example.fantasyatl.ui.auth

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions // IMPORTANTE
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import com.example.fantasyatl.R
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun LoginScreen(
    viewModel: AuthViewModel = viewModel(),
    onLoginSuccess: () -> Unit,
    onNavigateToRegister: () -> Unit
){
    val gradientBackground = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF1A237E),
            Color(0xFF004D40)
        )
    )
    LaunchedEffect(viewModel.loginExitoso.value) {
        if (viewModel.loginExitoso.value) {
            onLoginSuccess()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = gradientBackground)
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo_fantasi),
            contentDescription = "Logo",
            modifier = Modifier
                .size(220.dp)
                .align(Alignment.TopCenter)
                .padding(top = 60.dp),
            contentScale = ContentScale.Fit
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.height(100.dp))

            // --- CAMPO USUARIO (EMAIL) ---
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Correo Electrónico",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 4.dp, bottom = 4.dp)
                )
                OutlinedTextField(
                    value = viewModel.username.value,
                    onValueChange = {
                        viewModel.username.value = it
                        viewModel.usernameError.value = null
                    },
                    // SOLUCIÓN AL PROBLEMA DE LAS SUGERENCIAS EXTRAÑAS
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email,
                        autoCorrect = false,
                        imeAction = ImeAction.Next
                    ),
                    isError = viewModel.usernameError.value != null,
                    supportingText = {
                        viewModel.usernameError.value?.let { Text(text = it, color = Color(0xFFFFCDD2)) }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.White.copy(alpha = 0.9f),
                        unfocusedContainerColor = Color.White.copy(alpha = 0.8f),
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black,
                        cursorColor = Color.Black,
                        focusedBorderColor = Color(0xFF80CBC4),
                        unfocusedBorderColor = Color.Transparent
                    )
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // --- CAMPO CONTRASEÑA ---
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Contraseña",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 4.dp, bottom = 4.dp)
                )
                OutlinedTextField(
                    value = viewModel.password.value,
                    onValueChange = {
                        viewModel.password.value = it
                        viewModel.passwordError.value = null
                    },
                    visualTransformation = PasswordVisualTransformation(),
                    // CONFIGURACIÓN DE TECLADO PARA CONTRASEÑA
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Done
                    ),
                    isError = viewModel.passwordError.value != null,
                    supportingText = {
                        viewModel.passwordError.value?.let { Text(text = it, color = Color(0xFFFFCDD2)) }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.White.copy(alpha = 0.9f),
                        unfocusedContainerColor = Color.White.copy(alpha = 0.8f),
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black,
                        cursorColor = Color.Black,
                        focusedBorderColor = Color(0xFF80CBC4),
                        unfocusedBorderColor = Color.Transparent
                    )
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // --- BOTÓN ENTRAR (CONECTADO A SUPABASE) ---
            Button(
                onClick = {
                    // Llamamos a la función que busca en la base de datos
                    viewModel.loginUsuario()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32))
            ) {
                Text(text = "Entrar", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedButton(
                onClick = { onNavigateToRegister() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(2.dp, Color.White)
            ) {
                Text(text = "Crear cuenta nueva", fontSize = 16.sp, color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
    }
}
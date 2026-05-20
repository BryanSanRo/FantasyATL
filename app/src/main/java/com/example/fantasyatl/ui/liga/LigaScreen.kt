package com.example.fantasyatl.ui.liga

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LigaScreen(
    onVolverAlDashboard: () -> Unit,
    ligaViewModel: LigaViewModel = viewModel()
) {

    val premiumBackground = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF111E47),
            Color(0xFF0A1128)
        )
    )

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Configuración de Liga",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onVolverAlDashboard) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color(0xFF1A237E)
                )
            )
        }
    ) { paddingValues ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(premiumBackground)
                .padding(paddingValues)
        ) {

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {

                // -------------------------
                // CREAR NUEVA LIGA
                // -------------------------
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFF1A2552)
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp)
                    ) {

                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Add,
                                contentDescription = null,
                                tint = Color(0xFFFFA000)
                            )

                            Spacer(modifier = Modifier.width(8.dp))

                            Text(
                                text = "Crear Nueva Liga",
                                color = Color.White,
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        OutlinedTextField(
                            value = ligaViewModel.nombreLiga.value,
                            onValueChange = {
                                ligaViewModel.nombreLiga.value = it
                            },

                            modifier = Modifier.fillMaxWidth(),

                            shape = RoundedCornerShape(16.dp),

                            textStyle = LocalTextStyle.current.copy(
                                color = Color.White,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Medium
                            ),

                            placeholder = {
                                Text(
                                    text = "Ej: Liga Olímpica 2026",
                                    color = Color.White.copy(alpha = 0.45f),
                                    fontSize = 16.sp
                                )
                            },

                            singleLine = true,

                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,

                                focusedBorderColor = Color(0xFF4FC3F7),
                                unfocusedBorderColor = Color.White.copy(alpha = 0.25f),

                                focusedContainerColor = Color(0xFF243B6B),
                                unfocusedContainerColor = Color(0xFF243B6B),

                                cursorColor = Color(0xFFFFA000)
                            )
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        Button(
                            onClick = {
                                ligaViewModel.crearLiga {
                                    onVolverAlDashboard()
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(58.dp),
                            shape = RoundedCornerShape(18.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF238636)
                            )
                        ) {
                            Text(
                                text = "Confirmar y Crear",
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                // -------------------------
                // UNIRSE A LIGA
                // -------------------------
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFF1A2552)
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp)
                    ) {

                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Lock,
                                contentDescription = null,
                                tint = Color(0xFFFFA000)
                            )

                            Spacer(modifier = Modifier.width(8.dp))

                            Text(
                                text = "Unirse con Código",
                                color = Color.White,
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        OutlinedTextField(
                            value = ligaViewModel.codigoLiga.value,
                            onValueChange = {
                                ligaViewModel.codigoLiga.value = it.uppercase()
                            },

                            modifier = Modifier.fillMaxWidth(),

                            shape = RoundedCornerShape(16.dp),

                            textStyle = LocalTextStyle.current.copy(
                                color = Color.White,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 2.sp
                            ),

                            placeholder = {
                                Text(
                                    text = "ABC123",
                                    color = Color.White.copy(alpha = 0.45f),
                                    fontSize = 16.sp
                                )
                            },

                            singleLine = true,

                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,

                                focusedBorderColor = Color(0xFFFFA000),
                                unfocusedBorderColor = Color.White.copy(alpha = 0.25f),

                                focusedContainerColor = Color(0xFF243B6B),
                                unfocusedContainerColor = Color(0xFF243B6B),

                                cursorColor = Color(0xFFFFA000)
                            )
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        Button(
                            onClick = {
                                ligaViewModel.unirseALigaPorCodigo {
                                    onVolverAlDashboard()
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(58.dp),
                            shape = RoundedCornerShape(18.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF2B4C7E)
                            )
                        ) {
                            Text(
                                text = "Unirse a la Comunidad",
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                // -------------------------
                // MENSAJE ERROR
                // -------------------------
                ligaViewModel.errorMessage.value?.let { error ->

                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFF5A1A1A)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = error,
                            color = Color.White,
                            modifier = Modifier.padding(16.dp),
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}
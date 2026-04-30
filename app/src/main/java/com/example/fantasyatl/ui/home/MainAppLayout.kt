package com.example.fantasyatl.ui.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppLayout(
    nombreUsuario: String,
    nombreLiga: String,
    saldo: String,
    seccionSeleccionada: Int,
    onSeccionSelected: (Int) -> Unit,
    contenido: @Composable (PaddingValues) -> Unit
) {
    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = {
                        Column {
                            Text(text = nombreUsuario, style = MaterialTheme.typography.titleMedium, color = Color.White)
                            Text(text = nombreLiga, style = MaterialTheme.typography.bodySmall, color = Color.White.copy(alpha = 0.7f))
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF1A1A1A))
                )

                TabRow(
                    selectedTabIndex = seccionSeleccionada,
                    containerColor = Color(0xFF1A1A1A),
                    contentColor = Color.White
                ) {
                    Tab(
                        selected = seccionSeleccionada == 0,
                        onClick = { onSeccionSelected(0) },
                        text = { Text("Perfil") }
                    )
                    Tab(
                        selected = seccionSeleccionada == 1,
                        onClick = { onSeccionSelected(1) },
                        text = { Text("Equipo") }
                    )
                    Tab(
                        selected = seccionSeleccionada == 2,
                        onClick = { onSeccionSelected(2) },
                        text = { Text("Mercado") }
                    )
                }
            }
        },
        bottomBar = {
            Surface(
                color = Color(0xFF2E7D32),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Saldo: $saldo",
                    modifier = Modifier.padding(12.dp),
                    color = Color.White,
                    style = MaterialTheme.typography.titleMedium,
                    textAlign = TextAlign.Center
                )
            }
        }
    ) { paddingValues ->
        contenido(paddingValues)
    }
}
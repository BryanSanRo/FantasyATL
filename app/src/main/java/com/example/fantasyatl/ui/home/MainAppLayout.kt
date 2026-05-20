package com.example.fantasyatl.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.SportsMartialArts
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppLayout(
    nombreUsuario: String,
    nombreLiga: String,
    saldo: String,
    seccionSeleccionada: Int,
    onSeccionSelected: (Int) -> Unit,
    onBackToDashboard: () -> Unit,
    dashboardViewModel: HomeDashboardViewModel? = null,
    onLigaCambiada: (() -> Unit)? = null,
    content: @Composable (PaddingValues) -> Unit
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(nombreLiga, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        Text(nombreUsuario, color = Color.White.copy(alpha = 0.7f), fontSize = 12.sp)
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBackToDashboard,
                        modifier = Modifier
                            .padding(start = 8.dp)
                            .size(40.dp)
                            .background(Color.White, shape = CircleShape)
                    ) {
                        Icon(Icons.Default.ArrowBack, "Volver", tint = Color.Black, modifier = Modifier.size(22.dp))
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color(0xFF1A237E))
            )
        },
        bottomBar = {
            Column {
                Surface(color = Color(0xFF004D40), modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Presupuesto: $saldo",
                        color = Color.White,
                        modifier = Modifier.padding(6.dp),
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }
                // ✅ 4 pestañas — sin Competencias
                NavigationBar(
                    containerColor = Color(0xFF1A237E),
                    contentColor = Color.White,
                    tonalElevation = 0.dp
                ) {
                    NavigationBarItem(
                        selected = seccionSeleccionada == 0,
                        onClick  = { onSeccionSelected(0) },
                        label    = { Text("Alineación", color = Color.White, fontSize = 9.sp) },
                        icon     = { Icon(Icons.Default.SportsMartialArts, null, tint = Color.White) },
                        colors   = NavigationBarItemDefaults.colors(indicatorColor = Color(0xFF3949AB))
                    )
                    NavigationBarItem(
                        selected = seccionSeleccionada == 1,
                        onClick  = { onSeccionSelected(1) },
                        label    = { Text("Plantilla", color = Color.White, fontSize = 9.sp) },
                        icon     = { Icon(Icons.Default.Person, null, tint = Color.White) },
                        colors   = NavigationBarItemDefaults.colors(indicatorColor = Color(0xFF3949AB))
                    )
                    NavigationBarItem(
                        selected = seccionSeleccionada == 2,
                        onClick  = { onSeccionSelected(2) },
                        label    = { Text("Mercado", color = Color.White, fontSize = 9.sp) },
                        icon     = { Icon(Icons.Default.ShoppingCart, null, tint = Color.White) },
                        colors   = NavigationBarItemDefaults.colors(indicatorColor = Color(0xFF3949AB))
                    )
                    NavigationBarItem(
                        selected = seccionSeleccionada == 3,
                        onClick  = { onSeccionSelected(3) },
                        label    = { Text("Puntos", color = Color.White, fontSize = 9.sp) },
                        icon     = { Icon(Icons.Default.Star, null, tint = Color.White) },
                        colors   = NavigationBarItemDefaults.colors(indicatorColor = Color(0xFF3949AB))
                    )
                }
            }
        }
    ) { paddingValues ->
        content(paddingValues)
    }
}

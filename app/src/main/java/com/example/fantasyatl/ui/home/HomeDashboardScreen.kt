package com.example.fantasyatl.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fantasyatl.data.SessionDB.SessionManager


@Composable
fun HomeDashboardScreen(
    dashboardViewModel: HomeDashboardViewModel,
    onNavigateToMiAlineacion: () -> Unit,
    onNavigateToPerfil: () -> Unit,
    onNavigateToCrearLiga: () -> Unit,
    onLogout: () -> Unit
) {
    LaunchedEffect(Unit) { dashboardViewModel.cargarDatosDashboardSilencioso() }

    val premiumBackground = Brush.verticalGradient(
        colors = listOf(Color(0xFF111E47), Color(0xFF0A1128))
    )
    val nombreUsuario = SessionManager.usuarioActual?.nombre ?: "Usuario"
    var mostrarAlertaSinLiga by remember { mutableStateOf(false) }
    var mostrarSelectorLiga by remember { mutableStateOf(false) }

    Box(modifier = Modifier
        .fillMaxSize()
        .background(brush = premiumBackground)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.Start
        ) {
            Spacer(modifier = Modifier.height(56.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column {
                    Text(
                        text = "¡Hola,\n${nombreUsuario.lowercase()}!",
                        fontSize = 38.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        lineHeight = 40.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Bienvenido a Fantasy ATL",
                        fontSize = 16.sp,
                        color = Color.White.copy(alpha = 0.6f)
                    )
                }
                IconButton(
                    onClick = onLogout,
                    modifier = Modifier
                        .background(
                            Color.White.copy(alpha = 0.12f),
                            shape = RoundedCornerShape(12.dp)
                        )
                        .size(48.dp)
                ) {
                    Icon(Icons.AutoMirrored.Filled.ExitToApp, "Cerrar Sesión", tint = Color.White)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // ✅ SELECTOR DE LIGA — solo si tiene más de una
            val ligas = dashboardViewModel.misLigasDisponibles.value
            if (ligas.size > 1) {
                Box {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { mostrarSelectorLiga = true },
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1D2951))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    "Liga activa",
                                    fontSize = 11.sp,
                                    color = Color.White.copy(alpha = 0.5f)
                                )
                                Text(
                                    dashboardViewModel.ligaNombre.value,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                            Icon(
                                Icons.Default.KeyboardArrowDown,
                                null,
                                tint = Color.White.copy(alpha = 0.6f)
                            )
                        }
                    }
                    DropdownMenu(
                        expanded = mostrarSelectorLiga,
                        onDismissRequest = { mostrarSelectorLiga = false }
                    ) {
                        ligas.forEach { liga ->
                            DropdownMenuItem(
                                text = { Text(liga.nombre) },
                                onClick = {
                                    mostrarSelectorLiga = false
                                    dashboardViewModel.cambiarDeLigaActiva(liga)
                                }
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            FullWidthDashboardCard(
                title = "Mi Liga",
                subtitle = "Entrar a Equipo y Mercado",
                backgroundColor = Color(0xFF238636),
                icon = Icons.Default.PlayArrow,
                onClick = {
                    if (SessionManager.ligaActual == null) mostrarAlertaSinLiga = true
                    else onNavigateToMiAlineacion()
                }
            )
            Spacer(modifier = Modifier.height(16.dp))
            FullWidthDashboardCard(
                title = "Configurar Liga",
                subtitle = "Crear o introducir código de acceso",
                backgroundColor = Color.White.copy(alpha = 0.08f),
                contentColor = Color.White,
                icon = Icons.Default.AddCircle,
                onClick = onNavigateToCrearLiga
            )
            Spacer(modifier = Modifier.height(16.dp))
            FullWidthDashboardCard(
                title = "Mi Perfil",
                subtitle = "Editar los detalles de tu cuenta",
                backgroundColor = Color(0xFF2B4C7E),
                icon = Icons.Default.Person,
                onClick = onNavigateToPerfil
            )

            Spacer(modifier = Modifier.height(28.dp))

            Text(
                "Tu Estado en la Liga",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.padding(bottom = 14.dp)
            )
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1D2951))
            ) {
                Row(
                    modifier = Modifier
                        .padding(20.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            dashboardViewModel.ligaNombre.value,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            "Liga activa",
                            fontSize = 13.sp,
                            color = Color.White.copy(alpha = 0.5f)
                        )
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            dashboardViewModel.posicionUsuario.value,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFFFFA000)
                        )
                        Text(
                            "${dashboardViewModel.puntosUsuario.value} pts",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                "Próximas Pruebas",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.padding(bottom = 14.dp)
            )
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF161F3D))
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    PruebaRowItem("10:30", "100m Lisos", "J. Bolt, M. Jacobs")
                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 14.dp),
                        color = Color.White.copy(alpha = 0.08f)
                    )
                    PruebaRowItem("11:15", "Salto de Altura", "G. Tamberi, M. Barshim")
                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 14.dp),
                        color = Color.White.copy(alpha = 0.08f)
                    )
                    PruebaRowItem("12:00", "400m Vallas", "K. Warholm, R. Benjamin")
                }
            }

            dashboardViewModel.avisoError.value?.let { mensaje ->
                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    mensaje,
                    color = Color(0xFFFF8A80),
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(40.dp))
        }
    }

    if (mostrarAlertaSinLiga) {
        AlertDialog(
            onDismissRequest = { mostrarAlertaSinLiga = false },
            title = {
                Text(
                    "¡No tienes ninguna Liga!",
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            },
            text = {
                Text(
                    "Crea una liga o únete con el código de tus amigos.",
                    color = Color.White.copy(alpha = 0.8f)
                )
            },
            containerColor = Color(0xFF1D2951),
            confirmButton = {
                TextButton(onClick = { mostrarAlertaSinLiga = false; onNavigateToCrearLiga() }) {
                    Text("Configurar Liga", color = Color(0xFFFFA000), fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { mostrarAlertaSinLiga = false }) {
                    Text("Cancelar", color = Color.White.copy(alpha = 0.6f))
                }
            }
        )
    }
}

@Composable
fun FullWidthDashboardCard(
    title: String,
    subtitle: String,
    backgroundColor: Color,
    contentColor: Color = Color.White,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor)
    ) {
        Row(modifier = Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = contentColor,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = contentColor
                )
                Text(text = subtitle, fontSize = 13.sp, color = contentColor.copy(alpha = 0.7f))
            }
        }
    }
}

@Composable
fun PruebaRowItem(hora: String, nombre: String, atletas: String) {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Text(
            hora,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFFFFA000),
            modifier = Modifier.width(55.dp)
        )
        Column {
            Text(nombre, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.White)
            Text("Atletas: $atletas", fontSize = 12.sp, color = Color.White.copy(alpha = 0.5f))
        }
    }
}

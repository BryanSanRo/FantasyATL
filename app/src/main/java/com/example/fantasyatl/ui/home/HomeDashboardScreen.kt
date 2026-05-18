package com.example.fantasyatl.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fantasyatl.data.dataSession.SessionManager

@Composable
fun HomeDashboardScreen(
    dashboardViewModel: HomeDashboardViewModel,
    onNavigateToMiAlineacion: () -> Unit,
    onNavigateToClasificacion: () -> Unit,
    onNavigateToPerfil: () -> Unit,
    onNavigateToCrearLiga: () -> Unit,
    onLogout: () -> Unit
) {
    // Fondo azul marino degradado premium de tu app
    val premiumBackground = Brush.verticalGradient(
        colors = listOf(Color(0xFF111E47), Color(0xFF0A1128))
    )

    val nombreUsuario = SessionManager.usuarioActual?.nombre ?: "Usuario"

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = premiumBackground)
    ) {
        if (dashboardViewModel.isLoading.value) {
            CircularProgressIndicator(
                color = Color.White,
                modifier = Modifier.align(Alignment.Center)
            )
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.Start
            ) {
                Spacer(modifier = Modifier.height(56.dp))

                // 👤 CABECERA: SALUDO Y BOTÓN DE SALIDA
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

                    // Botón de Cerrar Sesión minimalista
                    IconButton(
                        onClick = onLogout,
                        modifier = Modifier
                            .background(Color.White.copy(alpha = 0.12f), shape = RoundedCornerShape(12.dp))
                            .size(48.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                            contentDescription = "Cerrar Sesión",
                            tint = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // 🟢 BOTÓN 1: MI LIGA (Verde)
                FullWidthDashboardCard(
                    title = "Mi Liga",
                    subtitle = "Entrar a Equipo y Mercado",
                    backgroundColor = Color(0xFF238636),
                    icon = Icons.Default.PlayArrow,
                    onClick = {
                        dashboardViewModel.verificarYEntrarALigas {
                            onNavigateToMiAlineacion()
                        }
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))

                // ➕ BOTÓN 2: CONFIGURAR LIGA (Gris translúcido)
                FullWidthDashboardCard(
                    title = "Configurar Liga",
                    subtitle = "Crear o introducir código de acceso",
                    backgroundColor = Color.White.copy(alpha = 0.08f),
                    contentColor = Color.White,
                    icon = Icons.Default.AddCircle,
                    onClick = onNavigateToCrearLiga
                )

                Spacer(modifier = Modifier.height(16.dp))

                // 📊 FILA EN GRID: PERFIL Y CLASIFICACIÓN
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // 🔵 CARD: MI PERFIL
                    GridDashboardCard(
                        title = "Mi Perfil",
                        subtitle = "Editar cuenta",
                        backgroundColor = Color(0xFF2B4C7E),
                        icon = Icons.Default.Person,
                        onClick = onNavigateToPerfil,
                        modifier = Modifier.weight(1f)
                    )

                    // 🟠 CARD: CLASIFICACIÓN
                    GridDashboardCard(
                        title = "Clasificación",
                        subtitle = "Tabla de posiciones",
                        backgroundColor = Color(0xFFFFA000),
                        icon = Icons.Default.Star,
                        onClick = {
                            dashboardViewModel.verificarYEntrarALigas {
                                onNavigateToClasificacion()
                            }
                        },
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(28.dp))

                // ⏱️ NUEVA SECCIÓN: PRÓXIMAS PRUEBAS DEL DÍA
                Text(
                    text = "Próximas Pruebas",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.padding(bottom = 14.dp)
                )

                // Tarjeta contenedora oscura para la lista de pruebas
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF161F3D)) // Azul oscuro integrado
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp)
                    ) {
                        // Evento 1
                        PruebaRowItem(hora = "10:30", nombre = "100m Lisos", atletas = "Atletas: J. Bolt, M. Jacobs")
                        HorizontalDivider(modifier = Modifier.padding(vertical = 14.dp), color = Color.White.copy(alpha = 0.08f))

                        // Evento 2
                        PruebaRowItem(hora = "11:15", nombre = "Salto de Altura", atletas = "Atletas: G. Tamberi, M. Barshim")
                        HorizontalDivider(modifier = Modifier.padding(vertical = 14.dp), color = Color.White.copy(alpha = 0.08f))

                        // Evento 3
                        PruebaRowItem(hora = "12:00", nombre = "400m Vallas", atletas = "Atletas: K. Warholm, R. Benjamin")
                    }
                }

                // ⚠️ MENSAJE DE ERROR FLOTANTE (Si existe)
                dashboardViewModel.avisoError.value?.let { mensaje ->
                    Spacer(modifier = Modifier.height(20.dp))
                    Text(
                        text = mensaje,
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
    }
}

// --- COMPONENTE AUXILIAR PARA CADA PRUEBA DE LA LISTA ---
@Composable
fun PruebaRowItem(hora: String, nombre: String, atletas: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Hora destacada en color dorado
        Text(
            text = hora,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFFFFB300),
            modifier = Modifier.width(65.dp)
        )

        // Detalles de la competición
        Column {
            Text(
                text = nombre,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = atletas,
                fontSize = 13.sp,
                color = Color.White.copy(alpha = 0.5f)
            )
        }
    }
}

// --- COMPONENTES BASE DE TARJETAS ---

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FullWidthDashboardCard(
    title: String,
    subtitle: String,
    backgroundColor: Color,
    icon: ImageVector,
    onClick: () -> Unit,
    contentColor: Color = Color.White
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor)
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 24.dp, vertical = 20.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(text = title, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = contentColor)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = subtitle, fontSize = 14.sp, color = contentColor.copy(alpha = 0.7f))
            }
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = contentColor,
                modifier = Modifier.size(26.dp)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GridDashboardCard(
    title: String,
    subtitle: String,
    backgroundColor: Color,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier.height(135.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
        ) {
            Column(modifier = Modifier.align(Alignment.TopStart)) {
                Text(text = title, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
                Spacer(modifier = Modifier.height(2.dp))
                Text(text = subtitle, fontSize = 13.sp, color = Color.White.copy(alpha = 0.7f))
            }
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier
                    .size(24.dp)
                    .align(Alignment.BottomEnd)
            )
        }
    }
}
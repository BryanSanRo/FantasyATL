package com.example.fantasyatl

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.fantasyatl.data.SessionManager
import com.example.fantasyatl.ui.alineacion.AlineacionScreen
import com.example.fantasyatl.ui.auth.LoginScreen
import com.example.fantasyatl.ui.auth.RegisterScreen
import com.example.fantasyatl.ui.auth.RecuperacionScreen
import com.example.fantasyatl.ui.clasificacion.ClasificacionScreen
import com.example.fantasyatl.ui.home.HomeDashboardScreen
import com.example.fantasyatl.ui.home.MainAppLayout
import com.example.fantasyatl.ui.liga.LigaScreen
import com.example.fantasyatl.ui.liga.LigaViewModel
import com.example.fantasyatl.ui.market.MercadoScreen
import com.example.fantasyatl.ui.perfil.PerfilScreen
import com.example.fantasyatl.ui.plantilla.PlantillaScreen
import com.example.fantasyatl.ui.plantilla.PlantillaViewModel
import com.example.fantasyatl.ui.puntos.PuntosScreen
import com.example.fantasyatl.ui.theme.FantasyATLTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FantasyATLTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()

                    NavHost(navController = navController, startDestination = "login") {

                        // --- LOGIN ---
                        composable("login") {
                            LoginScreen(
                                onLoginSuccess = {
                                    navController.navigate("dashboard") {
                                        popUpTo("login") { inclusive = true }
                                    }
                                },
                                onNavigateToRegister = {
                                    navController.navigate("register")
                                },
                                onOlvidePassword = {
                                    navController.navigate("recuperacion")
                                }
                            )
                        }

                        // --- REGISTRO ---
                        composable("register") {
                            RegisterScreen(
                                onRegisterSuccess = { navController.popBackStack() },
                                onNavigateToLogin = { navController.popBackStack() }
                            )
                        }

                        // --- RECUPERACIÓN CONTRASEÑA ---
                        composable("recuperacion") {
                            RecuperacionScreen(
                                onVolver = { navController.popBackStack() }
                            )
                        }

                        // --- DASHBOARD (primera pantalla tras login) ---
                        composable("dashboard") {
                            HomeDashboardScreen(
                                onIrAHome = {
                                    navController.navigate("home")
                                },
                                onIrAPerfil = {
                                    navController.navigate("perfil")
                                },
                                onCerrarSesion = {
                                    SessionManager.cerrarSesion()
                                    navController.navigate("login") {
                                        popUpTo("dashboard") { inclusive = true }
                                    }
                                }
                            )
                        }

                        composable("liga") {
                            val ligaViewModel: LigaViewModel = viewModel()
                            LigaScreen(
                                viewModel = ligaViewModel,
                                onLigaConfigurada = {
                                    navController.navigate("home") {
                                        popUpTo("liga") { inclusive = true }
                                    }
                                }
                            )
                        }

                        composable("home") {
                            var seccionActual by remember { mutableIntStateOf(0) }
                            val plantillaViewModel: PlantillaViewModel = viewModel()

                            MainAppLayout(
                                nombreUsuario = SessionManager.usuarioActual?.nombre ?: "Usuario",
                                nombreLiga = SessionManager.ligaActual?.nombre ?: "Mi Liga",
                                saldo = "%,d".format(plantillaViewModel.presupuesto.value) + " €",
                                seccionSeleccionada = seccionActual,
                                onSeccionSelected = { seccionActual = it },
                                onBackToDashboard = {
                                    navController.navigate("dashboard") {
                                        popUpTo("home") { inclusive = true }
                                    }
                                }
                            ) { paddingValues ->
                                when (seccionActual) {
                                    0 -> AlineacionScreen(paddingValues, plantillaViewModel)
                                    1 -> PlantillaScreen(paddingValues, plantillaViewModel)
                                    2 -> MercadoScreen(paddingValues, plantillaViewModel)
                                    3 -> PuntosScreen(paddingValues)
                                    4 -> ClasificacionScreen(paddingValues)
                                }
                            }
                        }

                        // --- PERFIL (editar cuenta) ---
                        composable("perfil") {
                            PerfilScreen(
                                onVolver = { navController.popBackStack() }
                            )
                        }
                    }
                }
            }
        }
    }
}
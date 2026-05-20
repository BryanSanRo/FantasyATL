package com.example.fantasyatl

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.fantasyatl.data.SessionDB.SessionManager
import com.example.fantasyatl.ui.alineacion.AlineacionScreen
import com.example.fantasyatl.ui.auth.LoginScreen
import com.example.fantasyatl.ui.auth.RecuperacionScreen
import com.example.fantasyatl.ui.auth.RegisterScreen
import com.example.fantasyatl.ui.home.HomeDashboardScreen
import com.example.fantasyatl.ui.home.HomeDashboardViewModel
import com.example.fantasyatl.ui.home.MainAppLayout
import com.example.fantasyatl.ui.liga.LigaScreen
import com.example.fantasyatl.ui.market.AtletaViewModel
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
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    val navController = rememberNavController()
                    NavHost(navController = navController, startDestination = "login") {

                        composable("login") {
                            LoginScreen(
                                onLoginSuccess       = { navController.navigate("dashboard") { popUpTo("login") { inclusive = true } } },
                                onNavigateToRegister = { navController.navigate("register") },
                                onOlvidePassword     = { navController.navigate("recuperacion") }
                            )
                        }
                        composable("register") {
                            RegisterScreen(
                                onRegisterSuccess = { navController.popBackStack() },
                                onNavigateToLogin = { navController.popBackStack() }
                            )
                        }
                        composable("recuperacion") {
                            RecuperacionScreen(onVolver = { navController.popBackStack() })
                        }
                        composable("dashboard") {
                            val dashboardViewModel: HomeDashboardViewModel = viewModel()
                            HomeDashboardScreen(
                                dashboardViewModel       = dashboardViewModel,
                                onNavigateToMiAlineacion = { navController.navigate("home?section=0") },
                                onNavigateToPerfil       = { navController.navigate("perfil") },
                                onNavigateToCrearLiga    = { navController.navigate("liga") },
                                onLogout = {
                                    SessionManager.cerrarSesion()
                                    navController.navigate("login") { popUpTo("dashboard") { inclusive = true } }
                                }
                            )
                        }
                        composable("liga") {
                            LigaScreen(onVolverAlDashboard = {
                                navController.navigate("dashboard") { popUpTo("liga") { inclusive = true } }
                            })
                        }
                        composable(
                            route = "home?section={section}",
                            arguments = listOf(navArgument("section") { type = NavType.IntType; defaultValue = 0 })
                        ) { backStackEntry ->
                            val sectionParam = backStackEntry.arguments?.getInt("section") ?: 0
                            var seccionActual by remember { mutableIntStateOf(sectionParam) }

                            val dashboardViewModel: HomeDashboardViewModel = viewModel()
                            val plantillaViewModel: PlantillaViewModel     = viewModel()
                            val atletaViewModel:    AtletaViewModel        = viewModel()

                            LaunchedEffect(Unit) {
                                dashboardViewModel.cargarDatosDashboardSilencioso()
                                plantillaViewModel.cargarPlantilla()
                            }

                            MainAppLayout(
                                nombreUsuario       = SessionManager.usuarioActual?.nombre ?: "Usuario",
                                nombreLiga          = SessionManager.ligaActual?.nombre ?: "Mi Liga",
                                saldo               = "%,d".format(plantillaViewModel.presupuesto.value) + " €",
                                seccionSeleccionada = seccionActual,
                                onSeccionSelected   = { seccionActual = it },
                                dashboardViewModel  = dashboardViewModel,
                                onBackToDashboard   = {
                                    navController.navigate("dashboard") {
                                        popUpTo("home?section={section}") { inclusive = true }
                                    }
                                },
                                onLigaCambiada = { plantillaViewModel.cargarPlantilla() }
                            ) { paddingValues ->
                                Box(modifier = Modifier.padding(paddingValues)) {
                                    when (seccionActual) {
                                        0 -> AlineacionScreen(PaddingValues(0.dp), plantillaViewModel)
                                        1 -> PlantillaScreen(PaddingValues(0.dp), plantillaViewModel)
                                        2 -> MercadoScreen(PaddingValues(0.dp), plantillaViewModel, atletaViewModel)
                                        3 -> PuntosScreen(PaddingValues(0.dp))
                                    }
                                }
                            }
                        }
                        composable("perfil") {
                            PerfilScreen(onVolver = { navController.popBackStack() })
                        }
                    }
                }
            }
        }
    }
}

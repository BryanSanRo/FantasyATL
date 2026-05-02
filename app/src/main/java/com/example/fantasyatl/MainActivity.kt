package com.example.fantasyatl

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.fantasyatl.data.SessionManager
import com.example.fantasyatl.ui.auth.LoginScreen
import com.example.fantasyatl.ui.auth.RegisterScreen
import com.example.fantasyatl.ui.equipo.EquipoScreen
import com.example.fantasyatl.ui.home.MainAppLayout
import com.example.fantasyatl.ui.market.MercadoScreen
import com.example.fantasyatl.ui.perfil.PerfilScreen
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

                        composable("login") {
                            LoginScreen(
                                onLoginSuccess = {
                                    navController.navigate("home") {
                                        popUpTo("login") { inclusive = true }
                                    }
                                },
                                onNavigateToRegister = {
                                    navController.navigate("register")
                                }
                            )
                        }

                        composable("register") {
                            RegisterScreen(
                                onRegisterSuccess = {
                                    navController.popBackStack()
                                },
                                onNavigateToLogin = {
                                    navController.navigate("login") {
                                        popUpTo("register") { inclusive = true }
                                    }
                                }
                            )
                        }

                        // ✅ CORRECCIÓN: pasamos onCerrarSesion al composable
                        composable("home") {
                            AppNavigation(
                                onCerrarSesion = {
                                    navController.navigate("login") {
                                        // Limpia todo el historial
                                        popUpTo(0) { inclusive = true }
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AppNavigation(onCerrarSesion: () -> Unit) {
    var seccionActual by remember { mutableIntStateOf(0) }

    val nombreUsuario = SessionManager.usuarioActual?.nombre ?: "Usuario"

    MainAppLayout(
        nombreUsuario = nombreUsuario,
        nombreLiga = "Liga de Grado Superior",
        saldo = "15.000.000 €",
        seccionSeleccionada = seccionActual,
        onSeccionSelected = { nuevaSeccion -> seccionActual = nuevaSeccion },
        onCerrarSesion = onCerrarSesion
    ) { paddingValues ->
        when (seccionActual) {
            0 -> PerfilScreen(paddingValues)
            1 -> EquipoScreen(paddingValues)
            2 -> MercadoScreen(paddingValues)
        }
    }
}
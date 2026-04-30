package com.example.fantasyatl

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
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

                        // PANTALLA DE LOGIN
                        composable("login") {
                            LoginScreen(
                                onLoginSuccess = {
                                    navController.navigate("home") {
                                        // Evita que el usuario vuelva al login con el botón atrás
                                        popUpTo("login") { inclusive = true }
                                    }
                                },
                                onNavigateToRegister = {
                                    navController.navigate("register")
                                }
                            )
                        }

                        // PANTALLA DE REGISTRO (Corregida)
                        composable("register") {
                            RegisterScreen(
                                onRegisterSuccess = {
                                    // Al registrarse con éxito, volvemos al login
                                    navController.popBackStack()
                                },
                                onNavigateToLogin = {
                                    // Si pulsa en "Ya tengo cuenta", volvemos atrás
                                    navController.navigate("login") {
                                        popUpTo("register") { inclusive = true }
                                    }
                                }
                            ) // Aquí se cierran los parámetros de RegisterScreen
                        }

                        // PANTALLA PRINCIPAL
                        composable("home") {
                            AppNavigation()
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AppNavigation() {
    var seccionActual by remember { mutableIntStateOf(0) }

    MainAppLayout(
        nombreUsuario = "Ramses",
        nombreLiga = "Liga de Grado Superior",
        saldo = "15.000.000 €",
        seccionSeleccionada = seccionActual,
        onSeccionSelected = { nuevaSeccion -> seccionActual = nuevaSeccion }
    ) { paddingValues ->
        when (seccionActual) {
            0 -> PerfilScreen(paddingValues)
            1 -> EquipoScreen(paddingValues)
            2 -> MercadoScreen(paddingValues)
        }
    }
}
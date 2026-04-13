package com.example.fantasyatl

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController


import com.example.fantasyatl.ui.auth.LoginScreen
import com.example.fantasyatl.ui.home.MainAppLayout
import com.example.fantasyatl.ui.perfil.PerfilScreen
import com.example.fantasyatl.ui.equipo.EquipoScreen
import com.example.fantasyatl.ui.market.MercadoScreen
import com.example.fantasyatl.ui.theme.FantasyATLTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FantasyATLTheme {
             
                val navController = rememberNavController()

                NavHost(navController = navController, startDestination = "login") {

                    // PANTALLA 1: LOGIN
                    composable("login") {
                        // OJO: Asegúrate de que tu LoginScreen acepte este onLoginSuccess (como te expliqué en el mensaje anterior)
                        LoginScreen(onLoginSuccess = {
                            navController.navigate("home") {
                                popUpTo("login") { inclusive = true }
                            }
                        })
                    }

                    // PANTALLA 2: LA APP PRINCIPAL
                    composable("home") {
                        AppNavigation()
                    }
                }
            }
        }
    }
}

@Composable
fun AppNavigation() {
    // 0 = Perfil, 1 = Equipo, 2 = Mercado
    var seccionActual by remember { mutableIntStateOf(0) }

    MainAppLayout(
        nombreUsuario = "Ramses",
        nombreLiga = "Liga de Grado Superior",
        saldo = "15.000.000 €"
    ) { paddingValues ->

        when (seccionActual) {
            0 -> PerfilScreen(paddingValues)
            1 -> EquipoScreen(paddingValues)
            2 -> MercadoScreen(paddingValues)
        }
    }
}
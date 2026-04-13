package com.example.fantasyatl.ui.equipo

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun EquipoScreen(paddingValues: PaddingValues) {
    var tabSeleccionada by remember { mutableIntStateOf(0) }
    val titulosTabs = listOf("Alineación Hoy", "Mi Plantilla", "Puntos Generados")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
    ) {
        TabRow(selectedTabIndex = tabSeleccionada) {
            titulosTabs.forEachIndexed { index, titulo ->
                Tab(
                    selected = tabSeleccionada == index,
                    onClick = { tabSeleccionada = index },
                    text = { Text(titulo) }
                )
            }
        }

        Box(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            when (tabSeleccionada) {
                0 -> Text("Alineación elegida para competir hoy (Ej: 4 atletas)")
                1 -> Text("Lista de TODOS los atletas que tienes comprados")
                2 -> Text("Desglose de puntos: \n- Atleta 1: +15 pts\n- Atleta 2: +8 pts")
            }
        }
    }
}
package com.example.fantasyatl.ui.market

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
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
fun MercadoScreen(paddingValues: PaddingValues) {
    var tabSeleccionada by remember { mutableIntStateOf(0) }
    val titulosTabs = listOf("En Venta", "Operaciones")

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

        when (tabSeleccionada) {
            0 -> {
                // Pestaña 1: Lista de 8 atletas a la venta
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(8) { index ->
                        Card(modifier = Modifier.fillMaxWidth()) {
                            Row(
                                modifier = Modifier.padding(16.dp).fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Atleta Libre #${index + 1}")
                                Text("Valor: 2M €")
                            }
                        }
                    }
                }
            }
            1 -> {
                // Pestaña 2: Operaciones (Compras y Ventas divididas en la pantalla)
                Row(
                    modifier = Modifier.fillMaxSize().padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Columna Izquierda: Compras
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Últimas Compras", style = MaterialTheme.typography.titleMedium)
                        Spacer(modifier = Modifier.height(8.dp))
                        Card(modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)) {
                            Text("Has comprado a X por 1.5M", modifier = Modifier.padding(8.dp))
                        }
                    }

                    // Separador visual
                    Divider(modifier = Modifier.width(1.dp).fillMaxHeight())

                    // Columna Derecha: Ventas
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Últimas Ventas", style = MaterialTheme.typography.titleMedium)
                        Spacer(modifier = Modifier.height(8.dp))
                        Card(modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)) {
                            Text("Has vendido a Y por 3M", modifier = Modifier.padding(8.dp))
                        }
                    }
                }
            }
        }
    }
}
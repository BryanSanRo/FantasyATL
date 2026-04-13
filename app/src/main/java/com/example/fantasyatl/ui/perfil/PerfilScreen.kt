package com.example.fantasyatl.ui.perfil

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun PerfilScreen(paddingValues: PaddingValues) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Cuadrado 1: Horas de competiciones
        Card(modifier = Modifier.fillMaxWidth().weight(1f)) {
            Box(modifier = Modifier.fillMaxSize().padding(16.dp), contentAlignment = Alignment.Center) {
                Text("Próximas Competiciones\n10:00 - Atletismo\n12:30 - Natación", textAlign = androidx.compose.ui.text.style.TextAlign.Center)
            }
        }

        // Cuadrado 2: Liga y Posición
        Card(modifier = Modifier.fillMaxWidth().weight(1f)) {
            Box(modifier = Modifier.fillMaxSize().padding(16.dp), contentAlignment = Alignment.Center) {
                Text("Liga Oro Olimpiadas\nPosición actual: 3º", textAlign = androidx.compose.ui.text.style.TextAlign.Center)
            }
        }

        // Cuadrado 3: Noticias
        Card(modifier = Modifier.fillMaxWidth().weight(1f)) {
            Box(modifier = Modifier.fillMaxSize().padding(16.dp), contentAlignment = Alignment.Center) {
                Text("Noticias\nNuevo récord mundial establecido en los 100m lisos.", textAlign = androidx.compose.ui.text.style.TextAlign.Center)
            }
        }
    }
}
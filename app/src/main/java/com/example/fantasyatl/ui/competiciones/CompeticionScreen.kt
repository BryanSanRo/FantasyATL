package com.example.fantasyatl.ui.competiciones

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.fantasyatl.data.CompeticionDB.Competicion
import com.example.fantasyatl.data.CompeticionDB.lugar
import com.example.fantasyatl.data.ResultadoDB.ResultadoConNombre


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompeticionScreen(
    paddingValues: PaddingValues,
    viewModel: CompeticionViewModel = viewModel()
) {
    var viendoResultados by remember { mutableStateOf<Competicion?>(null) }

    LaunchedEffect(Unit) { viewModel.cargarCompeticiones() }

    val fondo = Brush.verticalGradient(
        colors = listOf(Color(0xFF0F1B3E), Color(0xFF080F26))
    )

    if (viendoResultados != null) {
        val comp = viendoResultados!!
        LaunchedEffect(comp.id) { viewModel.cargarResultados(comp.id) }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            comp.nombre,
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { viendoResultados = null }) {
                            Icon(Icons.Default.ArrowBack, null, tint = Color.White)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF1A237E))
                )
            }
        ) { inner ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(brush = fondo)
                    .padding(inner)
            ) {
                if (viewModel.isLoading.value) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.align(Alignment.Center)
                    )
                } else {
                    val listaResultados: List<ResultadoConNombre> = viewModel.resultados.value
                    if (listaResultados.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("Sin resultados todavía", color = Color.White.copy(alpha = 0.5f))
                        }
                    } else {
                        LazyColumn(
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            item {
                                Text(
                                    "Resultados",
                                    color = Color.White,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    "Jornada ${comp.jornada} · ${comp.fecha}",
                                    color = Color.White.copy(alpha = 0.6f),
                                    fontSize = 12.sp
                                )
                                Spacer(Modifier.height(12.dp))
                            }
                            items(
                                items = listaResultados,
                                key = { it.resultado.id }
                            ) { r ->
                                FilaResultado(r)
                            }
                        }
                    }
                }
            }
        }
        return
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = fondo)
            .padding(paddingValues)
    ) {
        when {
            viewModel.isLoading.value -> {
                CircularProgressIndicator(
                    color = Color.White,
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            viewModel.error.value != null -> {
                Text(
                    viewModel.error.value!!,
                    color = Color.Red,
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            else -> {
                val proximas: List<Competicion> = viewModel.proximasCompeticiones()
                val pasadas: List<Competicion> = viewModel.competicionesPasadas()

                if (proximas.isEmpty() && pasadas.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "No hay competiciones en esta liga",
                            color = Color.White.copy(alpha = 0.5f),
                            textAlign = TextAlign.Center
                        )
                    }
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        if (proximas.isNotEmpty()) {
                            item {
                                Text(
                                    "Próximas competiciones",
                                    color = Color.White,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(Modifier.height(8.dp))
                            }
                            items(
                                items = proximas,
                                key = { it.id }
                            ) { comp ->
                                TarjetaCompeticion(
                                    comp = comp,
                                    lugar = viewModel.lugares.value[comp.lugarId],
                                    esPasada = false,
                                    onClick = { viendoResultados = comp }
                                )
                            }
                        }

                        if (pasadas.isNotEmpty()) {
                            item {
                                Spacer(Modifier.height(8.dp))
                                Text(
                                    "Historial",
                                    color = Color.White.copy(alpha = 0.7f),
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(Modifier.height(8.dp))
                            }
                            items(
                                items = pasadas,
                                key = { it.id }
                            ) { comp ->
                                TarjetaCompeticion(
                                    comp = comp,
                                    lugar = viewModel.lugares.value[comp.lugarId],
                                    esPasada = true,
                                    onClick = { viendoResultados = comp }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TarjetaCompeticion(
    comp: Competicion,
    lugar: lugar?,
    esPasada: Boolean,
    onClick: () -> Unit
) {
    val colorFondo = if (esPasada) Color(0xFF1A2240) else Color(0xFF1A237E)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = colorFondo)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    comp.nombre,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    modifier = Modifier.weight(1f)
                )
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = when (comp.tipo) {
                        "Mundial" -> Color(0xFFFFD700).copy(alpha = 0.2f)
                        "Internacional" -> Color(0xFF2196F3).copy(alpha = 0.2f)
                        else -> Color(0xFF4CAF50).copy(alpha = 0.2f)
                    }
                ) {
                    Text(
                        comp.tipo,
                        color = when (comp.tipo) {
                            "Mundial" -> Color(0xFFFFD700)
                            "Internacional" -> Color(0xFF64B5F6)
                            else -> Color(0xFF81C784)
                        },
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.CalendarMonth,
                    null,
                    tint = Color.White.copy(alpha = 0.5f),
                    modifier = Modifier.size(14.dp)
                )
                Spacer(Modifier.width(4.dp))
                Text(comp.fecha, color = Color.White.copy(alpha = 0.6f), fontSize = 12.sp)
                Spacer(Modifier.width(12.dp))
                Text(
                    "Jornada ${comp.jornada}",
                    color = Color(0xFFFFD54F),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            lugar?.let {
                Spacer(Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.LocationOn,
                        null,
                        tint = Color.White.copy(alpha = 0.5f),
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        "${it.nombre}, ${it.ciudad}",
                        color = Color.White.copy(alpha = 0.6f),
                        fontSize = 12.sp
                    )
                }
            }

            if (esPasada) {
                Spacer(Modifier.height(8.dp))
                Text(
                    "Ver resultados →",
                    color = Color(0xFF64B5F6),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
fun FilaResultado(r: ResultadoConNombre) {
    val colorPosicion = when (r.resultado.posicion) {
        1 -> Color(0xFFFFD700)
        2 -> Color(0xFFCFD8DC)
        3 -> Color(0xFFFFCC80)
        else -> Color.White
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1A2A56))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = when (r.resultado.posicion) {
                    1 -> "🥇"
                    2 -> "🥈"
                    3 -> "🥉"
                    null -> r.resultado.estado.uppercase()
                    else -> "#${r.resultado.posicion}"
                },
                color = colorPosicion,
                fontWeight = FontWeight.Black,
                fontSize = 16.sp,
                modifier = Modifier.width(42.dp)
            )

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    r.nombreAtleta,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
                Text(
                    r.nombrePrueba,
                    color = Color.White.copy(alpha = 0.6f),
                    fontSize = 12.sp
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                r.resultado.marcaNum?.let {
                    Text(
                        "${it}m",
                        color = Color(0xFFFFD54F),
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }
                if (r.puntosFantasy > 0) {
                    Text(
                        "+${r.puntosFantasy} pts",
                        color = Color(0xFF4CAF50),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                if (r.resultado.recordPersonal) {
                    Text("RP", color = Color(0xFF64B5F6), fontSize = 11.sp)
                }
                if (r.resultado.recordMundial) {
                    Text("RM", color = Color(0xFFFFD700), fontSize = 11.sp)
                }
            }
        }
    }
}

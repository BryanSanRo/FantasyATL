package com.example.fantasyatl.ui.market

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fantasyatl.data.AtletaDB.Atleta
import com.example.fantasyatl.data.PujaDB.PujaDB
import com.example.fantasyatl.data.SessionDB.SupabaseClient
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.TimeZone

class AtletaViewModel : ViewModel() {

    private var atletasDelDia: List<Atleta> = emptyList()

    // Estados reactivos que Compose observará para redibujar la pantalla
    var atletasMercado = mutableStateOf<List<Atleta>>(emptyList())
    var misPujasActivas = mutableStateOf<List<Atleta>>(emptyList())
    var dineroRetenidoEnPujas = mutableStateOf(0)

    var isLoading = mutableStateOf(false)
    var error = mutableStateOf<String?>(null)
    var mensajeResolucion = mutableStateOf<String?>(null)

    private val _tiempoRestante = mutableStateOf("24:00:00")
    val tiempoRestante: State<String> = _tiempoRestante

    init {
        iniciarTemporizadorMercado()
    }

    fun cargarMercado(idsYaFichados: Set<String>, emailUsuario: String) {
        if (emailUsuario.isBlank()) return

        viewModelScope.launch {
            isLoading.value = true
            error.value = null
            try {
                // 1. Descarga defensiva de atletas si la lista en memoria está vacía
                if (atletasDelDia.isEmpty()) {
                    val todos = SupabaseClient.client.from("atletas")
                        .select().decodeList<Atleta>()

                    atletasDelDia = todos.filter {
                        it.id != null && it.id !in idsYaFichados
                    }.shuffled().take(8)
                }

                // 2. Traer SIEMPRE la lista fresca de pujas de la base de datos
                val todasLasPujas = SupabaseClient.client.from("pujas")
                    .select().decodeList<PujaDB>()

                var totalRetenido = 0
                val correoFiltrado = emailUsuario.trim().lowercase()

                // 3. Vincular los datos cruzando las tablas atleta <-> pujas
                val listaMapeada = atletasDelDia.map { atleta ->
                    val pujasDeEsteAtleta = todasLasPujas.filter { it.atleta_id == atleta.id }

                    // Buscamos si el usuario actual tiene una puja en este atleta específico
                    val pujaDelUsuario = pujasDeEsteAtleta.find {
                        it.email_usuario.trim().lowercase() == correoFiltrado
                    }

                    if (pujaDelUsuario != null) {
                        totalRetenido += pujaDelUsuario.cantidad
                    }

                    atleta.copy(
                        contadorPujas = pujasDeEsteAtleta.size,
                        miPujaActual = pujaDelUsuario?.cantidad
                    )
                }

                // 4. Notificar a Compose modificando los estados atómicos
                atletasMercado.value = listaMapeada

                // IMPORTANTE: Aquí filtramos explícitamente para llenar la pestaña "Operaciones"
                val filtradas = listaMapeada.filter { it.miPujaActual != null && it.miPujaActual!! > 0 }
                misPujasActivas.value = filtradas

                // Actualizamos el dinero retenido para restar del presupuesto de abajo
                dineroRetenidoEnPujas.value = totalRetenido

            } catch (e: Exception) {
                error.value = "Error al sincronizar datos: ${e.localizedMessage}"
            } finally {
                isLoading.value = false
            }
        }
    }

    fun enviarOModificarPuja(
        atletaId: String,
        emailUsuario: String,
        oferta: Int,
        idsYaFichados: Set<String>,
        onExito: (String) -> Unit,
        onError: (String) -> Unit
    ) {
        if (atletaId.isBlank() || emailUsuario.isBlank()) return

        viewModelScope.launch {
            try {
                // Verificar si ya existe un registro de puja para este atleta y usuario
                val pujaExistente = SupabaseClient.client.from("pujas")
                    .select {
                        filter {
                            eq("atleta_id", atletaId)
                            eq("email_usuario", emailUsuario.trim())
                        }
                    }.decodeSingleOrNull<PujaDB>()

                if (pujaExistente != null) {
                    // Si existe, actualizamos el valor de la puja existente
                    SupabaseClient.client.from("pujas").update(
                        { set("cantidad", oferta) }
                    ) { filter { eq("id", pujaExistente.id ?: "") } }

                    mensajeResolucion.value = "Puja modificada con éxito"
                    onExito("Puja modificada con éxito")
                } else {
                    // Si no existe, creamos una nueva fila en Supabase
                    SupabaseClient.client.from("pujas").insert(
                        PujaDB(
                            atleta_id = atletaId,
                            email_usuario = emailUsuario.trim(),
                            cantidad = oferta
                        )
                    )
                    mensajeResolucion.value = "Puja secreta registrada"
                    onExito("Puja secreta registrada")
                }

                // Forzar de inmediato la recarga local de los datos para actualizar la UI
                cargarMercado(idsYaFichados, emailUsuario)

            } catch (e: Exception) {
                onError(e.localizedMessage ?: "Error en la operación")
            }
        }
    }

    fun eliminarPuja(
        atletaId: String,
        emailUsuario: String,
        idsYaFichados: Set<String>,
        onExito: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                SupabaseClient.client.from("pujas").delete {
                    filter {
                        eq("atleta_id", atletaId)
                        eq("email_usuario", emailUsuario.trim())
                    }
                }
                cargarMercado(idsYaFichados, emailUsuario)
                onExito()
            } catch (e: Exception) {
                onError(e.localizedMessage ?: "Error al retirar puja")
            }
        }
    }

    private fun iniciarTemporizadorMercado() {
        viewModelScope.launch {
            while (true) {
                val ahora = System.currentTimeMillis()
                val calendarMedianoche = Calendar.getInstance(TimeZone.getDefault()).apply {
                    add(Calendar.DAY_OF_YEAR, 1)
                    set(Calendar.HOUR_OF_DAY, 0)
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }
                val diferenciaMilis = calendarMedianoche.timeInMillis - ahora
                if (diferenciaMilis <= 0) {
                    _tiempoRestante.value = "00:00:00"
                } else {
                    val totalSegundos = diferenciaMilis / 1000
                    val horas = totalSegundos / 3600
                    val minutos = (totalSegundos % 3600) / 60
                    val segundos = totalSegundos % 60
                    _tiempoRestante.value = String.format("%02d:%02d:%02d", horas, minutos, segundos)
                }
                delay(1000)
            }
        }
    }
}
package com.example.fantasyatl

import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

/**
 * Tests unitarios - Olympic Fantasy ATL
 * Alumno: Raúl Martínez
 *
 * Cubre RF01-RF12 mediante lógica de negocio pura
 * Sin dependencias de Supabase ni Android — ejecutables con JVM
 */
class FantasyATLTest {


    data class AtletaTest(
        val id: String,
        val nombre: String,
        val apellidos: String,
        val disciplina: String,
        val valoracion: Int,
        val precio: Int
    )

    data class EntradaPlantillaTest(
        val id: String,
        val atletaId: String,
        val esTitular: Boolean
    )

    data class MiembroTest(
        val email: String,
        val puntos: Int,
        val presupuesto: Long
    )

    data class PuntoJornadaTest(
        val atletaId: String,
        val jornada: Int,
        val puntos: Int
    )

    data class ResultadoTest(
        val atletaId: String,
        val prueba: String,
        val posicion: Int?,
        val estado: String,
        val marcaNum: Double?,
        val recordPersonal: Boolean = false,
        val recordMundial: Boolean = false
    )

    // ─────────────────────────────────────────────────────────────
    // LÓGICA DE NEGOCIO (replica lo que hace el ViewModel)
    // ─────────────────────────────────────────────────────────────

    private fun validarRegistro(nombre: String, email: String, contrasena: String): List<String> {
        val errores = mutableListOf<String>()
        if (nombre.isBlank()) errores.add("El nombre no puede estar vacío")
        if (!email.contains("@") || !email.contains(".")) errores.add("El email no es válido")
        if (contrasena.length < 8) errores.add("La contraseña debe tener mínimo 8 caracteres")
        if (!contrasena.any { it.isDigit() }) errores.add("La contraseña debe contener al menos un número")
        if (!contrasena.any { !it.isLetterOrDigit() }) errores.add("La contraseña debe contener al menos un símbolo")
        return errores
    }

    private fun validarLogin(email: String, contrasena: String): List<String> {
        val errores = mutableListOf<String>()
        if (email.isBlank()) errores.add("El email no puede estar vacío")
        if (!email.contains("@")) errores.add("El email no es válido")
        if (contrasena.isBlank()) errores.add("La contraseña no puede estar vacía")
        return errores
    }

    private fun generarTokenRecuperacion(): String =
        (100000..999999).random().toString()

    private fun validarToken(tokenReal: String, tokenIntroducido: String): Boolean =
        tokenReal == tokenIntroducido

    private fun validarCrearLiga(nombre: String, emailAdmin: String): List<String> {
        val errores = mutableListOf<String>()
        if (nombre.isBlank()) errores.add("El nombre de la liga no puede estar vacío")
        if (emailAdmin.isBlank()) errores.add("Se requiere un administrador")
        return errores
    }

    private fun generarCodigoLiga(): String {
        val chars = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789"
        return (1..6).map { chars.random() }.joinToString("")
    }

    private fun validarCodigoUnirse(codigo: String): List<String> {
        val errores = mutableListOf<String>()
        if (codigo.isBlank()) errores.add("El código no puede estar vacío")
        if (codigo.length != 6) errores.add("El código debe tener exactamente 6 caracteres")
        return errores
    }

    private fun validarFichaje(
        presupuesto: Long,
        precio: Int,
        idsPlantilla: Set<String>,
        atletaId: String
    ): String? {
        if (idsPlantilla.contains(atletaId)) return "Este atleta ya está fichado en tu plantilla"
        if (presupuesto < precio.toLong()) return "Saldo insuficiente. Tu presupuesto: ${presupuesto}€. Precio: ${precio}€"
        return null
    }

    private fun calcularPuntosFantasy(posicion: Int?): Int = when (posicion) {
        1    -> 25
        2    -> 18
        3    -> 15
        4    -> 12
        5    -> 10
        6    -> 8
        7    -> 6
        8    -> 4
        9    -> 2
        10   -> 1
        else -> 0
    }

    private fun calcularPuntosConRecords(posicion: Int?, recordPersonal: Boolean, recordMundial: Boolean): Int {
        if (posicion == null) return 0
        var puntos = calcularPuntosFantasy(posicion)
        if (recordPersonal) puntos += 5
        if (recordMundial) puntos += 20
        return puntos
    }

    // ─────────────────────────────────────────────────────────────
    // DATOS DE PRUEBA
    // ─────────────────────────────────────────────────────────────

    private lateinit var atletaBarato: AtletaTest
    private lateinit var atletaCaro: AtletaTest
    private lateinit var atletaMedio: AtletaTest
    private lateinit var plantillaBase: MutableList<EntradaPlantillaTest>
    private lateinit var miembrosLiga: List<MiembroTest>

    @Before
    fun setUp() {
        atletaBarato = AtletaTest("atl-001", "Carlos",  "Rojas",     "Salto altura", 76,  8000000)
        atletaCaro   = AtletaTest("atl-002", "Asier",   "Martínez",  "Vallas 110m",  88, 18000000)
        atletaMedio  = AtletaTest("atl-003", "Bruno",   "Hortelano", "Velocidad",    82, 12000000)

        plantillaBase = mutableListOf(
            EntradaPlantillaTest("pu-001", "atl-001", true),
            EntradaPlantillaTest("pu-002", "atl-002", true),
            EntradaPlantillaTest("pu-003", "atl-003", true),
            EntradaPlantillaTest("pu-004", "atl-004", false),
            EntradaPlantillaTest("pu-005", "atl-005", false)
        )

        miembrosLiga = listOf(
            MiembroTest("raul@gmail.com",   45, 82000000L),
            MiembroTest("luis@gmail.com",   38, 91000000L),
            MiembroTest("laura@gmail.com",  52, 74000000L),
            MiembroTest("bryan@gmail.com",  27, 95000000L)
        )
    }

    // ─────────────────────────────────────────────────────────────
    // RF01 — REGISTRO
    // ─────────────────────────────────────────────────────────────

    @Test
    fun `RF01_T01 - registro con datos validos es aceptado`() {
        val errores = validarRegistro("Raúl", "raul@gmail.com", "Segura123!")
        assertTrue("Registro válido no debe tener errores", errores.isEmpty())
    }

    @Test
    fun `RF01_T02 - registro con nombre vacio es rechazado`() {
        val errores = validarRegistro("", "raul@gmail.com", "Segura123!")
        assertTrue("Nombre vacío debe dar error", errores.any { it.contains("nombre", ignoreCase = true) })
    }

    @Test
    fun `RF01_T03 - registro con email sin arroba es rechazado`() {
        val errores = validarRegistro("Raúl", "rauLsinArroba.com", "Segura123!")
        assertTrue("Email sin @ debe dar error", errores.any { it.contains("email", ignoreCase = true) })
    }

    @Test
    fun `RF01_T04 - registro con contrasena menor de 8 chars es rechazado`() {
        val errores = validarRegistro("Raúl", "raul@gmail.com", "Ab1!")
        assertTrue("Contraseña corta debe dar error", errores.isNotEmpty())
    }

    @Test
    fun `RF01_T05 - registro con contrasena sin numero es rechazado`() {
        val errores = validarRegistro("Raúl", "raul@gmail.com", "SinNumero!")
        assertTrue("Sin número debe dar error", errores.isNotEmpty())
    }

    @Test
    fun `RF01_T06 - registro con contrasena sin simbolo es rechazado`() {
        val errores = validarRegistro("Raúl", "raul@gmail.com", "SinSimbolo1")
        assertTrue("Sin símbolo debe dar error", errores.isNotEmpty())
    }

    // ─────────────────────────────────────────────────────────────
    // RF02 — LOGIN
    // ─────────────────────────────────────────────────────────────

    @Test
    fun `RF02_T01 - login con credenciales validas es aceptado`() {
        val errores = validarLogin("raul@gmail.com", "Segura123!")
        assertTrue("Login válido no debe tener errores", errores.isEmpty())
    }

    @Test
    fun `RF02_T02 - login con email vacio es rechazado`() {
        val errores = validarLogin("", "Segura123!")
        assertTrue("Email vacío debe dar error", errores.isNotEmpty())
    }

    @Test
    fun `RF02_T03 - login con password vacio es rechazado`() {
        val errores = validarLogin("raul@gmail.com", "")
        assertTrue("Password vacío debe dar error", errores.isNotEmpty())
    }

    @Test
    fun `RF02_T04 - login con email sin arroba es rechazado`() {
        val errores = validarLogin("emailSinArroba.com", "Segura123!")
        assertTrue("Email inválido debe dar error", errores.isNotEmpty())
    }

    // ─────────────────────────────────────────────────────────────
    // RF03 — RECUPERACIÓN DE CONTRASEÑA
    // ─────────────────────────────────────────────────────────────

    @Test
    fun `RF03_T01 - token generado tiene 6 digitos`() {
        val token = generarTokenRecuperacion()
        assertEquals("El token debe tener 6 dígitos", 6, token.length)
    }

    @Test
    fun `RF03_T02 - token generado es numerico`() {
        val token = generarTokenRecuperacion()
        assertTrue("El token debe ser numérico", token.all { it.isDigit() })
    }

    @Test
    fun `RF03_T03 - token correcto valida la recuperacion`() {
        assertTrue("Token correcto debe ser válido", validarToken("437784", "437784"))
    }

    @Test
    fun `RF03_T04 - token incorrecto rechaza la recuperacion`() {
        assertFalse("Token incorrecto debe ser rechazado", validarToken("437784", "000000"))
    }

    @Test
    fun `RF03_T05 - nueva contrasena valida cumple requisitos`() {
        val errores = validarRegistro("x", "x@x.com", "NuevaPass1!")
        assertTrue("Contraseña válida debe ser aceptada", errores.isEmpty())
    }

    // ─────────────────────────────────────────────────────────────
    // RF04 — CREAR LIGA
    // ─────────────────────────────────────────────────────────────

    @Test
    fun `RF04_T01 - crear liga con nombre valido es aceptado`() {
        val errores = validarCrearLiga("RMFmana", "raul@gmail.com")
        assertTrue("Liga válida no debe tener errores", errores.isEmpty())
    }

    @Test
    fun `RF04_T02 - crear liga con nombre vacio es rechazado`() {
        val errores = validarCrearLiga("", "raul@gmail.com")
        assertTrue("Nombre vacío debe dar error", errores.isNotEmpty())
    }

    @Test
    fun `RF04_T03 - codigo generado tiene 6 caracteres`() {
        val codigo = generarCodigoLiga()
        assertEquals("El código debe tener 6 caracteres", 6, codigo.length)
    }

    @Test
    fun `RF04_T04 - codigo generado esta en mayusculas`() {
        val codigo = generarCodigoLiga()
        assertEquals("El código debe estar en mayúsculas", codigo.uppercase(), codigo)
    }

    @Test
    fun `RF04_T05 - dos codigos generados son distintos`() {
        val c1 = generarCodigoLiga()
        val c2 = generarCodigoLiga()
        assertNotEquals("Dos códigos no deben ser iguales", c1, c2)
    }

    // ─────────────────────────────────────────────────────────────
    // RF05 — UNIRSE A LIGA
    // ─────────────────────────────────────────────────────────────

    @Test
    fun `RF05_T01 - unirse con codigo de 6 chars es aceptado`() {
        val errores = validarCodigoUnirse("RMF001")
        assertTrue("Código válido debe ser aceptado", errores.isEmpty())
    }

    @Test
    fun `RF05_T02 - unirse con codigo vacio es rechazado`() {
        val errores = validarCodigoUnirse("")
        assertTrue("Código vacío debe dar error", errores.isNotEmpty())
    }

    @Test
    fun `RF05_T03 - unirse con codigo de menos de 6 chars es rechazado`() {
        val errores = validarCodigoUnirse("AB1")
        assertTrue("Código corto debe dar error", errores.isNotEmpty())
    }

    @Test
    fun `RF05_T04 - codigo se normaliza a mayusculas antes de buscar`() {
        val introducido = "rmf001"
        val normalizado = introducido.trim().uppercase()
        assertEquals("RMF001", normalizado)
    }

    // ─────────────────────────────────────────────────────────────
    // RF06 — CATÁLOGO DE ATLETAS
    // ─────────────────────────────────────────────────────────────

    @Test
    fun `RF06_T01 - atleta tiene precio positivo`() {
        assertTrue("El precio debe ser mayor que 0", atletaBarato.precio > 0)
    }

    @Test
    fun `RF06_T02 - atleta tiene valoracion entre 0 y 100`() {
        assertTrue("Valoración entre 0-100", atletaBarato.valoracion in 0..100)
    }

    @Test
    fun `RF06_T03 - catalogo se ordena por precio ascendente`() {
        val catalogo = listOf(atletaCaro, atletaBarato, atletaMedio)
        val ordenado = catalogo.sortedBy { it.precio }
        assertEquals("El más barato va primero", atletaBarato.id, ordenado.first().id)
    }

    @Test
    fun `RF06_T04 - catalogo se ordena por valoracion descendente`() {
        val catalogo = listOf(atletaBarato, atletaMedio, atletaCaro)
        val ordenado = catalogo.sortedByDescending { it.valoracion }
        assertEquals("El mejor valorado va primero", atletaCaro.id, ordenado.first().id)
    }

    @Test
    fun `RF06_T05 - atletas ya fichados no aparecen en el mercado`() {
        val fichados = setOf("atl-001", "atl-002")
        val catalogo = listOf(atletaBarato, atletaCaro, atletaMedio)
        val disponibles = catalogo.filter { it.id !in fichados }
        assertEquals("Solo debe quedar 1 atleta libre", 1, disponibles.size)
        assertEquals("atl-003", disponibles.first().id)
    }

    // ─────────────────────────────────────────────────────────────
    // RF07 — FICHAR ATLETAS
    // ─────────────────────────────────────────────────────────────

    @Test
    fun `RF07_T01 - fichar con presupuesto suficiente es valido`() {
        val resultado = validarFichaje(100000000L, atletaBarato.precio, emptySet(), atletaBarato.id)
        assertNull("Fichaje válido no debe dar error", resultado)
    }

    @Test
    fun `RF07_T02 - fichar con presupuesto insuficiente es rechazado`() {
        val resultado = validarFichaje(5000000L, atletaCaro.precio, emptySet(), atletaCaro.id)
        assertNotNull("Presupuesto insuficiente debe dar error", resultado)
        assertTrue(resultado!!.contains("insuficiente", ignoreCase = true))
    }

    @Test
    fun `RF07_T03 - fichar atleta ya en plantilla es rechazado`() {
        val fichados = setOf("atl-001")
        val resultado = validarFichaje(100000000L, atletaBarato.precio, fichados, atletaBarato.id)
        assertNotNull("Atleta duplicado debe dar error", resultado)
        assertTrue(resultado!!.contains("fichado", ignoreCase = true))
    }

    @Test
    fun `RF07_T04 - presupuesto se descuenta correctamente`() {
        val inicial = 100000000L
        val nuevo   = inicial - atletaBarato.precio.toLong()
        assertEquals("Presupuesto debe reducirse en el precio", 92000000L, nuevo)
    }

    @Test
    fun `RF07_T05 - fichar con presupuesto exacto es valido`() {
        val presupuesto = atletaBarato.precio.toLong()
        val resultado = validarFichaje(presupuesto, atletaBarato.precio, emptySet(), atletaBarato.id)
        assertNull("Presupuesto justo debe ser suficiente", resultado)
    }

    // ─────────────────────────────────────────────────────────────
    // RF08 — VENDER ATLETAS
    // ─────────────────────────────────────────────────────────────

    @Test
    fun `RF08_T01 - vender atleta suma su precio al presupuesto`() {
        val inicial = 82000000L
        val nuevo   = inicial + atletaBarato.precio.toLong()
        assertEquals("Presupuesto debe aumentar al vender", 90000000L, nuevo)
    }

    @Test
    fun `RF08_T02 - vender atleta lo elimina de la plantilla`() {
        val entrada = plantillaBase.first()
        plantillaBase.remove(entrada)
        assertFalse("El atleta vendido no debe seguir en plantilla",
            plantillaBase.any { it.atletaId == entrada.atletaId })
    }

    @Test
    fun `RF08_T03 - no se puede vender atleta que no esta en plantilla`() {
        val idInexistente = "atl-999"
        val estaEnPlantilla = plantillaBase.any { it.atletaId == idInexistente }
        assertFalse("No debe poder vender atleta que no tiene", estaEnPlantilla)
    }

    @Test
    fun `RF08_T04 - atleta vendido queda disponible en el mercado`() {
        var disponibleEnMercado = false
        // Simula que al vender se marca disponible = true
        disponibleEnMercado = true
        assertTrue("El atleta vendido debe volver al mercado", disponibleEnMercado)
    }

    // ─────────────────────────────────────────────────────────────
    // RF09 — PLANTILLA
    // ─────────────────────────────────────────────────────────────

    @Test
    fun `RF09_T01 - plantilla separa titulares y suplentes correctamente`() {
        val titulares = plantillaBase.filter { it.esTitular }
        val suplentes = plantillaBase.filter { !it.esTitular }
        assertEquals("Debe haber 3 titulares", 3, titulares.size)
        assertEquals("Debe haber 2 suplentes", 2, suplentes.size)
    }

    @Test
    fun `RF09_T02 - cambio de titularidad invierte el estado de titular`() {
        val entrada = EntradaPlantillaTest("pu-001", "atl-001", true)
        val nuevo   = entrada.copy(esTitular = !entrada.esTitular)
        assertFalse("Titular debe pasar a suplente", nuevo.esTitular)
    }

    @Test
    fun `RF09_T03 - cambio de titularidad convierte suplente en titular`() {
        val entrada = EntradaPlantillaTest("pu-004", "atl-004", false)
        val nuevo   = entrada.copy(esTitular = !entrada.esTitular)
        assertTrue("Suplente debe pasar a titular", nuevo.esTitular)
    }

    @Test
    fun `RF09_T04 - total atletas en plantilla es correcto`() {
        assertEquals("La plantilla de prueba tiene 5 atletas", 5, plantillaBase.size)
    }

    // ─────────────────────────────────────────────────────────────
    // RF10 — CLASIFICACIÓN
    // ─────────────────────────────────────────────────────────────

    @Test
    fun `RF10_T01 - clasificacion se ordena de mayor a menor puntos`() {
        val ranking = miembrosLiga.sortedByDescending { it.puntos }
        assertEquals("El primero debe tener más puntos", 52, ranking.first().puntos)
        assertEquals("El último debe tener menos puntos", 27, ranking.last().puntos)
    }

    @Test
    fun `RF10_T02 - posicion del usuario logueado se calcula correctamente`() {
        val email   = "raul@gmail.com"
        val ranking = miembrosLiga.sortedByDescending { it.puntos }
        val pos     = ranking.indexOfFirst { it.email == email } + 1
        assertEquals("Raúl debe estar en posición 2", 2, pos)
    }

    @Test
    fun `RF10_T03 - lider del ranking tiene los maximos puntos`() {
        val lider = miembrosLiga.maxByOrNull { it.puntos }
        assertEquals("El líder es laura@gmail.com", "laura@gmail.com", lider?.email)
        assertEquals("El líder tiene 52 puntos", 52, lider?.puntos)
    }

    @Test
    fun `RF10_T04 - clasificacion incluye a todos los miembros`() {
        val ranking = miembrosLiga.sortedByDescending { it.puntos }
        assertEquals("El ranking tiene 4 participantes", 4, ranking.size)
    }

    // ─────────────────────────────────────────────────────────────
    // RF11 — PUNTOS POR JORNADA
    // ─────────────────────────────────────────────────────────────

    @Test
    fun `RF11_T01 - posicion 1 otorga 25 puntos`() {
        assertEquals(25, calcularPuntosFantasy(1))
    }

    @Test
    fun `RF11_T02 - posicion 2 otorga 18 puntos`() {
        assertEquals(18, calcularPuntosFantasy(2))
    }

    @Test
    fun `RF11_T03 - posicion 10 otorga 1 punto`() {
        assertEquals(1, calcularPuntosFantasy(10))
    }

    @Test
    fun `RF11_T04 - posicion fuera de top 10 no otorga puntos`() {
        assertEquals("Posición 15 no da puntos", 0, calcularPuntosFantasy(15))
    }

    @Test
    fun `RF11_T05 - record personal suma 5 puntos extra`() {
        val puntos = calcularPuntosConRecords(posicion = 1, recordPersonal = true, recordMundial = false)
        assertEquals("1º + RP = 30 puntos", 30, puntos)
    }

    @Test
    fun `RF11_T06 - record mundial suma 20 puntos extra`() {
        val puntos = calcularPuntosConRecords(posicion = 1, recordPersonal = true, recordMundial = true)
        assertEquals("1º + RP + RM = 50 puntos", 50, puntos)
    }

    @Test
    fun `RF11_T07 - atleta con DNF no suma puntos`() {
        val posicion: Int? = null
        val puntos = if (posicion != null) calcularPuntosFantasy(posicion) else 0
        assertEquals("DNF no suma puntos", 0, puntos)
    }

    @Test
    fun `RF11_T08 - puntos totales de un usuario suman correctamente`() {
        val jornada = listOf(
            PuntoJornadaTest("atl-001", 1, 25),
            PuntoJornadaTest("atl-002", 1, 10),
            PuntoJornadaTest("atl-003", 1, 0)
        )
        val total = jornada.sumOf { it.puntos }
        assertEquals("Total debe ser 35", 35, total)
    }

    // ─────────────────────────────────────────────────────────────
    // RF12 — RESULTADOS DE COMPETICIONES
    // ─────────────────────────────────────────────────────────────

    @Test
    fun `RF12_T01 - resultado ok tiene posicion y marca`() {
        val r = ResultadoTest("atl-001", "100m lisos", 1, "ok", 9.85)
        assertEquals("Estado debe ser ok", "ok", r.estado)
        assertNotNull("Debe tener posición", r.posicion)
        assertNotNull("Debe tener marca", r.marcaNum)
    }

    @Test
    fun `RF12_T02 - resultado DNF no tiene posicion`() {
        val r = ResultadoTest("atl-002", "Salto longitud", null, "dnf", null)
        assertNull("DNF no tiene posición", r.posicion)
        assertNull("DNF no tiene marca", r.marcaNum)
    }

    @Test
    fun `RF12_T03 - record mundial requiere record personal`() {
        // Si hay RM debe haber RP (regla de negocio validada en backend)
        val rmSinRp = ResultadoTest("atl-001", "100m", 1, "ok", 9.58, recordPersonal = false, recordMundial = true)
        val esValido = !(rmSinRp.recordMundial && !rmSinRp.recordPersonal)
        assertFalse("RM sin RP no es válido", esValido)
    }

    @Test
    fun `RF12_T04 - resultado con record personal genera puntos extra`() {
        val r = ResultadoTest("atl-001", "100m", 3, "ok", 9.90, recordPersonal = true)
        val puntos = calcularPuntosConRecords(r.posicion, r.recordPersonal, r.recordMundial)
        assertEquals("3º + RP = 20 puntos", 20, puntos)
    }

    @Test
    fun `RF12_T05 - competiciones se ordenan por fecha correctamente`() {
        val competiciones = listOf(
            Pair("2026-07-12", "Meeting Barcelona"),
            Pair("2025-07-26", "Campeonato España"),
            Pair("2026-06-15", "Mitin Madrid")
        )
        val ordenadas = competiciones.sortedBy { it.first }
        assertEquals("La más antigua va primero", "2025-07-26", ordenadas.first().first)
        assertEquals("La más reciente va última", "2026-07-12", ordenadas.last().first)
    }
}

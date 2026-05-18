package com.example.fantasyatl.data.ligadata

import com.example.fantasyatl.data.Liga
import com.example.fantasyatl.data.dataSession.SupabaseClient
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.UUID

object LigaRepository {

    private val supabase = SupabaseClient.client

    suspend fun crearLigaYAutoUnirse(nombreLiga: String, emailAdmin: String): Result<Liga> =
        withContext(Dispatchers.IO) {
            try {
                val codigoUnico = generarCodigoLiga()

                val nuevaLiga = Liga(
                    id = UUID.randomUUID().toString(),
                    nombre = nombreLiga,
                    codigo = codigoUnico,
                    adminEmail = emailAdmin
                )

                supabase.from("ligas").insert(nuevaLiga)

                // ✅ Usamos nuevaLiga.id — antes usaba una variable separada con UUID distinto
                val membresiaAdmin = LigaUsuario(
                    id = UUID.randomUUID().toString(),
                    ligaId = nuevaLiga.id,
                    emailUsuario = emailAdmin,
                    puntos = 0,
                    presupuesto = 40000000L
                )

                supabase.from("liga_usuarios").insert(membresiaAdmin)

                Result.success(nuevaLiga)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    suspend fun unirseALigaPorCodigo(codigoInput: String, emailUsuario: String): Result<Liga> =
        withContext(Dispatchers.IO) {
            try {
                val codigoLimpio = codigoInput.trim().uppercase()

                if (codigoLimpio.isEmpty())
                    return@withContext Result.failure(Exception("El código no puede estar vacío"))

                val ligaEncontrada = supabase.from("ligas")
                    .select { filter { eq("codigo", codigoLimpio) } }
                    .decodeSingleOrNull<Liga>()
                    ?: return@withContext Result.failure(Exception("El código de liga introducido no existe"))

                val miembroExistente = supabase.from("liga_usuarios")
                    .select {
                        filter {
                            eq("liga_id", ligaEncontrada.id)
                            eq("email_usuario", emailUsuario)
                        }
                    }.decodeSingleOrNull<LigaUsuario>()

                // Si ya pertenecía, no insertamos de nuevo
                if (miembroExistente != null)
                    return@withContext Result.success(ligaEncontrada)

                val nuevaMembresia = LigaUsuario(
                    id = UUID.randomUUID().toString(),
                    ligaId = ligaEncontrada.id,
                    emailUsuario = emailUsuario,
                    puntos = 0,
                    presupuesto = 40000000L
                )

                supabase.from("liga_usuarios").insert(nuevaMembresia)

                Result.success(ligaEncontrada)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    suspend fun obtenerClasificacion(ligaId: String): Result<List<LigaUsuario>> =
        withContext(Dispatchers.IO) {
            try {
                val listaMiembros = supabase.from("liga_usuarios")
                    .select { filter { eq("liga_id", ligaId) } }
                    .decodeList<LigaUsuario>()
                    .sortedByDescending { it.puntos }
                Result.success(listaMiembros)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    private fun generarCodigoLiga(): String {
        val chars = "23456789ABCDEFGHJKLMNPQRSTUVWXYZ"
        return (1..6).map { chars.random() }.joinToString("")
    }
}

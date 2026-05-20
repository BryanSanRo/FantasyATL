package com.example.fantasyatl.data.EmailDB

import com.example.fantasyatl.BuildConfig
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class ResendRequest(
    val from: String,
    val to: List<String>,
    val subject: String,
    val html: String
)

object EmailService {

    // ✅ Configuración correcta del cliente Ktor
    private val client = HttpClient(Android) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                encodeDefaults = true
            })
        }
    }

    suspend fun enviarCodigoRecuperacion(
        emailDestino: String,
        codigo: String,
        nombreUsuario: String
    ): Boolean {
        // ✅ Logs de debug para verificar configuración
        println("🔑 RESEND KEY primeros 8: ${BuildConfig.RESEND_API_KEY.take(8)}...")
        println("📧 Enviando a: $emailDestino")
        println("🔢 Código: $codigo")

        return try {
            val response: HttpResponse = client.post("https://api.resend.com/emails") {
                header(HttpHeaders.Authorization, "Bearer ${BuildConfig.RESEND_API_KEY}")
                contentType(ContentType.Application.Json)
                setBody(
                    ResendRequest(
                        from = "Olympic Fantasy <onboarding@resend.dev>",
                        to = listOf(emailDestino),
                        subject = "Código de recuperación - Olympic Fantasy",
                        html = buildEmailHtml(nombreUsuario, codigo)
                    )
                )
            }

            // ✅ Log del resultado de Resend
            val statusCode = response.status.value
            val body = response.bodyAsText()
            println("📨 Status Resend: $statusCode")
            println("📨 Body Resend: $body")

            // ✅ Resend devuelve 200 o 201 en éxito
            response.status.isSuccess()

        } catch (e: Exception) {
            println("Tipo error: ${e.javaClass.name}")
            println(" Mensaje: ${e.message}")
            false
        }
    }

    private fun buildEmailHtml(nombre: String, codigo: String): String {
        return """
            <!DOCTYPE html>
            <html>
            <body style="font-family: Arial, sans-serif; background-color: #f4f4f4; padding: 20px;">
                <div style="max-width: 500px; margin: auto; background: white;
                            border-radius: 12px; padding: 32px; text-align: center;">

                    <h1 style="color: #1A237E;">🏅 Olympic Fantasy</h1>

                    <p style="color: #333; font-size: 16px;">
                        Hola <strong>$nombre</strong>,
                    </p>
                    <p style="color: #555; font-size: 15px;">
                        Tu código de recuperación de contraseña es:
                    </p>

                    <div style="background: #1A237E; border-radius: 12px;
                                padding: 24px; margin: 24px 0;">
                        <span style="color: white; font-size: 42px;
                                     font-weight: bold; letter-spacing: 12px;">
                            $codigo
                        </span>
                    </div>

                    <p style="color: #888; font-size: 13px;">
                        ⏱️ Este código expira en <strong>15 minutos</strong>.
                    </p>
                    <p style="color: #888; font-size: 13px;">
                        Si no solicitaste este cambio, ignora este email.
                    </p>

                    <hr style="border: none; border-top: 1px solid #eee; margin: 24px 0;">
                    <p style="color: #aaa; font-size: 11px;">
                        Olympic Fantasy · IES El Cañaveral · 2º DAM · 2026
                    </p>
                </div>
            </body>
            </html>
        """.trimIndent()
    }
}

package com.example.fantasyatl.data

import com.example.fantasyatl.BuildConfig
import io.ktor.client.*
import io.ktor.client.engine.android.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
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
            response.status.isSuccess()
        } catch (e: Exception) {
            println("Error enviando email: ${e.message}")
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
                    <h1 style="color: #1A237E;">Olympic Fantasy</h1>
                    <p style="color: #333; font-size: 16px;">
                        Hola <strong>$nombre</strong>,
                    </p>
                    <p style="color: #555; font-size: 15px;">
                        Tu código de recuperación es:
                    </p>
                    <div style="background: #1A237E; border-radius: 12px; 
                                padding: 24px; margin: 24px 0;">
                        <span style="color: white; font-size: 42px; 
                                     font-weight: bold; letter-spacing: 12px;">
                            $codigo
                        </span>
                    </div>
                    <p style="color: #888; font-size: 13px;">
                        Expira en 15 minutos. Si no lo solicitaste, ignora este email.
                    </p>
                    <hr style="border: none; border-top: 1px solid #eee; margin: 24px 0;">
                    <p style="color: #aaa; font-size: 11px;">
                        Olympic Fantasy - IES El Canaveral - 2 DAM
                    </p>
                </div>
            </body>
            </html>
        """.trimIndent()

    }

}
package com.example.fantasyatl.data.dataPlantilla

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PlantillaEntry(
    val id: String? = null,

    @SerialName("liga_id")
    val ligaId: String, // 🟢 Mantiene camelCase para tu código

    @SerialName("email_usuario")
    val emailUsuario: String, // 🟢 Mantiene camelCase para tu código

    @SerialName("atleta_id")
    val atletaId: String?, // 🟢 Mantiene camelCase para tu código

    @SerialName("es_titular")
    val esTitular: Boolean // 🟢 Mantiene camelCase para tu código
)
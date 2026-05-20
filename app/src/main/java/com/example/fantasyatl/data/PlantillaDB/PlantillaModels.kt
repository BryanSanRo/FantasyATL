package com.example.fantasyatl.data.PlantillaDB

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PlantillaEntry(
    @SerialName("id")
    val id: String? = null,

    @SerialName("liga_id")
    val ligaId: String,

    @SerialName("email_usuario")
    val emailUsuario: String,

    @SerialName("atleta_id")
    val atletaId: String?,

    @SerialName("es_titular")
    val esTitular: Boolean
)
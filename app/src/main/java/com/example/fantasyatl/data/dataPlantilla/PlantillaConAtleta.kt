package com.example.fantasyatl.data.dataPlantilla

import com.example.fantasyatl.data.dataAtleta.Atleta
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PlantillaConAtleta(
    val id: String? = null,

    @SerialName("liga_id")
    val ligaId: String,

    @SerialName("email_usuario")
    val emailUsuario: String,

    @SerialName("atleta_id")
    val atletaId: String?,

    @SerialName("es_titular")
    val esTitular: Boolean,

    val atleta: Atleta? = null
)
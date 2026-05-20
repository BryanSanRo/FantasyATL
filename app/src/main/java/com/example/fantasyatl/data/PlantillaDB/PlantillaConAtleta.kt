package com.example.fantasyatl.data.PlantillaDB

import com.example.fantasyatl.data.AtletaDB.Atleta
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable  // ✅ añade este import

@Serializable
data class PlantillaConAtleta(
    @SerialName("id")
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

package fr.mathano.livingdex.data.model

import com.google.gson.annotations.SerializedName

data class RegionResponse(
    val id: Int,
    val name: String,
    val locations: List<NamedApiResource>,
    @SerializedName("main_generation")
    val mainGeneration: NamedApiResource,
    val names: List<LocalizedName>,
    val pokedexes: List<NamedApiResource>,
    @SerializedName("version_groups")
    val versionGroups: List<NamedApiResource>,
)

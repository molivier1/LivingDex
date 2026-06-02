package fr.mathano.livingdex.data.local

import androidx.room.Entity
import fr.mathano.livingdex.data.model.DataPokemonDetail

@Entity(
    tableName = "pokemon_details",
    primaryKeys = ["language", "idPokemon"]
)
data class PokemonDetailEntity(
    val language: String,
    val idPokemon: Int,
    val nom: String,
    val urlSprite: String,
    val taille: Int,
    val poids: Int,
    val types: String,
    val description: String?,
    val talents: String,
    val evolutions: String,
)

fun PokemonDetailEntity.toDataPokemonDetail(): DataPokemonDetail =
    DataPokemonDetail(
        idPokemon = idPokemon,
        nom = nom,
        urlSprite = urlSprite,
        taille = taille,
        poids = poids,
        types = types.toListValue(),
        description = description,
        talents = talents.toListValue(),
        evolutions = evolutions.toListValue()
    )

fun DataPokemonDetail.toPokemonDetailEntity(language: String): PokemonDetailEntity =
    PokemonDetailEntity(
        language = language,
        idPokemon = idPokemon,
        nom = nom,
        urlSprite = urlSprite,
        taille = taille,
        poids = poids,
        types = types.toStoredValue(),
        description = description,
        talents = talents.toStoredValue(),
        evolutions = evolutions.toStoredValue()
    )

private const val LIST_SEPARATOR = "|"

private fun String.toListValue(): List<String> =
    if (isBlank()) emptyList() else split(LIST_SEPARATOR)

private fun List<String>.toStoredValue(): String =
    joinToString(LIST_SEPARATOR)

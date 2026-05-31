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
)

fun PokemonDetailEntity.toDataPokemonDetail(): DataPokemonDetail =
    DataPokemonDetail(
        idPokemon = idPokemon,
        nom = nom,
        urlSprite = urlSprite,
        taille = taille,
        poids = poids
    )

fun DataPokemonDetail.toPokemonDetailEntity(language: String): PokemonDetailEntity =
    PokemonDetailEntity(
        language = language,
        idPokemon = idPokemon,
        nom = nom,
        urlSprite = urlSprite,
        taille = taille,
        poids = poids
    )

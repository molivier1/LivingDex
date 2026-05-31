package fr.mathano.livingdex.data.local

import androidx.room.Entity
import fr.mathano.livingdex.data.model.DataPokemon

@Entity(
    tableName = "pokedex_pokemons",
    primaryKeys = ["idPokedex", "language", "entryDex"]
)
data class PokedexPokemonEntity(
    val idPokedex: Int,
    val language: String,
    val idPokemon: Int,
    val nom: String,
    val urlSprite: String,
    val entryDex: Int,
)

fun PokedexPokemonEntity.toDataPokemon(): DataPokemon =
    DataPokemon(
        idPokemon = idPokemon,
        nom = nom,
        urlSprite = urlSprite,
        entryDex = entryDex
    )

fun DataPokemon.toPokedexPokemonEntity(idPokedex: Int, language: String): PokedexPokemonEntity =
    PokedexPokemonEntity(
        idPokedex = idPokedex,
        language = language,
        idPokemon = idPokemon,
        nom = nom,
        urlSprite = urlSprite,
        entryDex = entryDex
    )

package fr.mathano.livingdex.data.local

import androidx.room.Entity

@Entity(
    tableName = "pokemon_progress",
    primaryKeys = ["idPokedex", "entryDex"]
)
data class PokemonProgressEntity(
    val idPokedex: Int,
    val entryDex: Int,
    val idPokemon: Int,
)

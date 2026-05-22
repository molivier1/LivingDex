package fr.mathano.livingdex.data

import androidx.compose.ui.text.intl.Locale
import co.pokeapi.pokekotlin.PokeApi
import co.pokeapi.pokekotlin.PokeApi.Default.get
import fr.mathano.livingdex.data.model.DataPokemon
import fr.mathano.livingdex.toDisplayName
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext

object Pokedex {
    suspend fun recupererPokedexParRegion(idPokedex: Int): List<DataPokemon> = withContext(Dispatchers.IO) {
        val locale = Locale.current.language

        PokeApi.getPokedex(idPokedex).pokemonEntries.map { pokemonEntry ->
            async {
                val pokemonSpecies = pokemonEntry.pokemonSpecies.get()

                val pokemon = pokemonEntry.pokemonSpecies.get().varieties.first().variety.get()

                val idPokemon = pokemon.id

                val nom = pokemonSpecies.names.firstOrNull {
                    it.language.name == locale
                }?.name ?: pokemonSpecies.name.toDisplayName()

                val sprite = pokemon.sprites.frontDefault.toString()

                val entryDex = pokemonEntry.entryNumber

                DataPokemon(idPokemon, nom, sprite, entryDex)
            }
        }.awaitAll()
    }
}

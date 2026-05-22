package fr.mathano.livingdex.data

import co.pokeapi.pokekotlin.PokeApi
import co.pokeapi.pokekotlin.PokeApi.Default.get
import fr.mathano.livingdex.data.model.DataPokemon
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext

object Pokedex {
    suspend fun recupererPokedexParRegion(idPokedex: Int): List<DataPokemon> = withContext(Dispatchers.IO) {
        PokeApi.getPokedex(idPokedex).pokemonEntries.map { pokemonEntry ->
            async {
                val pokemon = pokemonEntry.pokemonSpecies.get().varieties.first().variety.get()

                val idPokemon = pokemon.id

                val nom = pokemonEntry.pokemonSpecies.name

                val sprite = pokemon.sprites.frontDefault.toString()

                DataPokemon(idPokemon, nom, sprite)
            }
        }.awaitAll()
    }
}

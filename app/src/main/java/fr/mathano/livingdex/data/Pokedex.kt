package fr.mathano.livingdex.data

import co.pokeapi.pokekotlin.PokeApi
import co.pokeapi.pokekotlin.PokeApi.Default.get
import fr.mathano.livingdex.data.model.DataPokemon

object Pokedex {
    suspend fun recupererPokedexParRegion(idPokedex: Int): List<DataPokemon> {
        val listeDataPokemon = mutableListOf<DataPokemon>()

        PokeApi.getPokedex(idPokedex).pokemonEntries.forEach { pokemonEntry ->

            val pokemon = pokemonEntry.pokemonSpecies.get().varieties.first().variety.get()

            val idPokemon = pokemon.id

            val nom = pokemonEntry.pokemonSpecies.name

            val sprite = pokemon.sprites.frontDefault.toString()

            listeDataPokemon.add(DataPokemon(idPokemon, nom, sprite))
        }

        return listeDataPokemon
    }
}
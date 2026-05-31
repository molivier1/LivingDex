package fr.mathano.livingdex.data

import androidx.compose.ui.text.intl.Locale
import co.pokeapi.pokekotlin.PokeApi
import co.pokeapi.pokekotlin.PokeApi.Default.get
import fr.mathano.livingdex.data.local.DatabaseProvider
import fr.mathano.livingdex.data.local.PokemonProgressEntity
import fr.mathano.livingdex.data.local.toDataPokemon
import fr.mathano.livingdex.data.local.toPokedexPokemonEntity
import fr.mathano.livingdex.data.model.DataPokemon
import fr.mathano.livingdex.toDisplayName
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext

object Pokedex {
    suspend fun recupererPokedexParRegion(idPokedex: Int): List<DataPokemon> = withContext(Dispatchers.IO) {
        val locale = Locale.current.language
        val pokeDao = DatabaseProvider.pokeDao

        val pokemonsEnBase = pokeDao.getPokedexPokemons(idPokedex, locale)
        if (pokemonsEnBase.isNotEmpty()) {
            return@withContext pokemonsEnBase.map { it.toDataPokemon() }
        }

        val pokemons = PokeApi.getPokedex(idPokedex).pokemonEntries.map { pokemonEntry ->
            async {
                val pokemonSpecies = pokemonEntry.pokemonSpecies.get()

                val pokemon = pokemonSpecies.varieties.first().variety.get()

                val idPokemon = pokemon.id

                val nom = pokemonSpecies.names.firstOrNull {
                    it.language.name == locale
                }?.name ?: pokemonSpecies.name.toDisplayName()

                val sprite = pokemon.sprites.frontDefault.toString()

                val entryDex = pokemonEntry.entryNumber

                DataPokemon(idPokemon, nom, sprite, entryDex)
            }
        }.awaitAll()

        pokeDao.insertPokedexPokemons(
            pokemons.map { it.toPokedexPokemonEntity(idPokedex, locale) }
        )

        return@withContext pokemons
    }

    suspend fun recupererEntriesCapturees(idPokedex: Int): Set<Int> = withContext(Dispatchers.IO) {
        DatabaseProvider.pokeDao
            .getPokemonProgress(idPokedex)
            .map { it.entryDex }
            .toSet()
    }

    suspend fun changerCapturePokemon(idPokedex: Int, pokemon: DataPokemon): Boolean = withContext(Dispatchers.IO) {
        val pokeDao = DatabaseProvider.pokeDao
        val progressionExistante = pokeDao.getPokemonProgressEntry(
            idPokedex = idPokedex,
            entryDex = pokemon.entryDex
        )

        if (progressionExistante != null) {
            pokeDao.deletePokemonProgress(progressionExistante)
            return@withContext false
        }

        pokeDao.insertPokemonProgress(
            PokemonProgressEntity(
                idPokedex = idPokedex,
                entryDex = pokemon.entryDex,
                idPokemon = pokemon.idPokemon
            )
        )
        return@withContext true
    }
}

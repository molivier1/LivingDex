package fr.mathano.livingdex.data

import androidx.compose.ui.text.intl.Locale
import co.pokeapi.pokekotlin.PokeApi
import co.pokeapi.pokekotlin.PokeApi.Default.get
import co.pokeapi.pokekotlin.model.PokemonEntry
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
    private const val NATIONAL_POKEDEX_ID = 1
    private const val POKEMON_FETCH_BATCH_SIZE = 100

    suspend fun recupererPokedexParRegion(idPokedex: Int): List<DataPokemon> = withContext(Dispatchers.IO) {
        val locale = Locale.current.language
        val pokeDao = DatabaseProvider.pokeDao

        val pokemonsEnBase = pokeDao.getPokedexPokemons(idPokedex, locale)
        if (pokemonsEnBase.isNotEmpty()) {
            return@withContext pokemonsEnBase.map { it.toDataPokemon() }
        }

        val pokemonEntries = PokeApi.getPokedex(idPokedex).pokemonEntries
        val pokemons = mutableListOf<DataPokemon>()

        pokemonEntries.chunked(POKEMON_FETCH_BATCH_SIZE).forEach { pokemonEntryBatch ->
            val pokemonsBatch = pokemonEntryBatch.map { pokemonEntry ->
                async { pokemonEntry.toDataPokemon(locale) }
            }.awaitAll()

            pokemons.addAll(pokemonsBatch)
        }

        pokeDao.insertPokedexPokemons(
            pokemons.map { it.toPokedexPokemonEntity(idPokedex, locale) }
        )

        return@withContext pokemons
    }

    suspend fun recupererPokedexNational(): List<DataPokemon> =
        recupererPokedexParRegion(NATIONAL_POKEDEX_ID)

    suspend fun recupererPokedexNationalProgressif(
        onPokemonsLoaded: (List<DataPokemon>) -> Unit,
    ) = withContext(Dispatchers.IO) {
        val locale = Locale.current.language
        val pokeDao = DatabaseProvider.pokeDao
        val pokemonsParEntryDex = linkedMapOf<Int, DataPokemon>()

        val pokemonsEnBase = pokeDao.getPokedexPokemons(NATIONAL_POKEDEX_ID, locale)
            .map { it.toDataPokemon() }

        pokemonsEnBase.forEach { pokemon ->
            pokemonsParEntryDex[pokemon.entryDex] = pokemon
        }

        if (pokemonsEnBase.isNotEmpty()) {
            withContext(Dispatchers.Main) {
                onPokemonsLoaded(pokemonsParEntryDex.values.sortedBy { it.entryDex })
            }
        }

        val pokemonEntries = PokeApi.getPokedex(NATIONAL_POKEDEX_ID).pokemonEntries
        val missingPokemonEntries = pokemonEntries.filter { pokemonEntry ->
            pokemonEntry.entryNumber !in pokemonsParEntryDex
        }

        missingPokemonEntries.chunked(POKEMON_FETCH_BATCH_SIZE).forEach { pokemonEntryBatch ->
            val pokemonsBatch = pokemonEntryBatch.map { pokemonEntry ->
                async { pokemonEntry.toDataPokemon(locale) }
            }.awaitAll()

            pokeDao.insertPokedexPokemons(
                pokemonsBatch.map { it.toPokedexPokemonEntity(NATIONAL_POKEDEX_ID, locale) }
            )

            pokemonsBatch.forEach { pokemon ->
                pokemonsParEntryDex[pokemon.entryDex] = pokemon
            }

            withContext(Dispatchers.Main) {
                onPokemonsLoaded(pokemonsParEntryDex.values.sortedBy { it.entryDex })
            }
        }
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

    private suspend fun PokemonEntry.toDataPokemon(locale: String): DataPokemon {
        val species = pokemonSpecies.get()

        val pokemon = species.varieties.first().variety.get()

        val idPokemon = pokemon.id

        val nom = species.names.firstOrNull {
            it.language.name == locale
        }?.name ?: species.name.toDisplayName()

        val sprite = pokemon.sprites.frontDefault.toString()

        val entryDex = entryNumber

        return DataPokemon(idPokemon, nom, sprite, entryDex)
    }
}

package fr.mathano.livingdex.data

import androidx.compose.ui.text.intl.Locale
import co.pokeapi.pokekotlin.PokeApi
import co.pokeapi.pokekotlin.PokeApi.Default.get
import fr.mathano.livingdex.data.local.DatabaseProvider
import fr.mathano.livingdex.data.local.toDataPokemonDetail
import fr.mathano.livingdex.data.local.toPokemonDetailEntity
import fr.mathano.livingdex.data.model.DataPokemonDetail
import fr.mathano.livingdex.toDisplayName
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object PokemonDetails {
    suspend fun recupererPokemonDetail(idPokemon: Int): DataPokemonDetail = withContext(Dispatchers.IO) {
        val locale = Locale.current.language
        val pokeDao = DatabaseProvider.pokeDao

        val pokemonEnBase = pokeDao.getPokemonDetail(idPokemon, locale)
        if (pokemonEnBase != null) {
            return@withContext pokemonEnBase.toDataPokemonDetail()
        }

        val pokemon = PokeApi.getPokemonVariety(idPokemon)
        val pokemonSpecies = pokemon.species.get()

        val nom = pokemonSpecies.names.firstOrNull {
            it.language.name == locale
        }?.name ?: pokemon.name.toDisplayName()

        val detail = DataPokemonDetail(
            idPokemon = pokemon.id,
            nom = nom,
            urlSprite = pokemon.sprites.frontDefault.orEmpty(),
            taille = pokemon.height,
            poids = pokemon.weight
        )

        pokeDao.insertPokemonDetail(detail.toPokemonDetailEntity(locale))

        return@withContext detail
    }
}

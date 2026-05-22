package fr.mathano.livingdex.data

import co.pokeapi.pokekotlin.PokeApi
import fr.mathano.livingdex.data.model.DataRegion
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext
import fr.mathano.livingdex.MainActivity.Companion as outils

object Regions {
    suspend fun recupererRegions(locale: String): List<DataRegion> = withContext(Dispatchers.IO) {
        PokeApi.getRegionList(0, 100).results.map { regionRaw ->
            async {
                val region = PokeApi.getRegion(regionRaw.id)

                val idRegion = region.id

                val nomRegion = region.names.firstOrNull {
                    it.language.name == locale
                }?.name ?: region.name.toDisplayName()

                val idPokedex = region.pokedexes.firstOrNull()?.id ?: -1

                DataRegion(idRegion, nomRegion, idPokedex)
            }
        }.awaitAll()
    }
}

private fun String.toDisplayName(): String =
    split("-").joinToString(" ") { word ->
        word.replaceFirstChar { firstChar ->
            if (firstChar.isLowerCase()) firstChar.titlecase() else firstChar.toString()
        }
    }

package fr.mathano.livingdex.data

import co.pokeapi.pokekotlin.PokeApi
import fr.mathano.livingdex.MainActivity.Companion as outils

object Regions {
    suspend fun recupererRegions(locale: String): HashMap<String, Int> {
        val regions = HashMap<String, Int>()

        for (regionRaw in PokeApi.getRegionList(0, 100).results) {
            val region = PokeApi.getRegion(regionRaw.id)

            regions[
                region.names.firstOrNull {
                    it.language.name == locale
                }?.name ?: region.name.toDisplayName()
            ] = region.pokedexes.firstOrNull()?.id ?: -1
        }

        return regions
    }
}

private fun String.toDisplayName(): String =
    split("-").joinToString(" ") { word ->
        word.replaceFirstChar { firstChar ->
            if (firstChar.isLowerCase()) firstChar.titlecase() else firstChar.toString()
        }
    }

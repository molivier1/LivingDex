package fr.mathano.livingdex.data

import co.pokeapi.pokekotlin.PokeApi
import fr.mathano.livingdex.data.model.DataRegion
import fr.mathano.livingdex.MainActivity.Companion as outils

object Regions {
    suspend fun recupererRegions(locale: String): List<DataRegion> {
        val regions = mutableListOf<DataRegion>()

        for (regionRaw in PokeApi.getRegionList(0, 100).results) {
            val region = PokeApi.getRegion(regionRaw.id)

            /*regions[
                region.names.firstOrNull {
                    it.language.name == locale
                }?.name ?: region.name.toDisplayName()
            ] = region.pokedexes.firstOrNull()?.id ?: -1*/

            val idRegion = region.id

            val nomRegion = region.names.firstOrNull {
                it.language.name == locale
            }?.name ?: region.name.toDisplayName()

            val idPokedex = region.pokedexes.firstOrNull()?.id ?: -1

            regions.add(DataRegion(idRegion, nomRegion, idPokedex))

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

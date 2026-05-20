package fr.mathano.livingdex.data

import co.pokeapi.pokekotlin.PokeApi

object Regions {
    suspend fun recupererRegions(locale: String): List<String> {
        val regions = mutableListOf<String>()

        for (regionRaw in PokeApi.getRegionList(0, 100).results) {
            val region = PokeApi.getRegion(regionRaw.id)

            regions.add(
                region.names.firstOrNull { langue ->
                    langue.language.name == locale
                }?.name?: region.name.toDisplayName())
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

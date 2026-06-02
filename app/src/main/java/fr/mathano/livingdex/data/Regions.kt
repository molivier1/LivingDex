package fr.mathano.livingdex.data

import co.pokeapi.pokekotlin.PokeApi
import fr.mathano.livingdex.R
import fr.mathano.livingdex.data.local.DatabaseProvider
import fr.mathano.livingdex.data.local.toDataRegion
import fr.mathano.livingdex.data.local.toRegionEntity
import fr.mathano.livingdex.data.model.DataRegion
import fr.mathano.livingdex.toDisplayName
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext

object Regions {
    suspend fun recupererRegions(): List<DataRegion> = withContext(Dispatchers.IO) {
        val locale = AppLanguage.current()
        val apiLocale = AppLanguage.pokeApiLanguage()
        val pokeDao = DatabaseProvider.pokeDao

        val regionsEnBase = pokeDao.getRegions(locale)
        if (regionsEnBase.isNotEmpty()) {
            return@withContext regionsEnBase.map { it.toDataRegion() }
        }

        val regions = PokeApi.getRegionList(0, 100).results.map { regionRaw ->
            async {
                val region = PokeApi.getRegion(regionRaw.id)

                val idRegion = region.id

                val nomRegion = region.names.firstOrNull {
                    it.language.name.equals(apiLocale, ignoreCase = true)
                }?.name ?: region.name.toLocalRegionName()

                val idPokedex = region.pokedexes.firstOrNull()?.id ?: -1

                DataRegion(idRegion, nomRegion, idPokedex)
            }
        }.awaitAll()

        pokeDao.insertRegions(regions.map { it.toRegionEntity(locale) })
        regions
    }

    private fun String.toLocalRegionName(): String {
        val resId = when (this) {
            "kanto" -> R.string.region_kanto
            "johto" -> R.string.region_johto
            "hoenn" -> R.string.region_hoenn
            "sinnoh" -> R.string.region_sinnoh
            "unova" -> R.string.region_unova
            "kalos" -> R.string.region_kalos
            "alola" -> R.string.region_alola
            "galar" -> R.string.region_galar
            "hisui" -> R.string.region_hisui
            "paldea" -> R.string.region_paldea
            else -> null
        }

        return resId?.let { AppLanguage.string(it) } ?: toDisplayName()
    }
}

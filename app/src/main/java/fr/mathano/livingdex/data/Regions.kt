package fr.mathano.livingdex.data

import androidx.compose.ui.text.intl.Locale
import co.pokeapi.pokekotlin.PokeApi
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
        val locale = Locale.current.language
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
                    it.language.name == locale
                }?.name ?: region.name.toDisplayName()

                val idPokedex = region.pokedexes.firstOrNull()?.id ?: -1

                DataRegion(idRegion, nomRegion, idPokedex)
            }
        }.awaitAll()

        pokeDao.insertRegions(regions.map { it.toRegionEntity(locale) })
        regions
    }
}

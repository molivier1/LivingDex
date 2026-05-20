package fr.mathano.livingdex.data

import fr.mathano.livingdex.data.model.RegionResponse
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

private const val BASE_URL = "https://pokeapi.co/api/v2/"

object PokeApiClient {
    private val api: PokeApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(PokeApiService::class.java)
    }

    suspend fun recupererRegions(language: String): List<String> {
        val regions = api.recupererRegions(limit = 100).results

        val nomsTraduits = mutableListOf<String>()

        for (region in regions) {
            val regionResponse = recupererRegion(region.name)

            val nomTraduit = regionResponse.names
                .firstOrNull { it.language.name == language }
                ?.name
                ?: region.name.toDisplayName()

            nomsTraduits.add(nomTraduit)
        }

        return nomsTraduits
    }

    suspend fun recupererRegion(region: String): RegionResponse =
        api.recupererRegion(region)
}

private fun String.toDisplayName(): String =
    split("-").joinToString(" ") { word ->
        word.replaceFirstChar { firstChar ->
            if (firstChar.isLowerCase()) firstChar.titlecase() else firstChar.toString()
        }
    }

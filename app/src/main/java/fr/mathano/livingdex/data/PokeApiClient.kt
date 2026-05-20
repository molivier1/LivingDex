package fr.mathano.livingdex.data

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

private const val BASE_URL = "https://pokeapi.co/api/v2/"

object PokeApiClient {
    private val api: PokeApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(PokeApiService::class.java)
    }

    suspend fun recupererRegions(): List<String> =
        api.recupererRegions(limit = 100)
            .results
            .map { resource -> resource.name.toDisplayName() }
}

interface PokeApiService {
    @GET("region")
    suspend fun recupererRegions(
        @Query("limit") limit: Int,
    ): NamedApiResourceList
}

data class NamedApiResourceList(
    val count: Int,
    val next: String?,
    val previous: String?,
    val results: List<NamedApiResource>,
)

data class NamedApiResource(
    val name: String,
    val url: String,
)

private fun String.toDisplayName(): String =
    split("-").joinToString(" ") { word ->
        word.replaceFirstChar { firstChar ->
            if (firstChar.isLowerCase()) firstChar.titlecase() else firstChar.toString()
        }
    }

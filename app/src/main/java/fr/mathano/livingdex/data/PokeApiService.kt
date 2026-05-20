package fr.mathano.livingdex.data

import fr.mathano.livingdex.data.model.NamedApiResourceList
import fr.mathano.livingdex.data.model.RegionResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface PokeApiService {
    @GET("region")
    suspend fun recupererRegions(
        @Query("limit") limit: Int,
    ): NamedApiResourceList

    @GET("region/{region}")
    suspend fun recupererRegion(
        @Path("region") region: String,
    ): RegionResponse
}

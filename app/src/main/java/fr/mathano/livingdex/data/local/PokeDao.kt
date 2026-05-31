package fr.mathano.livingdex.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface PokeDao {
    @Query("SELECT * FROM regions WHERE language = :language ORDER BY idRegion")
    suspend fun getRegions(language: String): List<RegionEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRegions(regions: List<RegionEntity>)

    @Query(
        """
        SELECT * FROM pokedex_pokemons
        WHERE idPokedex = :idPokedex AND language = :language
        ORDER BY entryDex
        """
    )
    suspend fun getPokedexPokemons(idPokedex: Int, language: String): List<PokedexPokemonEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPokedexPokemons(pokemons: List<PokedexPokemonEntity>)

    @Query(
        """
        SELECT * FROM pokemon_details
        WHERE idPokemon = :idPokemon AND language = :language
        """
    )
    suspend fun getPokemonDetail(idPokemon: Int, language: String): PokemonDetailEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPokemonDetail(pokemonDetail: PokemonDetailEntity)
}

package fr.mathano.livingdex.data.local

import androidx.room.Dao
import androidx.room.Delete
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

    @Query("SELECT * FROM pokemon_progress WHERE idPokedex = :idPokedex")
    suspend fun getPokemonProgress(idPokedex: Int): List<PokemonProgressEntity>

    @Query(
        """
        SELECT * FROM pokemon_progress
        WHERE idPokedex = :idPokedex AND entryDex = :entryDex
        """
    )
    suspend fun getPokemonProgressEntry(idPokedex: Int, entryDex: Int): PokemonProgressEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPokemonProgress(progress: PokemonProgressEntity)

    @Delete
    suspend fun deletePokemonProgress(progress: PokemonProgressEntity)
}

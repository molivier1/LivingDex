package fr.mathano.livingdex.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        RegionEntity::class,
        PokedexPokemonEntity::class,
        PokemonDetailEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class LivingDexDatabase : RoomDatabase() {
    abstract fun pokeDao(): PokeDao

    companion object {
        @Volatile
        private var instance: LivingDexDatabase? = null

        fun getInstance(context: Context): LivingDexDatabase {
            return instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    LivingDexDatabase::class.java,
                    "livingdex.db"
                ).build().also { instance = it }
            }
        }
    }
}

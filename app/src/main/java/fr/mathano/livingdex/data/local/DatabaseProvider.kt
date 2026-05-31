package fr.mathano.livingdex.data.local

import android.content.Context

object DatabaseProvider {
    private var database: LivingDexDatabase? = null

    val pokeDao: PokeDao
        get() = checkNotNull(database) {
            "DatabaseProvider.init(context) doit etre appele avant d'utiliser la base."
        }.pokeDao()

    fun init(context: Context) {
        if (database != null) return
        database = LivingDexDatabase.getInstance(context)
    }
}

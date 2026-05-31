package fr.mathano.livingdex.data.local

import androidx.room.Entity
import fr.mathano.livingdex.data.model.DataRegion

@Entity(
    tableName = "regions",
    primaryKeys = ["language", "idRegion"]
)
data class RegionEntity(
    val language: String,
    val idRegion: Int,
    val nomRegion: String,
    val idPokedex: Int,
)

fun RegionEntity.toDataRegion(): DataRegion =
    DataRegion(
        idRegion = idRegion,
        nomRegion = nomRegion,
        idPokedex = idPokedex
    )

fun DataRegion.toRegionEntity(language: String): RegionEntity =
    RegionEntity(
        language = language,
        idRegion = idRegion,
        nomRegion = nomRegion,
        idPokedex = idPokedex
    )

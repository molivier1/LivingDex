package fr.mathano.livingdex.data.model

data class DataPokemonDetail(
    val idPokemon: Int,
    val nom: String,
    val urlSprite: String,
    val taille: Int,
    val poids: Int,
    val types: List<String>,
    val description: String?,
    val talents: List<String>,
    val evolutions: List<String>,
)

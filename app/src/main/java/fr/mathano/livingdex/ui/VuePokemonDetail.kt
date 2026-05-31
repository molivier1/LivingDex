package fr.mathano.livingdex.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import fr.mathano.livingdex.data.PokemonDetails
import fr.mathano.livingdex.data.model.DataPokemonDetail

@Composable
fun EcranPokemonDetail(
    idPokemon: Int,
    entryDex: Int,
    modifier: Modifier = Modifier,
    recupererPokemonDetail: suspend (Int) -> DataPokemonDetail = PokemonDetails::recupererPokemonDetail,
) {
    var state by remember { mutableStateOf<PokemonDetailState>(PokemonDetailState.Loading) }

    LaunchedEffect(idPokemon, recupererPokemonDetail) {
        state = try {
            PokemonDetailState.Success(recupererPokemonDetail(idPokemon))
        } catch (exception: Exception) {
            PokemonDetailState.Error
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        when (val currentState = state) {
            PokemonDetailState.Loading -> {
                Text("Chargement...")
            }

            PokemonDetailState.Error -> {
                Text("Erreur API")
            }

            is PokemonDetailState.Success -> {
                val pokemon = currentState.pokemon
                val isFrench = Locale.current.language == "fr"

                Text(
                    text = pokemon.nom,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )

                AsyncImage(
                    model = pokemon.urlSprite,
                    contentDescription = "Image de ${pokemon.nom}",
                    modifier = Modifier.size(128.dp)
                )

                Text("National : #${pokemon.idPokemon}")
                Text("Regional : #$entryDex")
                Text("Taille : ${pokemon.taille.formattedHeight(isFrench)}")
                Text("Poids : ${pokemon.poids.formattedWeight(isFrench)}")
            }
        }
    }
}

private sealed interface PokemonDetailState {
    data object Loading : PokemonDetailState
    data object Error : PokemonDetailState
    class Success(val pokemon: DataPokemonDetail) : PokemonDetailState
}

private fun Int.formattedHeight(isFrench: Boolean): String {
    return if (isFrench) {
        "${formatOneDecimal(this / 10f)} m"
    } else {
        "$this dm"
    }
}

private fun Int.formattedWeight(isFrench: Boolean): String {
    return if (isFrench) {
        "${formatOneDecimal(this / 10f)} kg"
    } else {
        "$this hg"
    }
}

private fun formatOneDecimal(value: Float): String =
    "%.1f".format(java.util.Locale.FRANCE, value)

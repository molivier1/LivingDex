package fr.mathano.livingdex.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import fr.mathano.livingdex.R
import fr.mathano.livingdex.data.AppLanguage
import fr.mathano.livingdex.data.PokemonDetails
import fr.mathano.livingdex.data.model.DataPokemonDetail
import fr.mathano.livingdex.ui.components.Bulle
import fr.mathano.livingdex.ui.components.livingDexString
import fr.mathano.livingdex.ui.theme.LivingDexBubbleGradient

@Composable
fun EcranPokemonDetail(
    idPokemon: Int,
    entryDex: Int,
    idPokedex: Int,
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
            .verticalScroll(rememberScrollState())
            .padding(vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        when (val currentState = state) {
            PokemonDetailState.Loading -> {
                Text(livingDexString(R.string.loading))
            }

            PokemonDetailState.Error -> {
                Text(livingDexString(R.string.error_api))
            }

            is PokemonDetailState.Success -> {
                val pokemon = currentState.pokemon
                val isFrench = AppLanguage.current() == "fr"

                Bulle {
                    Text(
                        text = livingDexString(R.string.pokedex_sheet_title),
                        modifier = Modifier.fillMaxWidth(),
                        color = Color.Black,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = pokemon.enteteNumero(idPokedex, entryDex),
                        modifier = Modifier.fillMaxWidth(),
                        color = Color.Black,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Center
                    )
                }

                Box(
                    modifier = Modifier
                        .size(150.dp)
                        .background(
                            brush = LivingDexBubbleGradient,
                            shape = RoundedCornerShape(20.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    AsyncImage(
                        model = pokemon.urlSprite,
                        contentDescription = livingDexString(R.string.pokemon_image_description, pokemon.nom),
                        modifier = Modifier.size(128.dp)
                    )
                }

                SectionDetail(livingDexString(R.string.types_title), pokemon.types.joinToString(", "))

                pokemon.description?.let { description ->
                    SectionDetail(livingDexString(R.string.description_title), description)
                }

                SectionDetail(livingDexString(R.string.abilities_title), pokemon.talents.joinToString("\n"))

                SectionDetail(
                    titre = livingDexString(R.string.measurements_title),
                    valeur = livingDexString(
                        R.string.height_label,
                        pokemon.taille.formattedHeight(isFrench)
                    ) + "\n" + livingDexString(
                        R.string.weight_label,
                        pokemon.poids.formattedWeight(isFrench)
                    )
                )

                SectionDetail(livingDexString(R.string.evolution_title), pokemon.evolutions.joinToString("\n"))
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

@Composable
private fun Int.toDexNumber(): String =
    livingDexString(R.string.dex_no_format, this)

@Composable
private fun DataPokemonDetail.enteteNumero(
    idPokedex: Int,
    entryDex: Int,
): String {
    return if (idPokedex == 1) {
        livingDexString(R.string.national_dex_header, idPokemon.toDexNumber(), nom)
    } else {
        livingDexString(
            R.string.regional_dex_header,
            entryDex.toDexNumber(),
            idPokemon.toDexNumber(),
            nom
        )
    }
}

@Composable
private fun SectionDetail(
    titre: String,
    valeur: String,
) {
    if (valeur.isBlank()) {
        return
    }

    Bulle {
        Text(
            text = titre,
            modifier = Modifier.fillMaxWidth(),
            color = Color.Black,
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Text(
            text = valeur,
            modifier = Modifier.fillMaxWidth(),
            color = Color.Black,
            fontSize = 20.sp,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center
        )
    }
}

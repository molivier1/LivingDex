package fr.mathano.livingdex.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.res.integerResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fr.mathano.livingdex.R
import fr.mathano.livingdex.data.Pokedex
import fr.mathano.livingdex.data.model.DataPokemon

@Composable
fun EcranPokedex(
    modifier: Modifier = Modifier,
    nomRegion: String,
    idPokedex: Int,
    onPokemonClick: (String) -> Unit = {_ -> },
    recupererPokedexParRegion: suspend (Int) -> List<DataPokemon> = Pokedex::recupererPokedexParRegion,
) {
    val locale = Locale.current.language
    var state by remember { mutableStateOf<PokedexState>(PokedexState.Loading) }
    val columnCount = integerResource(R.integer.n_colonnes)
    val cornerRadius = integerResource(R.integer.arrondi).dp

    LaunchedEffect (recupererPokedexParRegion, idPokedex) {
        state = try {
            PokedexState.Success(recupererPokedexParRegion(idPokedex))
        } catch (exception: Exception) {
            PokedexState.Error
        }
    }

    Column(modifier = modifier
        .fillMaxSize()
        .background(Color(0xFFF4F4F4))) {
        Text(
            text = "Région : $nomRegion",
            modifier = Modifier.padding(16.dp),
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )

        LazyVerticalGrid(
            columns = GridCells.Fixed(columnCount),
            modifier = modifier
                .fillMaxSize()
                .background(Color(0xFFF4F4F4)),
            contentPadding = PaddingValues(18.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            when (val currentState = state) {
                is PokedexState.Loading -> {
                    items(8) {
                        CarrePokemon(
                            label = "",
                            cornerRadius = cornerRadius,
                            onClick = onPokemonClick
                        )
                    }
                }

                is PokedexState.Error -> {
                    item { Text("Erreur API") }
                }

                is PokedexState.Success -> {
                    items(currentState.pokemons.toList()) { (idPokemon: Int, nom: String, urlSprite: String) ->
                        CarrePokemon(
                            label = nom,
                            cornerRadius = cornerRadius,
                            onClick = onPokemonClick
                        )
                    }
                }
            }
        }
    }
}

private sealed interface PokedexState {
    data object Loading : PokedexState
    data object Error : PokedexState
    class Success(val pokemons: List<DataPokemon>) : PokedexState
}

@Composable
private fun CarrePokemon(
    label: String,
    cornerRadius: androidx.compose.ui.unit.Dp,
    onClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .aspectRatio(1f)
            .background(
                color = Color(0xFFD9D9D9),
                shape = RoundedCornerShape(cornerRadius)
            )
            .clickable {
                onClick(label)
            }
            .padding(12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            color = Color.Black,
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium
        )
    }
}
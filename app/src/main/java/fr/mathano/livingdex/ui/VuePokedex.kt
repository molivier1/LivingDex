package fr.mathano.livingdex.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.OutlinedTextField
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
import coil.compose.AsyncImage
import fr.mathano.livingdex.R
import fr.mathano.livingdex.data.Pokedex
import fr.mathano.livingdex.data.model.DataPokemon

@Composable
fun EcranPokedex(
    modifier: Modifier = Modifier,
    nomRegion: String,
    idPokedex: Int,
    onPokemonClick: (String) -> Unit = {_ -> },
    onPokemonLongClick: (Int, Int) -> Unit = { _, _ -> },
    recupererPokedexParRegion: suspend (Int) -> List<DataPokemon> = Pokedex::recupererPokedexParRegion,
) {
    var state by remember { mutableStateOf<PokedexState>(PokedexState.Loading) }
    var searchQuery by remember { mutableStateOf("") }
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

        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, top = 12.dp, end = 16.dp),
            singleLine = true,
            label = { Text("Rechercher") }
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
                            onClick = onPokemonClick,
                            onLongClick = onPokemonLongClick
                        )
                    }
                }

                is PokedexState.Error -> {
                    item { Text("Erreur API") }
                }

                is PokedexState.Success -> {
                    val pokemonsFiltres = currentState.pokemons.filter { pokemon ->
                        pokemon.nom.contains(searchQuery, ignoreCase = true)
                    }

                    items(pokemonsFiltres) { (idPokemon: Int, nom: String, urlSprite: String, entryDex: Int) ->
                        CarrePokemon(
                            label = nom,
                            cornerRadius = cornerRadius,
                            onClick = onPokemonClick,
                            onLongClick = onPokemonLongClick,
                            idPokemon = idPokemon,
                            entryDex = entryDex,
                            urlSprite = urlSprite
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

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun CarrePokemon(
    label: String,
    cornerRadius: androidx.compose.ui.unit.Dp,
    onClick: (String) -> Unit,
    onLongClick: (Int, Int) -> Unit,
    modifier: Modifier = Modifier,
    idPokemon: Int = -1,
    entryDex: Int = -1,
    urlSprite: String = ""
) {
    Box(
        modifier = modifier
            .aspectRatio(1f)
            .background(
                color = Color(0xFFD9D9D9),
                shape = RoundedCornerShape(cornerRadius)
            )
            .combinedClickable(
                onClick = { onClick(label) },
                onLongClick = {
                    if (idPokemon != -1) {
                        onLongClick(idPokemon, entryDex)
                    }
                }
            )
            .padding(12.dp)
    ) {
        // ID en haut à gauche
        if (entryDex != -1) {
            Text(
                text = "#$entryDex",
                modifier = Modifier.align(Alignment.TopStart),
                color = Color(0xFF666666),
                fontSize = 12.sp,
                fontWeight = FontWeight.Light
            )
        }

        // Image (AsyncImage) au centre
        Box(
            modifier = Modifier
                .size(64.dp)
                .align(Alignment.Center),
            contentAlignment = Alignment.Center
        ) {
            AsyncImage(
                model = urlSprite,
                contentDescription = "Image de $label",
                modifier = Modifier.fillMaxSize()
            )
        }

        // Nom en bas au centre
        Text(
            text = label,
            modifier = Modifier.align(Alignment.BottomCenter),
            color = Color.Black,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

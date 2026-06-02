package fr.mathano.livingdex.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.integerResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fr.mathano.livingdex.R
import fr.mathano.livingdex.data.Pokedex
import fr.mathano.livingdex.data.model.DataPokemon
import fr.mathano.livingdex.ui.components.BarreRecherche
import fr.mathano.livingdex.ui.components.Bulle
import fr.mathano.livingdex.ui.components.CarrePokemon
import fr.mathano.livingdex.ui.components.livingDexString
import kotlinx.coroutines.launch

@Composable
fun EcranPokedex(
    modifier: Modifier = Modifier,
    nomRegion: String,
    idPokedex: Int,
    onPokemonClick: (String) -> Unit = { _ -> },
    onPokemonLongClick: (Int, Int) -> Unit = { _, _ -> },
    recupererPokedexParRegion: suspend (Int) -> List<DataPokemon> = Pokedex::recupererPokedexParRegion,
    recupererEntriesCapturees: suspend (Int) -> Set<Int> = Pokedex::recupererEntriesCapturees,
    changerCapturePokemon: suspend (Int, DataPokemon) -> Boolean = Pokedex::changerCapturePokemon,
) {
    var state by remember { mutableStateOf<PokedexState>(PokedexState.Loading) }
    var searchQuery by remember { mutableStateOf("") }
    var entriesCapturees by remember { mutableStateOf<Set<Int>>(emptySet()) }
    val coroutineScope = rememberCoroutineScope()
    val columnCount = integerResource(R.integer.n_colonnes)
    val cornerRadius = integerResource(R.integer.arrondi).dp

    LaunchedEffect(recupererPokedexParRegion, recupererEntriesCapturees, idPokedex) {
        state = try {
            val pokemons = recupererPokedexParRegion(idPokedex)
            entriesCapturees = recupererEntriesCapturees(idPokedex)
            PokedexState.Success(pokemons)
        } catch (exception: Exception) {
            PokedexState.Error
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
    ) {
        Bulle {
            Text(
                text = livingDexString(R.string.region_title, nomRegion),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            if (state is PokedexState.Success) {
                val totalPokemon = (state as PokedexState.Success).pokemons.size
                val pokemonCaptures = entriesCapturees.size
                val progress = if (totalPokemon == 0) {
                    0f
                } else {
                    pokemonCaptures.toFloat() / totalPokemon
                }

                Text(
                    text = "$pokemonCaptures / $totalPokemon",
                    modifier = Modifier.padding(top = 8.dp),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )

                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 6.dp),
                    color = Color.Black,
                    trackColor = Color.White
                )
            }
        }

        Bulle {
            BarreRecherche(
                valeur = searchQuery,
                onValeurChange = { searchQuery = it }
            )
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(columnCount),
            modifier = modifier
                .fillMaxSize(),
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
                            onLongClick = onPokemonLongClick
                        )
                    }
                }

                is PokedexState.Error -> {
                    item { Text(livingDexString(R.string.error_api)) }
                }

                is PokedexState.Success -> {
                    val pokemonsFiltres = currentState.pokemons.filter { pokemon ->
                        pokemon.nom.contains(searchQuery, ignoreCase = true)
                    }

                    items(pokemonsFiltres) { pokemon ->
                        val (idPokemon: Int, nom: String, urlSprite: String, entryDex: Int) = pokemon

                        CarrePokemon(
                            label = nom,
                            cornerRadius = cornerRadius,
                            idPokemon = idPokemon,
                            entryDex = entryDex,
                            urlSprite = urlSprite,
                            isCaptured = entryDex in entriesCapturees,
                            onClick = {
                                coroutineScope.launch {
                                    val estCapture = changerCapturePokemon(idPokedex, pokemon)
                                    entriesCapturees = if (estCapture) {
                                        entriesCapturees + entryDex
                                    } else {
                                        entriesCapturees - entryDex
                                    }
                                }
                                onPokemonClick(nom)
                            },
                            onLongClick = onPokemonLongClick
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

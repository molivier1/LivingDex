package fr.mathano.livingdex.ui

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import fr.mathano.livingdex.R
import fr.mathano.livingdex.data.Pokedex
import fr.mathano.livingdex.data.model.DataPokemon

@Composable
fun NavigationRecherche(
    modifier: Modifier = Modifier,
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "recherche",
        modifier = modifier,
        enterTransition = { EnterTransition.None },
        exitTransition = { ExitTransition.None },
        popEnterTransition = { EnterTransition.None },
        popExitTransition = { ExitTransition.None }
    ) {
        composable("recherche") {
            EcranRecherche(
                onPokemonLongClick = { idPokemon, entryDex ->
                    navController.navigate("pokemon/$idPokemon/$entryDex")
                }
            )
        }

        composable("pokemon/{idPokemon}/{entryDex}") { backStackEntry ->
            val idPokemon = backStackEntry.arguments?.getString("idPokemon")?.toInt() ?: 0
            val entryDex = backStackEntry.arguments?.getString("entryDex")?.toInt() ?: 0

            EcranPokemonDetail(
                idPokemon = idPokemon,
                entryDex = entryDex
            )
        }
    }
}

@Composable
fun EcranRecherche(
    modifier: Modifier = Modifier,
    onPokemonLongClick: (Int, Int) -> Unit = { _, _ -> },
    recupererPokedexNationalProgressif: suspend ((List<DataPokemon>) -> Unit) -> Unit =
        Pokedex::recupererPokedexNationalProgressif,
) {
    var state by remember { mutableStateOf<RechercheState>(RechercheState.Loading) }
    var searchQuery by remember { mutableStateOf("") }
    val columnCount = integerResource(R.integer.n_colonnes)
    val cornerRadius = integerResource(R.integer.arrondi).dp

    LaunchedEffect(recupererPokedexNationalProgressif) {
        try {
            recupererPokedexNationalProgressif { pokemons ->
                state = RechercheState.Success(pokemons, isLoading = true)
            }

            val currentState = state
            state = if (currentState is RechercheState.Success) {
                currentState.copy(isLoading = false)
            } else {
                RechercheState.Success(emptyList(), isLoading = false)
            }
        } catch (exception: Exception) {
            val currentState = state
            state = if (currentState is RechercheState.Success && currentState.pokemons.isNotEmpty()) {
                currentState.copy(isLoading = false)
            } else {
                RechercheState.Error
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF4F4F4))
    ) {
        Text(
            text = "Pokedex national",
            modifier = Modifier.padding(16.dp),
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )

        if (state is RechercheState.Success && (state as RechercheState.Success).isLoading) {
            Text(
                text = "Chargement... ${(state as RechercheState.Success).pokemons.size} Pokemon",
                modifier = Modifier.padding(start = 16.dp, top = 4.dp, end = 16.dp),
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
        }

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
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF4F4F4)),
            contentPadding = PaddingValues(18.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            when (val currentState = state) {
                RechercheState.Loading -> {
                    items(8) {
                        CarrePokemonNational(
                            label = "",
                            cornerRadius = cornerRadius,
                            onLongClick = onPokemonLongClick
                        )
                    }
                }

                RechercheState.Error -> {
                    item { Text("Erreur API") }
                }

                is RechercheState.Success -> {
                    val pokemonsFiltres = currentState.pokemons.filter { pokemon ->
                        pokemon.nom.contains(searchQuery, ignoreCase = true)
                    }

                    items(pokemonsFiltres) { pokemon ->
                        CarrePokemonNational(
                            label = pokemon.nom,
                            cornerRadius = cornerRadius,
                            onLongClick = onPokemonLongClick,
                            idPokemon = pokemon.idPokemon,
                            entryDex = pokemon.entryDex,
                            urlSprite = pokemon.urlSprite
                        )
                    }
                }
            }
        }
    }
}

private sealed interface RechercheState {
    data object Loading : RechercheState
    data object Error : RechercheState
    data class Success(
        val pokemons: List<DataPokemon>,
        val isLoading: Boolean,
    ) : RechercheState
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun CarrePokemonNational(
    label: String,
    cornerRadius: androidx.compose.ui.unit.Dp,
    onLongClick: (Int, Int) -> Unit,
    modifier: Modifier = Modifier,
    idPokemon: Int = -1,
    entryDex: Int = -1,
    urlSprite: String = "",
) {
    Box(
        modifier = modifier
            .aspectRatio(1f)
            .background(
                color = Color(0xFFD9D9D9),
                shape = RoundedCornerShape(cornerRadius)
            )
            .combinedClickable(
                onClick = {},
                onLongClick = {
                    if (idPokemon != -1) {
                        onLongClick(idPokemon, entryDex)
                    }
                }
            )
            .padding(12.dp)
    ) {
        if (entryDex != -1) {
            Text(
                text = "#$entryDex",
                modifier = Modifier.align(Alignment.TopStart),
                color = Color(0xFF666666),
                fontSize = 12.sp,
                fontWeight = FontWeight.Light
            )
        }

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

        Text(
            text = label,
            modifier = Modifier.align(Alignment.BottomCenter),
            color = Color.Black,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

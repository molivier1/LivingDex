package fr.mathano.livingdex.ui

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.integerResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import fr.mathano.livingdex.R
import fr.mathano.livingdex.data.Pokedex
import fr.mathano.livingdex.data.model.DataPokemon
import fr.mathano.livingdex.ui.components.BarreRecherche
import fr.mathano.livingdex.ui.components.Bulle
import fr.mathano.livingdex.ui.components.CarrePokemon
import fr.mathano.livingdex.ui.components.livingDexString

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
                    navController.navigate("pokemon/$idPokemon/$entryDex/1")
                }
            )
        }

        composable("pokemon/{idPokemon}/{entryDex}/{idPokedex}") { backStackEntry ->
            val idPokemon = backStackEntry.arguments?.getString("idPokemon")?.toInt() ?: 0
            val entryDex = backStackEntry.arguments?.getString("entryDex")?.toInt() ?: 0
            val idPokedex = backStackEntry.arguments?.getString("idPokedex")?.toInt() ?: 0

            EcranPokemonDetail(
                idPokemon = idPokemon,
                entryDex = entryDex,
                idPokedex = idPokedex
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
    ) {
        Bulle {
            Text(
                text = livingDexString(R.string.pokedex_national),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            if (state is RechercheState.Success && (state as RechercheState.Success).isLoading) {
                Text(
                    text = livingDexString(
                        R.string.loading_pokemon_count,
                        (state as RechercheState.Success).pokemons.size
                    ),
                    modifier = Modifier.padding(top = 4.dp),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            BarreRecherche(
                valeur = searchQuery,
                onValeurChange = { searchQuery = it },
                modifier = Modifier
                    .padding(top = 12.dp)
            )
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(columnCount),
            modifier = Modifier
                .fillMaxSize(),
            contentPadding = PaddingValues(18.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            when (val currentState = state) {
                RechercheState.Loading -> {
                    items(8) {
                        CarrePokemon(
                            label = "",
                            cornerRadius = cornerRadius,
                            onLongClick = onPokemonLongClick
                        )
                    }
                }

                RechercheState.Error -> {
                    item { Text(livingDexString(R.string.error_api)) }
                }

                is RechercheState.Success -> {
                    val pokemonsFiltres = currentState.pokemons.filter { pokemon ->
                        pokemon.nom.contains(searchQuery, ignoreCase = true)
                    }

                    items(pokemonsFiltres) { pokemon ->
                        CarrePokemon(
                            label = pokemon.nom,
                            cornerRadius = cornerRadius,
                            idPokemon = pokemon.idPokemon,
                            entryDex = pokemon.entryDex,
                            urlSprite = pokemon.urlSprite,
                            onLongClick = onPokemonLongClick
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

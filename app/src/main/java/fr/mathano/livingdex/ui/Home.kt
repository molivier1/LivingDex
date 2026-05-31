package fr.mathano.livingdex.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import fr.mathano.livingdex.R
import fr.mathano.livingdex.data.Regions
import fr.mathano.livingdex.data.model.DataRegion
import fr.mathano.livingdex.ui.theme.LivingDexTheme

@Composable
fun EcranHome(
    modifier: Modifier = Modifier,
    onRegionClick: (String, Int) -> Unit = { _, _ -> },
    chargerRegions: suspend () -> List<DataRegion> = Regions::recupererRegions,
) {
    var state by remember { mutableStateOf<RegionsState>(RegionsState.Loading) }
    val columnCount = integerResource(R.integer.n_colonnes)
    val cornerRadius = integerResource(R.integer.arrondi).dp

    LaunchedEffect(chargerRegions) {
        state = try {
            RegionsState.Success(chargerRegions())
        } catch (exception: Exception) {
            RegionsState.Error
        }
    }

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
            is RegionsState.Loading -> {
                items(8) { CarreArrondi(label = "", cornerRadius = cornerRadius, idPokedex = 0, onClick = onRegionClick) }
            }
            is RegionsState.Error -> {
                item { Text("Erreur API") }
            }
            is RegionsState.Success -> {
                items(currentState.regions.toList()) { (idRegion, nomRegion, idPokedex) ->
                    CarreArrondi(
                        label = nomRegion,
                        cornerRadius = cornerRadius,
                        idPokedex = idPokedex,
                        onClick = onRegionClick
                    )
                }
            }
        }
    }
}

private sealed interface RegionsState {
    data object Loading : RegionsState
    data object Error : RegionsState
    class Success(val regions: List<DataRegion>) : RegionsState
}

@Composable
private fun CarreArrondi(
    label: String,
    cornerRadius: androidx.compose.ui.unit.Dp,
    idPokedex: Int,
    onClick: (String, Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (idPokedex != -1) {
        Box(
            modifier = modifier
                .aspectRatio(1f)
                .background(
                    color = Color(0xFFD9D9D9),
                    shape = RoundedCornerShape(cornerRadius)
                )
                .clickable {
                    onClick(label, idPokedex)
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
}


@Composable
fun NavigationApp(
    modifier: Modifier = Modifier,
    homeResetSignal: Int = 0,
    onHomeRootChanged: (Boolean) -> Unit = {},
) {

    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    LaunchedEffect(currentRoute) {
        onHomeRootChanged(currentRoute == "home")
    }

    LaunchedEffect(homeResetSignal) {
        if (homeResetSignal > 0) {
            navController.popBackStack("home", inclusive = false)
        }
    }

    NavHost(
        navController = navController,
        startDestination = "home",
        modifier = modifier,
        enterTransition = { EnterTransition.None },
        exitTransition = { ExitTransition.None },
        popEnterTransition = { EnterTransition.None },
        popExitTransition = { ExitTransition.None }
    ) {

        composable("home") {

            EcranHome(
                onRegionClick = { nom, id ->

                    navController.navigate(
                        "pokedex/$nom/$id"
                    )
                }
            )
        }

        composable(
            route = "pokedex/{nom}/{id}",
        ) { backStackEntry ->

            val nom = backStackEntry.arguments?.getString("nom") ?: ""
            val id = backStackEntry.arguments?.getString("id")?.toInt() ?: 0

            EcranPokedex(
                nomRegion = nom,
                idPokedex = id,
                onPokemonLongClick = { idPokemon, entryDex ->
                    navController.navigate("pokemon/$idPokemon/$entryDex")
                }
            )
        }

        composable(
            route = "pokemon/{idPokemon}/{entryDex}",
        ) { backStackEntry ->

            val idPokemon = backStackEntry.arguments?.getString("idPokemon")?.toInt() ?: 0
            val entryDex = backStackEntry.arguments?.getString("entryDex")?.toInt() ?: 0

            EcranPokemonDetail(
                idPokemon = idPokemon,
                entryDex = entryDex
            )
        }
    }
}


/*@Preview(showBackground = true)
@Composable
private fun EcranHomePreview() {
    LivingDexTheme {
        EcranHome(
            chargerRegions = {
                hashMapOf("Kanto" to 1, "Johto" to 2, "Hoenn" to 3, "Sinnoh" to 4)
            }
        )
    }
}*/

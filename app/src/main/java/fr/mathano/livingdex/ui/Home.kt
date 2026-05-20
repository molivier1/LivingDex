package fr.mathano.livingdex.ui

import androidx.compose.foundation.background
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
import fr.mathano.livingdex.R
import fr.mathano.livingdex.data.PokeApiClient
import fr.mathano.livingdex.ui.theme.LivingDexTheme

@Composable
fun EcranHome(
    modifier: Modifier = Modifier,
    chargerRegions: suspend (String) -> List<String> = PokeApiClient::recupererRegions,
) {
    var state by remember { mutableStateOf<RegionsState>(RegionsState.Loading) }
    val columnCount = integerResource(R.integer.n_colonnes)
    val cornerRadius = integerResource(R.integer.arrondi).dp
    val language = Locale.current.language

    LaunchedEffect(chargerRegions, language) {
        state = try {
            RegionsState.Success(chargerRegions(language))
        } catch (exception: Exception) {
            RegionsState.Error
        }
    }

    val labels = when (val currentState = state) {
        RegionsState.Loading -> List(8) { "" }
        RegionsState.Error -> listOf("Erreur API")
        is RegionsState.Success -> currentState.regions
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
        items(labels) { label ->
            CarreArrondi(
                label = label,
                cornerRadius = cornerRadius
            )
        }
    }
}

private sealed interface RegionsState {
    data object Loading : RegionsState
    data object Error : RegionsState
    class Success(val regions: List<String>) : RegionsState
}

@Composable
private fun CarreArrondi(
    label: String,
    cornerRadius: androidx.compose.ui.unit.Dp,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .aspectRatio(1f)
            .background(
                color = Color(0xFFD9D9D9),
                shape = RoundedCornerShape(cornerRadius)
            )
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

@Preview(showBackground = true)
@Composable
private fun EcranHomePreview() {
    LivingDexTheme {
        EcranHome(
            chargerRegions = {
                listOf("Kanto", "Johto", "Hoenn", "Sinnoh", "Unys", "Kalos", "Alola", "Galar")
            }
        )
    }
}

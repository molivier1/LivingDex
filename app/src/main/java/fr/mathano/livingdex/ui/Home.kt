package fr.mathano.livingdex.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import fr.mathano.livingdex.R
import fr.mathano.livingdex.ui.theme.LivingDexTheme

@Composable
fun EcranHome(
    modifier: Modifier = Modifier,
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(R.integer.n_colonnes),
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF4F4F4)),
        contentPadding = PaddingValues(18.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(8) {
            CarreArrondi()
        }
    }
}

@Composable
private fun CarreArrondi(
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .aspectRatio(1f)
            .background(
                color = Color(0xFFD9D9D9),
                shape = RoundedCornerShape(R.integer.arrondi.dp)
            )
    )
}

@Preview(showBackground = true)
@Composable
private fun EcranHomePreview() {
    LivingDexTheme {
        EcranHome()
    }
}

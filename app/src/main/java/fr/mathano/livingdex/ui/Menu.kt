package fr.mathano.livingdex.ui

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import fr.mathano.livingdex.R
import fr.mathano.livingdex.ui.theme.LivingDexTheme

enum class AppDestination(
    val label: String,
    val contentTitle: String,
) {
    HOME("Accueil", "Accueil"),
    RECHERCHER("Rechercher", "Rechercher"),
    PROFIL("Profil", "Profil"),
}

@Composable
fun LivingDexMenu(
    currentDestination: AppDestination?,
    onDestinationSelected: (AppDestination) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        LivingDexMenuContent(
            currentDestination = currentDestination,
            onDestinationSelected = onDestinationSelected
        )
        Spacer(modifier = Modifier.windowInsetsBottomHeight(WindowInsets.navigationBars))
    }
}

@Composable
private fun LivingDexMenuContent(
    currentDestination: AppDestination?,
    onDestinationSelected: (AppDestination) -> Unit,
    modifier: Modifier = Modifier,
) {
    val menuBarHeight = 78.dp

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(menuBarHeight)
            .background(Color.Black)
            .padding(horizontal = 29.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        MenuIconButton(
            destination = AppDestination.HOME,
            onDestinationSelected = onDestinationSelected
        ) {
            MenuImageIcon(
                resourceId = if (currentDestination == AppDestination.HOME) {
                    R.drawable.home_plein
                } else {
                    R.drawable.home
                },
                contentDescription = AppDestination.HOME.label,
                modifier = Modifier.size(52.dp)
            )
        }

        MenuIconButton(
            destination = AppDestination.RECHERCHER,
            onDestinationSelected = onDestinationSelected
        ) {
            MenuImageIcon(
                resourceId = R.drawable.ball,
                contentDescription = AppDestination.RECHERCHER.label,
                modifier = Modifier.size(62.dp)
            )
        }

        MenuIconButton(
            destination = AppDestination.PROFIL,
            onDestinationSelected = onDestinationSelected
        ) {
            MenuImageIcon(
                resourceId = if (currentDestination == AppDestination.PROFIL) {
                    R.drawable.utilisateur_plein
                } else {
                    R.drawable.utilisateur
                },
                contentDescription = AppDestination.PROFIL.label,
                modifier = Modifier.size(52.dp)
            )
        }
    }
}

@Composable
private fun MenuIconButton(
    destination: AppDestination,
    onDestinationSelected: (AppDestination) -> Unit,
    icon: @Composable () -> Unit,
) {
    Box(
        modifier = Modifier
            .size(72.dp)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onDestinationSelected(destination) },
        contentAlignment = Alignment.Center
    ) {
        icon()
    }
}

@Composable
private fun MenuImageIcon(
    @DrawableRes resourceId: Int,
    contentDescription: String,
    modifier: Modifier = Modifier,
) {
    Image(
        painter = painterResource(resourceId),
        contentDescription = contentDescription,
        contentScale = ContentScale.Fit,
        modifier = modifier.fillMaxSize()
    )
}

@Preview(showBackground = true)
@Composable
private fun LivingDexMenuPreview() {
    LivingDexTheme {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .background(Color(0xFFF4F4F4)),
            contentAlignment = Alignment.BottomCenter
        ) {
            LivingDexMenu(
                currentDestination = AppDestination.RECHERCHER,
                onDestinationSelected = {}
            )
        }
    }
}

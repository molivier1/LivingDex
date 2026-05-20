package fr.mathano.livingdex.ui

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fr.mathano.livingdex.R
import fr.mathano.livingdex.ui.theme.LivingDexTheme
import kotlin.math.hypot

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
    currentDestination: AppDestination,
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
    currentDestination: AppDestination,
    onDestinationSelected: (AppDestination) -> Unit,
    modifier: Modifier = Modifier,
) {
    val menuBarHeight = 78.dp
    val pokeBallSize = 101.dp
    val pokeBallAssetSize = pokeBallSize * 2f

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(menuBarHeight + pokeBallSize / 2f)
    ) {
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
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

        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .size(pokeBallSize)
                .clipToBounds()
                .circularTapTarget(pokeBallSize) {
                    onDestinationSelected(AppDestination.RECHERCHER)
                },
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(R.drawable.ball),
                contentDescription = AppDestination.RECHERCHER.label,
                contentScale = ContentScale.Fit,
                modifier = Modifier.size(pokeBallAssetSize)
            )
        }

        Text(
            text = AppDestination.RECHERCHER.label,
            color = Color.White,
            fontSize = 17.sp,
            lineHeight = 21.sp,
            fontWeight = if (currentDestination == AppDestination.RECHERCHER) {
                FontWeight.Medium
            } else {
                FontWeight.Normal
            },
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = pokeBallSize - 7.dp)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) { onDestinationSelected(AppDestination.RECHERCHER) }
        )
    }
}

// Merci l'IA
private fun Modifier.circularTapTarget(
    size: Dp,
    onTap: () -> Unit,
): Modifier = this.pointerInput(size, onTap) {
    val radius = size.toPx() / 2f
    val center = Offset(radius, radius)

    detectTapGestures { offset ->
        val distanceFromCenter = hypot(
            x = offset.x - center.x,
            y = offset.y - center.y
        )

        if (distanceFromCenter <= radius) {
            onTap()
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

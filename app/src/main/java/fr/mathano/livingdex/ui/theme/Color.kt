package fr.mathano.livingdex.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

val Purple80 = Color(0xFFD0BCFF)
val PurpleGrey80 = Color(0xFFCCC2DC)
val Pink80 = Color(0xFFEFB8C8)

val Purple40 = Color(0xFF6650a4)
val PurpleGrey40 = Color(0xFF625b71)
val Pink40 = Color(0xFF7D5260)

val LivingDexBackgroundGradient: Brush = Brush.verticalGradient(
    colors = listOf(
        Color(0xFF3B4CCA),
        Color(0xFF181F54)
    )
)

val LivingDexBubbleGradient: Brush = Brush.radialGradient(
    colorStops = arrayOf(
        0.4f to Color(0xFF8FF7FF),
        1f to Color(0xFF90D5FF)
    )
)

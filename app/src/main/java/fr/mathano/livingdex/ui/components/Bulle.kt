package fr.mathano.livingdex.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import fr.mathano.livingdex.ui.theme.LivingDexBubbleGradient

@Composable
fun Bulle(
    modifier: Modifier = Modifier,
    outerPadding: PaddingValues = PaddingValues(horizontal = 18.dp, vertical = 8.dp),
    innerPadding: PaddingValues = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(outerPadding)
            .background(
                brush = LivingDexBubbleGradient,
                shape = RoundedCornerShape(20.dp)
            )
            .padding(innerPadding),
        content = content
    )
}

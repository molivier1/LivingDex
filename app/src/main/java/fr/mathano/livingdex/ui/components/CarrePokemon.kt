package fr.mathano.livingdex.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CarrePokemon(
    label: String,
    cornerRadius: Dp,
    modifier: Modifier = Modifier,
    idPokemon: Int = -1,
    entryDex: Int = -1,
    urlSprite: String = "",
    isCaptured: Boolean = false,
    onClick: () -> Unit = {},
    onLongClick: (Int, Int) -> Unit = { _, _ -> },
) {
    Box(
        modifier = modifier
            .aspectRatio(1f)
            .background(
                color = if (isCaptured) Color(0xFF9E9E9E) else Color(0xFFD9D9D9),
                shape = RoundedCornerShape(cornerRadius)
            )
            .combinedClickable(
                onClick = onClick,
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
                color = Color.Black,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
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

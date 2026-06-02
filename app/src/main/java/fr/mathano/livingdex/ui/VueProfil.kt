package fr.mathano.livingdex.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import fr.mathano.livingdex.R
import fr.mathano.livingdex.data.AppLanguage
import fr.mathano.livingdex.ui.components.Bulle

@Composable
fun NavigationProfil(
    modifier: Modifier = Modifier,
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "profil",
        modifier = modifier
    ) {
        composable("profil") {
            EcranProfil(
                onSettingsClick = {
                    navController.navigate("settings")
                }
            )
        }

        composable("settings") {
            EcranSettings()
        }
    }
}

@Composable
private fun EcranProfil(
    onSettingsClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
    ) {
        BoutonSettings(
            onClick = onSettingsClick,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 18.dp, end = 18.dp)
        )

        Text(
            text = "Profil",
            color = Color.Black,
            fontSize = 24.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

@Composable
private fun BoutonSettings(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .size(52.dp)
            .background(
                color = Color.Black,
                shape = RoundedCornerShape(12.dp)
            )
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(R.drawable.settings),
            contentDescription = "Parametres",
            contentScale = ContentScale.Fit,
            modifier = Modifier.size(32.dp)
        )
    }
}

@Composable
private fun EcranSettings(
    modifier: Modifier = Modifier,
) {
    var selectedLanguage by remember { mutableStateOf(AppLanguage.current()) }
    val languages = listOf(
        LanguageOption("fr", "🇫🇷", "Français"),
        LanguageOption("en", "🇬🇧", "Anglais"),
        LanguageOption("es", "🇪🇸", "Espagnol"),
        LanguageOption("it", "🇮🇹", "Italien"),
        LanguageOption("ru", "🇷🇺", "Russe"),
        LanguageOption("ja", "🇯🇵", "Japonais"),
        LanguageOption("zh-Hans", "🇨🇳", "Chinois")
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Bulle {
            Text(
                text = "Langue",
                modifier = Modifier.fillMaxWidth(),
                color = Color.Black,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        }

        languages.forEach { language ->
            Bulle(
                modifier = Modifier.clickable {
                    selectedLanguage = language.code
                    AppLanguage.set(language.code)
                }
            ) {
                Text(
                    text = language.label(selectedLanguage == language.code),
                    modifier = Modifier.fillMaxWidth(),
                    color = Color.Black,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

private data class LanguageOption(
    val code: String,
    val emoji: String,
    val name: String,
) {
    fun label(isSelected: Boolean): String {
        val selectedText = if (isSelected) " ✓" else ""
        return "$emoji $name$selectedText"
    }
}

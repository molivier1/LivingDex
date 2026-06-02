package fr.mathano.livingdex

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import fr.mathano.livingdex.data.AppLanguage
import fr.mathano.livingdex.data.local.DatabaseProvider
import fr.mathano.livingdex.ui.NavigationProfil
import fr.mathano.livingdex.ui.NavigationRecherche
import fr.mathano.livingdex.ui.components.AppDestination
import fr.mathano.livingdex.ui.components.LivingDexMenu
import fr.mathano.livingdex.ui.theme.LivingDexBackgroundGradient
import fr.mathano.livingdex.ui.theme.LivingDexTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DatabaseProvider.init(applicationContext)
        AppLanguage.init(applicationContext)
        enableEdgeToEdge()
        setContent {
            LivingDexTheme {
                LivingDexApp()
            }
        }
    }
}

@PreviewScreenSizes
@Composable
fun LivingDexApp() {
    var currentDestination by rememberSaveable { mutableStateOf(AppDestination.HOME) }
    var selectedMenuDestination by rememberSaveable { mutableStateOf<AppDestination?>(AppDestination.HOME) }
    var homeResetSignal by rememberSaveable { mutableStateOf(0) }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(LivingDexBackgroundGradient),
        containerColor = Color.Transparent,
        bottomBar = {
            LivingDexMenu(
                currentDestination = selectedMenuDestination,
                onDestinationSelected = { destination ->
                    if (destination == selectedMenuDestination) {
                        return@LivingDexMenu
                    }

                    val wasInHomeSection = currentDestination == AppDestination.HOME
                    val wasOnHomeRoot = selectedMenuDestination == AppDestination.HOME

                    currentDestination = destination
                    selectedMenuDestination = destination

                    if (destination == AppDestination.HOME && wasInHomeSection && !wasOnHomeRoot) {
                        homeResetSignal++
                    }
                }
            )
        }
    ) { innerPadding ->
        when (currentDestination) {
            AppDestination.HOME -> {
                fr.mathano.livingdex.ui.NavigationApp(
                    modifier = Modifier.padding(innerPadding),
                    homeResetSignal = homeResetSignal,
                    onHomeRootChanged = { isHomeRoot ->
                        selectedMenuDestination = if (isHomeRoot) {
                            AppDestination.HOME
                        } else {
                            null
                        }
                    }
                )
            }

            AppDestination.RECHERCHER -> {
                NavigationRecherche(
                    modifier = Modifier.padding(innerPadding)
                )
            }

            AppDestination.PROFIL -> {
                NavigationProfil(
                    modifier = Modifier.padding(innerPadding)
                )
            }
        }
    }
}

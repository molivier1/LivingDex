package fr.mathano.livingdex

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.compose.ui.unit.sp
import fr.mathano.livingdex.data.local.DatabaseProvider
import fr.mathano.livingdex.ui.AppDestination
import fr.mathano.livingdex.ui.LivingDexMenu
import fr.mathano.livingdex.ui.theme.LivingDexTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DatabaseProvider.init(applicationContext)
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
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            LivingDexMenu(
                currentDestination = selectedMenuDestination,
                onDestinationSelected = { destination ->
                    currentDestination = destination
                    selectedMenuDestination = destination

                    if (destination == AppDestination.HOME) {
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

            AppDestination.RECHERCHER,
            AppDestination.PROFIL -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xFFF4F4F4))
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = currentDestination.contentTitle,
                        color = Color.Black,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

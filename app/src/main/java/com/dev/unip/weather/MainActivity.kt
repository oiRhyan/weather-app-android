package com.dev.unip.weather

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.dev.unip.weather.ui.components.BottomNavigator
import com.dev.unip.weather.ui.screens.home.HomeScreen
import com.dev.unip.weather.ui.screens.weather.WeatherDetailScreen
import com.dev.unip.weather.ui.theme.WeatherTheme
import com.dev.unip.weather.ui.theme.WeatherDeepBlue
import com.dev.unip.weather.ui.theme.WeatherNightBlue
import com.dev.unip.weather.ui.theme.WeatherSkyBlue
import com.dev.unip.weather.ui.viewmodels.WeatherViewModel
import org.koin.androidx.compose.koinViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )

        setContent {
            WeatherTheme {
                val navHostController: NavHostController = rememberNavController()
                val weatherViewModel = koinViewModel<WeatherViewModel>()

                val currentDestination by navHostController.currentBackStackEntryAsState()
                val currentScreen = currentDestination?.destination?.route ?: "home"

                val backgroundGradient = Brush.verticalGradient(
                    colors = listOf(
                        WeatherNightBlue,
                        WeatherDeepBlue,
                        WeatherSkyBlue
                    )
                )

                Scaffold(
                    bottomBar = {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 28.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            BottomNavigator(
                                currentRoute = currentScreen,
                                onSelectedDestination = { route ->
                                    navHostController.navigate(route) {
                                        popUpTo(navHostController.graph.startDestinationId) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            )
                        }
                    }
                ) { paddingValues ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(backgroundGradient)
                            .padding(paddingValues)
                    ) {
                        NavHost(
                            navController = navHostController,
                            startDestination = "home"
                        ) {
                            composable("home") {
                                HomeScreen(
                                    viewModel = weatherViewModel,
                                    onNavigateToDetail = {
                                        navHostController.navigate("weather") {
                                            launchSingleTop = true
                                        }
                                    }
                                )
                            }
                            composable("weather") {
                                WeatherDetailScreen(
                                    viewModel = weatherViewModel,
                                    onNavigateToSearch = {
                                        navHostController.navigate("home") {
                                            popUpTo("home") { inclusive = true }
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

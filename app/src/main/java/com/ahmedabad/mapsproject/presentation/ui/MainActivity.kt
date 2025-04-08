package com.ahmedabad.mapsproject.presentation.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import com.ahmedabad.mapsproject.presentation.theme.MapsProjectTheme
import com.ahmedabad.mapsproject.presentation.ui.app_nav.AppNavigation
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MapsProjectTheme {
                val navController = rememberNavController()
                AppNavigation(navController = navController)
            }
        }
    }
}

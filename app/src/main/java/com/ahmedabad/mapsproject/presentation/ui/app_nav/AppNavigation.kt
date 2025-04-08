package com.ahmedabad.mapsproject.presentation.ui.app_nav

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.ahmedabad.mapsproject.presentation.ui.screens.add_location.AddLocationScreen
import com.ahmedabad.mapsproject.presentation.ui.screens.edit_location.EditLocationScreen
import com.ahmedabad.mapsproject.presentation.ui.screens.location_list.LocationListScreen
import com.ahmedabad.mapsproject.presentation.ui.screens.location_list.LocationListViewModel
import com.ahmedabad.mapsproject.presentation.ui.screens.map_screen.MapScreen

@Composable
fun AppNavigation(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Destination.LocationList.route
    ) {
        composable(Destination.LocationList.route) {
            val viewModel = hiltViewModel<LocationListViewModel>()
            LocationListScreen(
                navController = navController,
                viewModel = viewModel
            )
        }

        composable(Destination.AddLocation.route) {
            val viewModel: LocationListViewModel = hiltViewModel()
            AddLocationScreen(
                onBack = { navController.popBackStack() },
                onSaveLocation = { location ->
                    viewModel.addLocation(location)
                    navController.popBackStack()
                }
            )
        }

        composable(Destination.Map.route) {
            MapScreen(onBack = { navController.popBackStack() })
        }

        composable(
            route = Destination.EditLocation.route,
            arguments = listOf(
                navArgument("locationId") {
                    type = NavType.IntType
                }
            )
        ) { backStackEntry ->
            val locationId = backStackEntry.arguments?.getInt("locationId") ?: 0
            EditLocationScreen(
                locationId = locationId,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
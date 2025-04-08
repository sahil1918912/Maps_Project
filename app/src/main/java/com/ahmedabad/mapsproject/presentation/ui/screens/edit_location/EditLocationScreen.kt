package com.ahmedabad.mapsproject.presentation.ui.screens.edit_location

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.ahmedabad.mapsproject.presentation.ui.screens.edit_location.EditLocationViewModel
import com.ahmedabad.mapsproject.presentation.ui.screens.add_location.AddLocationScreen

@Composable
fun EditLocationScreen(
    locationId: Int,
    onBack: () -> Unit,
    viewModel: EditLocationViewModel = hiltViewModel()
) {
    // Correct way to collect StateFlow in Compose
    val locationState = viewModel.location.collectAsState()

    // Load location when screen opens
    LaunchedEffect(Unit) {
        viewModel.loadLocation(locationId)
    }

    when (val location = locationState.value) {
        null -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
        else -> {
            AddLocationScreen(
                onBack = onBack,
                initialLocation = location,
                onSaveLocation = { updatedLocation ->
                    viewModel.updateLocation(updatedLocation)
                    onBack()
                }
            )
        }
    }
}
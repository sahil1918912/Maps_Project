package com.ahmedabad.mapsproject.presentation.ui.screens.map_screen


import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.ahmedabad.mapsproject.presentation.ui.components.CentralAppTopBar
import com.ahmedabad.mapsproject.util.decodePolyline
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberCameraPositionState

@Composable
fun MapScreen(
    onBack: () -> Unit,
    viewModel: MapViewModel = hiltViewModel(),
) {
    val locations by viewModel.locations.collectAsState()
    val routePolyline by viewModel.routePolyline.collectAsState()
    val primaryLocation = remember { locations.firstOrNull { it.isPrimary } }

    val cameraPositionState = rememberCameraPositionState {
        position = primaryLocation?.let {
            CameraPosition.fromLatLngZoom(LatLng(it.latitude, it.longitude), 10f)
        } ?: CameraPosition.fromLatLngZoom(LatLng(0.0, 0.0), 2f)
    }

    Scaffold(
        topBar = {
            CentralAppTopBar(
                title = "Location Path",
                showBackIcon = true,
                showFilterIcon = false,
                onBackClick = onBack
            )
        },
        content = { padding ->
            Box(modifier = Modifier
                .fillMaxSize()
                .padding(padding)) {
                GoogleMap(
                    modifier = Modifier.fillMaxSize(),
                    cameraPositionState = cameraPositionState,
                    uiSettings = MapUiSettings(zoomControlsEnabled = true)
                ) {
                    locations.forEach { location ->
                        Marker(
                            state = MarkerState(LatLng(location.latitude, location.longitude)),
                            title = location.name,
                            icon = if (location.isPrimary) {
                                BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)
                            } else {
                                BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)
                            }
                        )
                    }

                    routePolyline?.let { polyline ->
                        Polyline(
                            points = polyline.decodePolyline(),
                            color = MaterialTheme.colorScheme.primary,
                            width = 8f
                        )
                    }
                }
            }
        }
    )
}


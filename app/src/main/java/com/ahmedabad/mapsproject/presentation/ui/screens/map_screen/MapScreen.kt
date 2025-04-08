package com.ahmedabad.mapsproject.presentation.ui.screens.map_screen


import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ahmedabad.mapsproject.presentation.ui.components.CentralAppTopBar
import com.ahmedabad.mapsproject.util.decodePolyline
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
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
    val primaryLocation = remember(locations) { locations.firstOrNull { it.isPrimary } }
    val cameraPositionState = rememberCameraPositionState()

    LaunchedEffect(primaryLocation) {
        if (primaryLocation != null) {
            cameraPositionState.move(
                CameraUpdateFactory.newLatLngZoom(
                    LatLng(primaryLocation.latitude, primaryLocation.longitude),
                    10f
                )
            )
        }
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
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                GoogleMap(
                    modifier = Modifier.fillMaxSize(),
                    cameraPositionState = cameraPositionState,
                    uiSettings = MapUiSettings(
                        zoomControlsEnabled = true,
                        zoomGesturesEnabled = true
                    ),
                    properties = MapProperties(
                        minZoomPreference = 3f,
                        maxZoomPreference = 20f
                    )
                ) {
                    locations.forEachIndexed { index, location ->
                        Marker(
                            state = MarkerState(LatLng(location.latitude, location.longitude)),
                            title = "${index + 1}. ${location.name}",
                            snippet = if (location.isPrimary) "Primary Location" else null,
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
                            color = Color.Blue,
                            width = 8f
                        )
                    }
                }

                if (primaryLocation != null) {
                    FloatingActionButton(
                        onClick = {
                            cameraPositionState.move(
                                CameraUpdateFactory.newLatLngZoom(
                                    LatLng(primaryLocation.latitude, primaryLocation.longitude),
                                    14f
                                )
                            )
                        },
                        modifier = Modifier
                            .padding(16.dp)
                            .align(Alignment.BottomEnd)
                    ) {
                        Icon(Icons.Default.LocationOn, contentDescription = "Focus on primary")
                    }
                }
            }
        }
    )
}


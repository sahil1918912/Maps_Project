package com.ahmedabad.mapsproject.presentation.ui.screens.add_location


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ahmedabad.mapsproject.domain.model.LocationModel
import com.ahmedabad.mapsproject.presentation.ui.components.CentralAppTopBar
import com.ahmedabad.mapsproject.presentation.ui.components.SearchTextField
import com.ahmedabad.mapsproject.presentation.ui.screens.add_location.AddLocationViewModel
import com.ahmedabad.mapsproject.util.Resource
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.delay

@Composable
fun AddLocationScreen(
    onBack: () -> Unit,
    initialLocation: LocationModel? = null,
    onSaveLocation: (LocationModel) -> Unit = { _ -> },
    viewModel: AddLocationViewModel = hiltViewModel()
) {
    val searchQuery by viewModel.searchQuery.collectAsState()
    val searchResults by viewModel.searchResults.collectAsState()
    val selectedLocation by viewModel.selectedLocation.collectAsState()
    val saveSuccess by viewModel.saveSuccess.collectAsState()
    val showSearchResults = remember { mutableStateOf(false) }

    val defaultLocation = LatLng(34.0479, 100.6197)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(defaultLocation, 3f)
    }

    LaunchedEffect(initialLocation) {
        if (initialLocation != null) {
            viewModel.onSearchQueryChanged(initialLocation.name)
            viewModel.searchPlaces(initialLocation.name)
        }
    }

    if (initialLocation != null) {
        LaunchedEffect(Unit) {
            viewModel.initializeWithLocation(initialLocation)
        }
    }

    LaunchedEffect(saveSuccess) {
        if (saveSuccess) {
            onBack()
        }
    }

    LaunchedEffect(selectedLocation) {
        selectedLocation?.let { location ->
            cameraPositionState.position = CameraPosition.fromLatLngZoom(
                LatLng(location.latitude, location.longitude),
                15f
            )
            showSearchResults.value = false
        }
    }

    LaunchedEffect(searchQuery) {
        if (searchQuery.length >= 3) {
            delay(300)
            viewModel.searchPlaces(searchQuery)
            showSearchResults.value = true
        } else {
            showSearchResults.value = false
        }
    }

    Scaffold(
        topBar = {
            CentralAppTopBar(
                title = if (initialLocation != null) "Edit Location" else "Add Location",
                showBackIcon = true,
                showFilterIcon = false,
                onBackClick = onBack
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState
            ) {
                selectedLocation?.let { location ->
                    Marker(
                        state = MarkerState(
                            position = LatLng(
                                location.latitude,
                                location.longitude
                            )
                        ),
                        title = location.name,
                        snippet = location.address
                    )
                }
            }

            Column(modifier = Modifier.fillMaxWidth()) {
                SearchTextField(
                    value = searchQuery,
                    onValueChange = { query ->
                        viewModel.onSearchQueryChanged(query)
                        viewModel.onNewSearchSelection()
                        if (query.isEmpty()) {
                            showSearchResults.value = false
                        }
                    },
                    modifier = Modifier.padding(16.dp)
                )

                if (showSearchResults.value) {
                    when (searchResults) {
                        is Resource.Success -> {
                            LocationSearchResults(
                                locations = (searchResults as Resource.Success).data ?: emptyList(),
                                onLocationSelected = { placeId ->
                                    viewModel.onLocationSelected(placeId)
                                    showSearchResults.value = false
                                },
                                modifier = Modifier
                                    .heightIn(max = 300.dp)
                                    .padding(horizontal = 16.dp)
                            )
                        }
                        is Resource.Loading -> {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator()
                            }
                        }
                        else -> {}
                    }
                }
            }

            if (selectedLocation != null) {
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp)
                ) {
                    Button(
                        onClick = {
                            selectedLocation?.let { location ->
                                val locationToSave = location.copy(
                                    id = initialLocation?.id ?: 0
                                )
                                onSaveLocation(locationToSave)
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(if (initialLocation != null) "Update Location" else "Save Location")
                    }
                }
            }
        }
    }
}

@Composable
private fun LocationSearchResults(
    locations: List<LocationModel>,
    onLocationSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        items(locations) { location ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp),
                onClick = { location.placeId?.let { onLocationSelected(it) } },
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = location.name,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold
                    )
                    if (location.address.isNotEmpty()) {
                        Text(
                            text = location.address,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}
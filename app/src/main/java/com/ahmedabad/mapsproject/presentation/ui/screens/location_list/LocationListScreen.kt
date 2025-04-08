package com.ahmedabad.mapsproject.presentation.ui.screens.location_list

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.ahmedabad.mapsproject.R
import com.ahmedabad.mapsproject.domain.model.LocationModel
import com.ahmedabad.mapsproject.presentation.ui.app_nav.Destination
import com.ahmedabad.mapsproject.presentation.ui.components.CentralAppTopBar
import com.ahmedabad.mapsproject.presentation.ui.screens.location_list.LocationListViewModel
import com.ahmedabad.mapsproject.util.DistanceCalculator

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocationListScreen(
    navController: NavController,
    viewModel: LocationListViewModel = hiltViewModel()
) {
    val sortOrder by viewModel.sortOrder.collectAsState()
    val locations by viewModel.locations.collectAsState()
    val primaryLocation by viewModel.primaryLocation.collectAsState()

    var showFilterSheet by remember { mutableStateOf(false) }

    val addLocation by viewModel.addLocation.collectAsState()
    LaunchedEffect(addLocation) {
        if (addLocation) {
            navController.navigate(Destination.AddLocation.route)
            viewModel.resetAddLocationState()
        }
    }

    val editLocationId by viewModel.editLocationId.collectAsState()
    LaunchedEffect(editLocationId) {
        editLocationId?.let { id ->
            navController.navigate(Destination.EditLocation.createRoute(id))
            viewModel.resetEditLocationState()
        }
    }

    val savedStateHandle = navController.currentBackStackEntry?.savedStateHandle
    val newLocation by savedStateHandle?.getStateFlow<LocationModel?>("new_location", null)
        ?.collectAsState() ?: remember { mutableStateOf(null) }

    LaunchedEffect(newLocation) {
        newLocation?.let { location ->
            viewModel.addLocation(location)
            savedStateHandle?.remove<LocationModel>("new_location")
        }
    }

    if (showFilterSheet) {
        FilterBottomSheet(
            sortOrder = sortOrder,
            onSortOrderChange = { viewModel.setSortOrder(it) },
            onDismiss = { showFilterSheet = false }
        )
    }

    Scaffold(
        topBar = {
            CentralAppTopBar(
                title = "Source Locations",
                showBackIcon = false,
                showFilterIcon = true,
                onLogoClick = {  },
                onFilterClick = { showFilterSheet = true },
            )
        },
        floatingActionButton = {
            if (locations.isNotEmpty()) {
                FloatingActionButton(
                    onClick = { navController.navigate(Destination.AddLocation.route) },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = Color.White,
                    modifier = Modifier.padding(bottom = 60.dp, end = 10.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add location")
                }
            }
        },
        content = { padding ->
            if (locations.isEmpty()) {
                EmptyStateView(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    onAddClick = { navController.navigate(Destination.AddLocation.route) }
                )
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                ) {
                    LocationListContent(
                        locations = locations,
                        primaryLocation = primaryLocation,
                        onDeleteClick = { location -> viewModel.deleteLocation(location) },
                        onEditClick = { location ->
                            navController.navigate(Destination.EditLocation.createRoute(location.id))
                        },
                        onSetPrimary = { viewModel.setPrimaryLocation(it) },
                        modifier = Modifier.weight(1f)
                    )

                    OutlinedButton(
                        onClick = { navController.navigate(Destination.Map.route) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Text("Show Path", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterBottomSheet(
    sortOrder: LocationListViewModel.SortOrder,
    onSortOrderChange: (LocationListViewModel.SortOrder) -> Unit,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Sort by distance ",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = sortOrder == LocationListViewModel.SortOrder.ASCENDING,
                    onClick = {
                        onSortOrderChange(LocationListViewModel.SortOrder.ASCENDING)
                        onDismiss()
                    },
                    label = { Text("Ascending") }
                )

                FilterChip(
                    selected = sortOrder == LocationListViewModel.SortOrder.DESCENDING,
                    onClick = {
                        onSortOrderChange(LocationListViewModel.SortOrder.DESCENDING)
                        onDismiss()
                    },
                    label = { Text("Descending") }
                )
            }

            // Extra padding at the bottom
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun LocationListContent(
    locations: List<LocationModel>,
    primaryLocation: LocationModel?,
    onDeleteClick: (LocationModel) -> Unit,
    onEditClick: (LocationModel) -> Unit,
    onSetPrimary: (LocationModel) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(locations) { location ->
            LocationListItem(
                location = location,
                primaryLocation = primaryLocation,
                onDeleteClick = onDeleteClick,
                onEditClick = onEditClick,
                onSetPrimary = onSetPrimary
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LocationListItem(
    location: LocationModel,
    primaryLocation: LocationModel?,
    onDeleteClick: (LocationModel) -> Unit,
    onEditClick: (LocationModel) -> Unit,
    onSetPrimary: (LocationModel) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 2.dp, vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(10.dp)
        ) {
            // Top row with name and primary badge/button
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = location.name,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = if (location.isPrimary) FontWeight.Bold else FontWeight.Normal
                    ),
                    modifier = Modifier.weight(1f)
                )

                if (location.isPrimary) {
                    Surface(
                        color = MaterialTheme.colorScheme.primary,
                        shape = MaterialTheme.shapes.small
                    ) {
                        Text(
                            text = "Primary Location",
                            color = MaterialTheme.colorScheme.onPrimary,
                            style = MaterialTheme.typography.labelSmall,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                } else {
                    TextButton(
                        onClick = { onSetPrimary(location) },
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp)
                    ) {
                        Text(
                            text = "Set as Primary",
                            style = MaterialTheme.typography.labelMedium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Bottom row with address/distance and action buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                // Address and distance information
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = location.address,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    // Show distance if this isn't primary and we have a primary location
                    if (!location.isPrimary && primaryLocation != null) {
                        Spacer(modifier = Modifier.height(4.dp))
                        val distance = DistanceCalculator.calculateDistance(
                            primaryLocation.latitude,
                            primaryLocation.longitude,
                            location.latitude,
                            location.longitude
                        )
                        Text(
                            text = "%.1f km from primary location".format(distance),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                // Action buttons
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { onEditClick(location) },
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = "Edit",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }

                    IconButton(
                        onClick = { onDeleteClick(location) },
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Delete",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun EmptyStateView(
    modifier: Modifier = Modifier,
    onAddClick: () -> Unit
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.maps_logo),
            contentDescription = "No Location",
            modifier = Modifier.size(80.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "No place available.\nLets start by adding few places",
            fontSize = 16.sp,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedButton(
            onClick = onAddClick,
            border = ButtonDefaults.outlinedButtonBorder,
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Text("Add POI", fontWeight = FontWeight.Bold)
        }
    }
}
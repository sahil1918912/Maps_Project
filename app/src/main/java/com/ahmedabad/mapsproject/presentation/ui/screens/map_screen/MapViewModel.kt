package com.ahmedabad.mapsproject.presentation.ui.screens.map_screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ahmedabad.mapsproject.domain.model.LocationModel
import com.ahmedabad.mapsproject.domain.usecase.GetAllLocationsUseCase
import com.ahmedabad.mapsproject.domain.usecase.GetRoutePathUseCase
import com.ahmedabad.mapsproject.util.DistanceCalculator
import com.ahmedabad.mapsproject.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MapViewModel @Inject constructor(
    private val getAllLocationsUseCase: GetAllLocationsUseCase,
    private val getRoutePathUseCase: GetRoutePathUseCase
) : ViewModel() {

    private val _locations = MutableStateFlow<List<LocationModel>>(emptyList())
    val locations: StateFlow<List<LocationModel>> = _locations

    private val _routePolyline = MutableStateFlow<String?>(null)
    val routePolyline: StateFlow<String?> = _routePolyline

    init {
        loadLocations()
    }

    private fun loadLocations() {
        viewModelScope.launch {
            getAllLocationsUseCase().collectLatest { locations ->
                val sortedLocations = sortLocations(locations)
                _locations.value = sortedLocations

                if (sortedLocations.size >= 2) {
                    getRoutePath(sortedLocations)
                }
            }
        }
    }

    private fun sortLocations(locations: List<LocationModel>): List<LocationModel> {
        val primary = locations.firstOrNull { it.isPrimary } ?: return locations

        return locations.sortedBy { location ->
            if (location.isPrimary) Double.MIN_VALUE
            else DistanceCalculator.calculateDistance(
                primary.latitude,
                primary.longitude,
                location.latitude,
                location.longitude
            )
        }
    }

    private fun getRoutePath(locations: List<LocationModel>) {
        viewModelScope.launch {
            when (val result = getRoutePathUseCase(locations)) {
                is Resource.Success -> {
                    _routePolyline.value = result.data
                }
                is Resource.Error -> {
                    // Handle error
                }
                else -> {}
            }
        }
    }
}
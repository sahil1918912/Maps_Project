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
                val optimizedRoute = calculateOptimizedRoute(locations)
                _locations.value = optimizedRoute

                if (optimizedRoute.size >= 2) {
                    getRoutePath(optimizedRoute)
                }
            }
        }
    }


    private fun calculateOptimizedRoute(locations: List<LocationModel>): List<LocationModel> {
        if (locations.isEmpty()) return emptyList()


        val primary = locations.firstOrNull { it.isPrimary } ?: locations.first()

        val result = mutableListOf<LocationModel>()
        result.add(primary)

        val remainingLocations = locations.filter { it.id != primary.id }.toMutableList()

        while (remainingLocations.isNotEmpty()) {
            val lastLocation = result.last()
            val closestLocation = remainingLocations.minByOrNull { location ->
                DistanceCalculator.calculateDistance(
                    lastLocation.latitude, lastLocation.longitude,
                    location.latitude, location.longitude
                )
            } ?: break

            result.add(closestLocation)
            remainingLocations.remove(closestLocation)
        }

        return result
    }

    private fun getRoutePath(locations: List<LocationModel>) {
        viewModelScope.launch {
            when (val result = getRoutePathUseCase(locations)) {
                is Resource.Success -> {
                    _routePolyline.value = result.data
                }
                is Resource.Error -> {

                }
                else -> {}
            }
        }
    }
}
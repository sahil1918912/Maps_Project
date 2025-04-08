package com.ahmedabad.mapsproject.presentation.ui.screens.map_screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ahmedabad.mapsproject.domain.model.LocationModel
import com.ahmedabad.mapsproject.domain.usecase.GetAllLocationsUseCase
import com.ahmedabad.mapsproject.domain.usecase.GetRoutePathUseCase
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

    private val _loadingState = MutableStateFlow(false)

    init {
        loadLocations()
    }

    private fun loadLocations() {
        viewModelScope.launch {
            getAllLocationsUseCase().collectLatest { locations ->
                _locations.value = locations
                if (locations.size >= 2) {
                    getRoutePath(locations)
                }
            }
        }
    }

    private fun getRoutePath(locations: List<LocationModel>) {
        viewModelScope.launch {
            _loadingState.value = true
            when (val result = getRoutePathUseCase(locations)) {
                is Resource.Success -> {
                    _routePolyline.value = result.data
                }
                is Resource.Error -> {

                }
                else -> {}
            }
            _loadingState.value = false
        }
    }
}
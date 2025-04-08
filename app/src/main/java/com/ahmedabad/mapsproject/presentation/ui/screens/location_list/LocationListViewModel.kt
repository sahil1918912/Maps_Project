package com.ahmedabad.mapsproject.presentation.ui.screens.location_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ahmedabad.mapsproject.domain.model.LocationModel
import com.ahmedabad.mapsproject.domain.usecase.AddLocationUseCase
import com.ahmedabad.mapsproject.domain.usecase.DeleteLocationUseCase
import com.ahmedabad.mapsproject.domain.usecase.GetAllLocationsUseCase
import com.ahmedabad.mapsproject.domain.usecase.UpdateLocationUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

@HiltViewModel
class LocationListViewModel @Inject constructor(
    private val getAllLocationsUseCase: GetAllLocationsUseCase,
    private val deleteLocationUseCase: DeleteLocationUseCase,
    private val updateLocationUseCase: UpdateLocationUseCase,
    private val addLocationUseCase: AddLocationUseCase,
) : ViewModel() {

    private val _locations = MutableStateFlow<List<LocationModel>>(emptyList())
    val locations: StateFlow<List<LocationModel>> = _locations.asStateFlow()

    private val _primaryLocation = MutableStateFlow<LocationModel?>(null)
    val primaryLocation: StateFlow<LocationModel?> = _primaryLocation.asStateFlow()

    private val _sortOrder = MutableStateFlow(SortOrder.ASCENDING)
    val sortOrder: StateFlow<SortOrder> = _sortOrder.asStateFlow()

    private val _addLocation = MutableStateFlow(false)
    val addLocation: StateFlow<Boolean> = _addLocation.asStateFlow()

    private val _editLocationId = MutableStateFlow<Int?>(null)
    val editLocationId: StateFlow<Int?> = _editLocationId.asStateFlow()

    init {
        loadLocations()
    }

    private fun loadLocations() {
        viewModelScope.launch {
            getAllLocationsUseCase().collectLatest { locations ->
                val sorted = sortLocations(locations)
                _locations.value = sorted

                val currentPrimary = _primaryLocation.value
                if (currentPrimary == null || !sorted.any { it.id == currentPrimary.id }) {
                    _primaryLocation.value = sorted.firstOrNull { it.isPrimary }
                        ?: sorted.firstOrNull()?.also { loc ->
                            // Only set as primary if there isn't one already
                            if (sorted.none { it.isPrimary }) {
                                setPrimaryLocation(loc)
                            }
                        }
                }
            }
        }
    }

    fun addLocation(location: LocationModel) {
        viewModelScope.launch {
            val isFirstLocation = _locations.value.isEmpty()
            val locationToAdd = if (isFirstLocation) {
                location.copy(isPrimary = true)
            } else {
                location
            }

            addLocationUseCase(locationToAdd)

            if (isFirstLocation) {
                _primaryLocation.value = locationToAdd
            }
        }
    }

    fun setPrimaryLocation(location: LocationModel) {
        viewModelScope.launch {
            val updatedLocations = _locations.value.map { existingLocation ->
                existingLocation.copy(isPrimary = existingLocation.id == location.id)
            }

            updatedLocations.forEach {
                updateLocationUseCase(it)
            }


            _locations.value = sortLocations(updatedLocations)


            _primaryLocation.value = updatedLocations.find { it.isPrimary }
        }
    }

    fun resetAddLocationState() {
        _addLocation.value = false
    }

    fun resetEditLocationState() {
        _editLocationId.value = null
    }

    fun deleteLocation(location: LocationModel) {
        viewModelScope.launch {
            deleteLocationUseCase(location)

            if (location.isPrimary && _locations.value.size > 1) {
                val newPrimary = _locations.value.first { it.id != location.id }
                setPrimaryLocation(newPrimary)
            }
        }
    }

    fun setSortOrder(order: SortOrder) {
        _sortOrder.value = order
        _locations.value = sortLocations(_locations.value)
    }


    private fun sortLocations(locations: List<LocationModel>): List<LocationModel> {
        val primary = _primaryLocation.value

        if (primary == null) {
            return locations
        } else {
            return when (_sortOrder.value) {
                SortOrder.ASCENDING -> locations.sortedBy { loc ->
                    if (loc.id == primary.id) -1.0 else calculateDistance(primary, loc)
                }
                SortOrder.DESCENDING -> locations.sortedByDescending { loc ->
                    if (loc.id == primary.id) Double.MAX_VALUE else calculateDistance(primary, loc)
                }
            }
        }
    }

    fun calculateDistance(from: LocationModel, to: LocationModel): Double {
        val earthRadius = 6371

        val lat1 = Math.toRadians(from.latitude)
        val lon1 = Math.toRadians(from.longitude)
        val lat2 = Math.toRadians(to.latitude)
        val lon2 = Math.toRadians(to.longitude)

        val dLat = lat2 - lat1
        val dLon = lon2 - lon1

        val a = sin(dLat / 2).pow(2) + cos(lat1) * cos(lat2) * sin(dLon / 2).pow(2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))

        return earthRadius * c
    }

    enum class SortOrder {
        ASCENDING, DESCENDING
    }
}
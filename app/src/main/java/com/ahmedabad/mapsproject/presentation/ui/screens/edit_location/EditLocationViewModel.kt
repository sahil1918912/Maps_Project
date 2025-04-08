package com.ahmedabad.mapsproject.presentation.ui.screens.edit_location

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ahmedabad.mapsproject.domain.model.LocationModel
import com.ahmedabad.mapsproject.domain.usecase.GetLocationByIdUseCase
import com.ahmedabad.mapsproject.domain.usecase.UpdateLocationUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditLocationViewModel @Inject constructor(
    private val getLocationByIdUseCase: GetLocationByIdUseCase,
    private val updateLocationUseCase: UpdateLocationUseCase
) : ViewModel() {
    private val _location = MutableStateFlow<LocationModel?>(null)
    val location: StateFlow<LocationModel?> = _location.asStateFlow()

    fun loadLocation(locationId: Int) {
        viewModelScope.launch {
            _location.value = getLocationByIdUseCase(locationId)
        }
    }

    fun updateLocation(location: LocationModel) {
        viewModelScope.launch {
            updateLocationUseCase(location)
            _location.value = location
        }
    }
}
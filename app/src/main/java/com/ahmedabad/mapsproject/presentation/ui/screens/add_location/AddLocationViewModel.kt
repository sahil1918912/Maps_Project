package com.ahmedabad.mapsproject.presentation.ui.screens.add_location

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ahmedabad.mapsproject.domain.model.LocationModel
import com.ahmedabad.mapsproject.domain.usecase.GetPlaceDetailsUseCase
import com.ahmedabad.mapsproject.domain.usecase.SearchPlacesUseCase
import com.ahmedabad.mapsproject.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddLocationViewModel @Inject constructor(
    private val searchPlacesUseCase: SearchPlacesUseCase,
    private val getPlaceDetailsUseCase: GetPlaceDetailsUseCase,
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _searchResults = MutableStateFlow<Resource<List<LocationModel>>>(Resource.Loading())
    val searchResults: StateFlow<Resource<List<LocationModel>>> = _searchResults

    private val _selectedLocation = MutableStateFlow<LocationModel?>(null)
    val selectedLocation: StateFlow<LocationModel?> = _selectedLocation

    private val _saveSuccess = MutableStateFlow(false)
    val saveSuccess: StateFlow<Boolean> = _saveSuccess

    fun initializeWithLocation(location: LocationModel) {
        _selectedLocation.value = location
        _searchQuery.value = location.name
    }

    fun onNewSearchSelection() {
        _selectedLocation.value = null
    }

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    fun searchPlaces(query: String) {
        viewModelScope.launch {
            _searchResults.value = Resource.Loading()
            _searchResults.value = searchPlacesUseCase(query)
        }
    }

    fun onLocationSelected(placeId: String) {
        viewModelScope.launch {
            _selectedLocation.value = null
            when (val result = getPlaceDetailsUseCase(placeId)) {
                is Resource.Success -> {
                    _selectedLocation.value = result.data
                }

                is Resource.Error -> {

                }

                else -> {}
            }
        }
    }
}
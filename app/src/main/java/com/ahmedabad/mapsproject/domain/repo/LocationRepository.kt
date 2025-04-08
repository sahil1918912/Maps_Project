package com.ahmedabad.mapsproject.domain.repo


import com.ahmedabad.mapsproject.domain.model.LocationModel
import com.ahmedabad.mapsproject.util.Resource
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.Flow

interface LocationRepository {
    fun getAllLocations(): Flow<List<LocationModel>>
    fun getAllLocationsOrderedByDistance(): Flow<List<LocationModel>>
    suspend fun addLocation(location: LocationModel)
    suspend fun updateLocation(location: LocationModel)
    suspend fun deleteLocation(location: LocationModel)
    suspend fun getLocationById(id: Int): LocationModel?

    suspend fun searchPlaces(
        query: String,
        types: String? = null,
        location: LatLng? = null,
        radius: Int? = null
    ): Resource<List<LocationModel>>
    suspend fun getPlaceDetails(placeId: String): Resource<LocationModel>
    suspend fun getRoutePath(locations: List<LocationModel>): Resource<String>
}
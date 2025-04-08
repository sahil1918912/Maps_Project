package com.ahmedabad.mapsproject.data.repo

import com.ahmedabad.mapsproject.data.local.LocationDao
import com.ahmedabad.mapsproject.data.local.LocationEntity
import com.ahmedabad.mapsproject.data.remote.DirectionsApi
import com.ahmedabad.mapsproject.data.remote.PlacesApi
import com.ahmedabad.mapsproject.domain.model.LocationModel
import com.ahmedabad.mapsproject.domain.repo.LocationRepository
import com.ahmedabad.mapsproject.util.Resource
import com.ahmedabad.mapsproject.util.toLocationEntity
import com.ahmedabad.mapsproject.util.toLocationModel
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class LocationRepositoryImpl @Inject constructor(
    private val locationDao: LocationDao,
    private val placesApi: PlacesApi,
    private val directionsApi: DirectionsApi,
    private val apiKey: String
) : LocationRepository {

    override fun getAllLocations(): Flow<List<LocationModel>> {
        return locationDao.getAllLocations().map { entities ->
            entities.map { it.toLocationModel() }
        }
    }

    override fun getAllLocationsOrderedByDistance(): Flow<List<LocationModel>> {
        return locationDao.getAllLocationsOrderedByDistance().map { entities ->
            entities.map { it.toLocationModel() }
        }
    }

    override suspend fun addLocation(location: LocationModel) {
        locationDao.insertLocation(location.toLocationEntity())
    }

    override suspend fun updateLocation(location: LocationModel) {
        locationDao.updateLocation(location.toLocationEntity())
    }

    override suspend fun deleteLocation(location: LocationModel) {
        locationDao.deleteLocation(location.toLocationEntity())
    }
    override suspend fun getLocationById(id: Int): LocationModel? {
        return locationDao.getLocationById(id)?.toLocationModel()
    }

    override suspend fun searchPlaces(
        query: String,
        types: String?,
        location: LatLng?,
        radius: Int?
    ): Resource<List<LocationModel>> {
        return try {
            val locationStr = location?.let { "${it.latitude},${it.longitude}" }

            val response = placesApi.getPlacePredictions(
                input = query,
                key = apiKey,
                types = types,
                location = locationStr,
                radius = radius
            )

            when (response.status) {
                "OK" -> Resource.Success(response.predictions.map {
                    LocationModel(
                        name = it.description,
                        address = "",
                        latitude = 0.0,
                        longitude = 0.0,
                        placeId = it.place_id
                    )
                })
                else -> Resource.Error(response.status ?: "Unknown error")
            }
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "Failed to search places")
        }
    }


    override suspend fun getPlaceDetails(placeId: String): Resource<LocationModel> {
        return try {
            val response = placesApi.getPlaceDetails(placeId = placeId, key = apiKey)
            when (response.status) {
                "OK" -> Resource.Success(
                    LocationModel(
                        name = response.result.name,
                        address = response.result.formatted_address,
                        latitude = response.result.geometry.location.lat,
                        longitude = response.result.geometry.location.lng,
                        placeId = placeId
                    )
                )
                else -> Resource.Error(response.status ?: "Unknown error")
            }
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "Failed to get place details")
        }
    }

    override suspend fun getRoutePath(locations: List<LocationModel>): Resource<String> {
        if (locations.size < 2) return Resource.Error("Need at least 2 locations")

        return try {
            val origin = "${locations.first().latitude},${locations.first().longitude}"
            val destination = "${locations.last().latitude},${locations.last().longitude}"
            val waypoints = if (locations.size > 2) {
                locations.subList(1, locations.size - 1)
                    .joinToString("|") { "${it.latitude},${it.longitude}" }
            } else null

            val response = directionsApi.getDirections(
                origin = origin,
                destination = destination,
                waypoints = waypoints,
                apiKey = apiKey
            )

            Resource.Success(response.routes.first().overview_polyline.points)
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "Failed to get directions")
        }
    }
}


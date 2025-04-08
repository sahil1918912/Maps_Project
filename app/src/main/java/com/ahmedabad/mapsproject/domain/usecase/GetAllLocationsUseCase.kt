package com.ahmedabad.mapsproject.domain.usecase

import com.ahmedabad.mapsproject.domain.model.LocationModel
import com.ahmedabad.mapsproject.domain.repo.LocationRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAllLocationsUseCase @Inject constructor(
    private val repository: LocationRepository,
) {
    operator fun invoke(): Flow<List<LocationModel>> {
        return repository.getAllLocations()
    }
}
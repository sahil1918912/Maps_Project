package com.ahmedabad.mapsproject.domain.usecase

import com.ahmedabad.mapsproject.domain.model.LocationModel
import com.ahmedabad.mapsproject.domain.repo.LocationRepository
import javax.inject.Inject

class GetLocationByIdUseCase @Inject constructor(
    private val repository: LocationRepository
) {
    suspend operator fun invoke(id: Int): LocationModel? {
        return repository.getLocationById(id)
    }
}
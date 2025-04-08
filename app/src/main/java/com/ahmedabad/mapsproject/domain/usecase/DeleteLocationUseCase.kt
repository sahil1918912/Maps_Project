package com.ahmedabad.mapsproject.domain.usecase

import com.ahmedabad.mapsproject.domain.model.LocationModel
import com.ahmedabad.mapsproject.domain.repo.LocationRepository
import javax.inject.Inject

class DeleteLocationUseCase @Inject constructor(
    private val repository: LocationRepository,
) {
    suspend operator fun invoke(location: LocationModel) {
        repository.deleteLocation(location)
    }
}

package com.ahmedabad.mapsproject.domain.usecase

import com.ahmedabad.mapsproject.domain.model.LocationModel
import com.ahmedabad.mapsproject.domain.repo.LocationRepository
import com.ahmedabad.mapsproject.util.Resource
import javax.inject.Inject

class GetPlaceDetailsUseCase @Inject constructor(
    private val repository: LocationRepository
) {
    suspend operator fun invoke(placeId: String): Resource<LocationModel> {
        return repository.getPlaceDetails(placeId)
    }
}
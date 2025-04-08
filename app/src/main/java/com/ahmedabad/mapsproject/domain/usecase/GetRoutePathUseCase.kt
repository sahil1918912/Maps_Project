package com.ahmedabad.mapsproject.domain.usecase

import com.ahmedabad.mapsproject.domain.model.LocationModel
import com.ahmedabad.mapsproject.domain.repo.LocationRepository
import com.ahmedabad.mapsproject.util.Resource
import javax.inject.Inject

class GetRoutePathUseCase @Inject constructor(
    private val repository: LocationRepository,
) {
    suspend operator fun invoke(locations: List<LocationModel>): Resource<String> {
        return repository.getRoutePath(locations)
    }
}



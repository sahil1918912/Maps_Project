package com.ahmedabad.mapsproject.di

import com.ahmedabad.mapsproject.domain.repo.LocationRepository
import com.ahmedabad.mapsproject.domain.usecase.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {

    @Provides
    @Singleton
    fun provideGetLocationByIdUseCase(repository: LocationRepository): GetLocationByIdUseCase {
        return GetLocationByIdUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideUpdateLocationUseCase(repository: LocationRepository): UpdateLocationUseCase {
        return UpdateLocationUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideAddLocationUseCase(repository: LocationRepository): AddLocationUseCase {
        return AddLocationUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideDeleteLocationUseCase(repository: LocationRepository): DeleteLocationUseCase {
        return DeleteLocationUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideGetAllLocationsUseCase(repository: LocationRepository): GetAllLocationsUseCase {
        return GetAllLocationsUseCase(repository)
    }
}
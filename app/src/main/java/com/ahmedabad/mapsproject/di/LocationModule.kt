package com.ahmedabad.mapsproject.di

import com.ahmedabad.mapsproject.data.repo.LocationRepositoryImpl
import com.ahmedabad.mapsproject.domain.repo.LocationRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class LocationModule {

    @Binds
    @Singleton
    abstract fun bindLocationRepository(
        impl: LocationRepositoryImpl
    ): LocationRepository
}

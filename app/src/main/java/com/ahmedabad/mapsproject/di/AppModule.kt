package com.ahmedabad.mapsproject.di

import android.content.Context
import com.ahmedabad.mapsproject.data.local.LocationDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideLocationDatabase(@ApplicationContext context: Context): LocationDatabase {
        return LocationDatabase.getDatabase(context)
    }

    @Singleton
    @Provides
    fun provideLocationDao(database: LocationDatabase) = database.locationDao()


}
package com.ahmedabad.mapsproject.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface LocationDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLocation(location: LocationEntity)

    @Update
    suspend fun updateLocation(location: LocationEntity)

    @Delete
    suspend fun deleteLocation(location: LocationEntity)

    @Query("SELECT * FROM locations")
    fun getAllLocations(): Flow<List<LocationEntity>>

    @Query("""
        SELECT * FROM locations ORDER BY 
        CASE WHEN isPrimary THEN 0 ELSE 1 END,
        (latitude - (SELECT latitude FROM locations WHERE isPrimary = 1 LIMIT 1)) * 
        (latitude - (SELECT latitude FROM locations WHERE isPrimary = 1 LIMIT 1)) +
        (longitude - (SELECT longitude FROM locations WHERE isPrimary = 1 LIMIT 1)) * 
        (longitude - (SELECT longitude FROM locations WHERE isPrimary = 1 LIMIT 1))
    """)
    fun getAllLocationsOrderedByDistance(): Flow<List<LocationEntity>>

    @Query("SELECT * FROM locations WHERE id = :id")
    suspend fun getLocationById(id: Int): LocationEntity?
}

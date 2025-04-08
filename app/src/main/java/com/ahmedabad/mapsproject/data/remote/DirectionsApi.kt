package com.ahmedabad.mapsproject.data.remote

import com.ahmedabad.mapsproject.data.remote.dto.DirectionsResponse
import retrofit2.http.GET
import retrofit2.http.Query


interface DirectionsApi {
    @GET("directions/json")
    suspend fun getDirections(
        @Query("origin") origin: String,
        @Query("destination") destination: String,
        @Query("waypoints") waypoints: String? = null,
        @Query("key") apiKey: String
    ): DirectionsResponse
}
package com.ahmedabad.mapsproject.data.remote

import com.ahmedabad.mapsproject.data.remote.dto.PlaceAutocompleteResponse
import com.ahmedabad.mapsproject.data.remote.dto.PlaceDetailsResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface PlacesApi {
    @GET("place/autocomplete/json")
    suspend fun getPlacePredictions(
        @Query("input") input: String,
        @Query("key") key: String,
        @Query("types") types: String? = null,
        @Query("location") location: String? = null,
        @Query("radius") radius: Int? = null
    ): PlaceAutocompleteResponse

    @GET("place/details/json")
    suspend fun getPlaceDetails(
        @Query("place_id") placeId: String,
        @Query("key") key: String
    ): PlaceDetailsResponse
}

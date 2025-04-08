package com.ahmedabad.mapsproject.data.remote.dto

data class PlaceDetailsResponse(
    val result: Result,
    val status: String
) {
    data class Result(
        val name: String,
        val formatted_address: String,
        val geometry: Geometry
    ) {
        data class Geometry(
            val location: Location
        ) {
            data class Location(
                val lat: Double,
                val lng: Double
            )
        }
    }
}
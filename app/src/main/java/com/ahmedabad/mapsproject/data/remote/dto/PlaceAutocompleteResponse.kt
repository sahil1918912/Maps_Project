package com.ahmedabad.mapsproject.data.remote.dto

data class PlaceAutocompleteResponse(
    val predictions: List<Prediction>,
    val status: String
) {
    data class Prediction(
        val description: String,
        val place_id: String
    )
}
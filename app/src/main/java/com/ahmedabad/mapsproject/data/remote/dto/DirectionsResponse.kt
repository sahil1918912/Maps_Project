package com.ahmedabad.mapsproject.data.remote.dto

data class DirectionsResponse(
    val routes: List<Route>,
    val status: String
) {
    data class Route(
        val overview_polyline: Polyline
    ) {
        data class Polyline(
            val points: String
        )
    }
}
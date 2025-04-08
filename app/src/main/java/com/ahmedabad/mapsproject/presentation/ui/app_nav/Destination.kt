package com.ahmedabad.mapsproject.presentation.ui.app_nav

sealed class Destination(val route: String) {

    object LocationList : Destination("location_list")
    object AddLocation : Destination("add_location")
    object EditLocation : Destination("edit_location/{locationId}") {
        fun createRoute(locationId: Int) = "edit_location/$locationId"
    }
    object Map : Destination("map")
}
package com.ahmedabad.mapsproject.util

import com.ahmedabad.mapsproject.data.local.LocationEntity
import com.ahmedabad.mapsproject.domain.model.LocationModel
import com.google.android.gms.maps.model.LatLng

fun LocationEntity.toLocationModel(): LocationModel {
    return LocationModel(
        id = id,
        name = name,
        address = address,
        latitude = latitude,
        longitude = longitude,
        isPrimary = isPrimary
    )
}

fun LocationModel.toLocationEntity(): LocationEntity {
    return LocationEntity(
        id = id,
        name = name,
        address = address,
        latitude = latitude,
        longitude = longitude,
        isPrimary = isPrimary
    )
}

fun String.decodePolyline(): List<LatLng> {
    val poly = mutableListOf<LatLng>()
    var index = 0
    val len = this.length
    var lat = 0
    var lng = 0

    while (index < len) {
        var b: Int
        var shift = 0
        var result = 0
        do {
            b = this[index++].code - 63
            result = result or (b and 0x1f shl shift)
            shift += 5
        } while (b >= 0x20)
        val dlat = if (result and 1 != 0) (result shr 1).inv() else result shr 1
        lat += dlat

        shift = 0
        result = 0
        do {
            b = this[index++].code - 63
            result = result or (b and 0x1f shl shift)
            shift += 5
        } while (b >= 0x20)
        val dlng = if (result and 1 != 0) (result shr 1).inv() else result shr 1
        lng += dlng

        val latLng = LatLng(lat / 1E5, lng / 1E5)
        poly.add(latLng)
    }

    return poly
}
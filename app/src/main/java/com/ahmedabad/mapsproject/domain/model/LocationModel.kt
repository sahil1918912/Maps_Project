package com.ahmedabad.mapsproject.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
data class LocationModel(
    val id: Int = 0,
    val name: String,
    val address: String,
    val latitude: Double,
    val longitude: Double,
    val placeId: String? = null,
    val isPrimary: Boolean = false
) : Parcelable
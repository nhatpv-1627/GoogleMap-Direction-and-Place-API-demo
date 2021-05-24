package com.example.googlemapdemo.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class LatLngData(
    @Expose
    @SerializedName("lat")
    val latitude: Double,

    @Expose
    @SerializedName("lng")
    val longitude: Double,
)

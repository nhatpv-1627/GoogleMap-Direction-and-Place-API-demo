package com.example.googlemapdemo.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.lang.StringBuilder

data class WayPointData(
    @Expose
    @SerializedName("geocoder_status")
    val status: String?,

    @Expose
    @SerializedName("place_id")
    val placeId: String,

    @Expose
    @SerializedName("types")
    val type: List<StringBuilder>?
)

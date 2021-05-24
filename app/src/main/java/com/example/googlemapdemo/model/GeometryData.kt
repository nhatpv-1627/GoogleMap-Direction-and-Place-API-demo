package com.example.googlemapdemo.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class GeometryData(
    @Expose
    @SerializedName("location")
    val location: LatLngData?,

    @Expose
    @SerializedName("viewport")
    val viewPort: ViewPortData
) {
    data class ViewPortData(
        @Expose
        @SerializedName("northeast")
        val northeast: LatLngData,

        @Expose
        @SerializedName("southwest")
        val southwest: LatLngData
    )
}

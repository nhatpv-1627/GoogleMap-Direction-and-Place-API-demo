package com.example.googlemapdemo.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class RouteData(
    @Expose
    @SerializedName("summary")
    val summary: String?,

    @Expose
    @SerializedName("legs")
    val legs: List<LegData>?,

    @Expose
    @SerializedName("overview_polyline")
    val overviewPolyline: PolylinePoints?
) {
    data class PolylinePoints(
        @Expose
        @SerializedName("points")
        val encodedPolyline: String?
    )
}

package com.example.googlemapdemo.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class DirectionResponse(
    @Expose
    @SerializedName("geocoded_waypoints")
    val geocodedWaypoints: List<WayPointData>?,

    @Expose
    @SerializedName("routes")
    val routes: List<RouteData>?,
)

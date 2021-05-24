package com.example.googlemapdemo.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class LegData(
    @Expose
    @SerializedName("distance")
    val distance: CommonData?,

    @Expose
    @SerializedName("duration")
    val duration: CommonData?,

    @Expose
    @SerializedName("end_address")
    val endAddressName: String?,

    @Expose
    @SerializedName("start_address")
    val startAddressName: String?,

    @Expose
    @SerializedName("start_location")
    val startLocation: LatLngData?,

    @Expose
    @SerializedName("end_location")
    val endLocation: LatLngData?,

    @Expose
    @SerializedName("steps")
    val steps: List<StepData>?

)

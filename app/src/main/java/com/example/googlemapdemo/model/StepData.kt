package com.example.googlemapdemo.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class StepData (
    @Expose
    @SerializedName("distance")
    val distance: CommonData?,

    @Expose
    @SerializedName("duration")
    val duration: CommonData?,

    @Expose
    @SerializedName("start_location")
    val startLocation: LatLngData?,

    @Expose
    @SerializedName("end_location")
    val endLocation: LatLngData?,

    @Expose
    @SerializedName("html_instructions")
    val htmlInstructions: String?,

    @Expose
    @SerializedName("maneuver")
    val maneuver: String?,
)

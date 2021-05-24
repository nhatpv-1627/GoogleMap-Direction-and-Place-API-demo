package com.example.googlemapdemo.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class PlaceData(
    @Expose
    @SerializedName("formatted_address")
    val formattedAddress: String?,

    @Expose
    @SerializedName("geometry")
    val geometry: GeometryData?,

    @Expose
    @SerializedName("name")
    val name: String?,

    @Expose
    @SerializedName("place_id")
    val placeId: String?

)

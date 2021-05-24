package com.example.googlemapdemo.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class CommonData(
    @Expose
    @SerializedName("text")
    val text: String?,

    @Expose
    @SerializedName("value")
    val value: Int?
)
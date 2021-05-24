package com.example.googlemapdemo.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class SearchResponse(
    @Expose
    @SerializedName("next_page_token")
    val nextPageToken: String?,

    @Expose
    @SerializedName("results")
    val results: List<PlaceData>?,

    @Expose
    @SerializedName("status")
    val status: String?
)

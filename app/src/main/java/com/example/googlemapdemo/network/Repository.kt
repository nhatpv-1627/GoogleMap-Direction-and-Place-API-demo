package com.example.googlemapdemo.network

import com.example.googlemapdemo.Utils.ApiGenerator
import com.example.googlemapdemo.model.DirectionResponse
import com.example.googlemapdemo.model.SearchResponse
import com.google.android.gms.maps.model.LatLng

class Repository {
    private val mapApi = ApiGenerator().getMapApiInstance()

    suspend fun getDirectionFromTo(apiKey: String, from: LatLng, to: LatLng): DirectionResponse {
        return mapApi.getDirectionFromTo(
            apiKey,
            "${from.latitude},${from.longitude}",
            "${to.latitude},${to.longitude}"
        )
    }

    suspend fun searchPlace(apiKey: String, query: String): SearchResponse {
        return mapApi.searchPlace(apiKey, query)
    }
}

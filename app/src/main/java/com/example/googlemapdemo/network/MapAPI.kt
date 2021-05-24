package com.example.googlemapdemo.network

import com.example.googlemapdemo.model.DirectionResponse
import com.example.googlemapdemo.model.SearchResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface MapAPI {
    @GET("directions/json?mode=driving&alternatives=true")
    suspend fun getDirectionFromTo(
        @Query("key") apiKey: String,
        @Query("origin") from: String,
        @Query("destination") to: String
    ): DirectionResponse

    @GET("place/textsearch/json")
    suspend fun searchPlace(
        @Query("key") apiKey: String,
        @Query("query") query: String
    ): SearchResponse
}

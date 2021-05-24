package com.example.googlemapdemo.Utils

import com.example.googlemapdemo.BuildConfig
import com.example.googlemapdemo.network.MapAPI
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.concurrent.TimeUnit

class ApiGenerator {
    private var mapApi: MapAPI? = null

    fun getMapApiInstance(): MapAPI {
        if (mapApi == null)
            mapApi = provideApi(MapAPI::class.java)

        return mapApi!!
    }

    private fun <T> provideApi(serviceClass: Class<T>): T {
        val okHttpBuilder = OkHttpClient().newBuilder()
        okHttpBuilder.addInterceptor(buildHttpLog())
        okHttpBuilder.connectTimeout(CONNECT_TIMEOUT, TimeUnit.MILLISECONDS)
            .readTimeout(READ_TIMEOUT, TimeUnit.MILLISECONDS)
            .writeTimeout(WRITE_TIMEOUT, TimeUnit.MILLISECONDS)
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(buildGson()))
            .client(okHttpBuilder.build())
            .build()
        return retrofit.create(serviceClass)
    }

    private fun buildHttpLog(): HttpLoggingInterceptor {
        val logLevel =
            if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE
        return HttpLoggingInterceptor().setLevel(logLevel)

    }

    private fun buildGson(): Gson {
        return GsonBuilder().excludeFieldsWithoutExposeAnnotation().create()
    }

    companion object {
        private const val CONNECT_TIMEOUT = 60000L
        private const val READ_TIMEOUT = 20000L
        private const val WRITE_TIMEOUT = 30000L
        private const val BASE_URL = "https://maps.googleapis.com/maps/api/"
    }
}

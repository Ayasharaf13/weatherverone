package com.example.weathermate.network
/*
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitHelper {
    private const val BASE_URL = "https://api.openweathermap.org/data/2.5/"
    val retrofit = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl(BASE_URL)
        .build()
}
object API{
    val retrofitService : WeatherService by lazy {
        RetrofitHelper.retrofit.create(WeatherService::class.java)
    }
}

 */





//import com.example.weathermate.database.GsonTypeAdapterFactory

import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitHelper {
    private const val BASE_URL = "https://api.openweathermap.org/data/2.5/"
    val retrofit = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create(createCustomGson()))
        .baseUrl(BASE_URL)
        .build()

    private fun createCustomGson() = GsonBuilder()
       // .registerTypeAdapterFactory(GsonTypeAdapterFactory())
        .create()
}

object API {
    val retrofitService: WeatherService by lazy {
        RetrofitHelper.retrofit.create(WeatherService::class.java)
    }
}



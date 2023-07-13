package com.example.weathermate.network

import com.example.weathermate.weather_data_fetcher.WeatherResponse
import retrofit2.Response

interface RemoteSource {
    suspend fun getWeatherData(  latitude: Double,
                                 longitude: Double,
                                 units: String = "default",
                                 lang: String = "en",
                                 exclude: String = "minutely",

                               ): Response<WeatherResponse>
}
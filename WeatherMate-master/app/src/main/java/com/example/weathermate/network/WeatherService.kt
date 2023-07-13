package com.example.weathermate.network


import com.example.weathermate.weather_data_fetcher.WeatherResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherService {
    @GET("onecall")
    suspend fun getWeatherData(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("exclude") exclude: String = "minutely",
        @Query("appid") appid: String = "209dfe26290f0980900f7978070ccfe2",
        @Query("units") units: String = "default",
        @Query("lang") lang: String = "en"
    ): Response<WeatherResponse>
}






/*
interface WeatherService {
    @GET("onecall")
    suspend fun getWeatherData(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("exclude") exclude: String = "minutely",
        @Query("appid") appid: String = "209dfe26290f0980900f7978070ccfe2",
        @Query("units") units: String = "default",
        @Query("lang") lang: String = "en"
    ): OneCallResponse
}

 */


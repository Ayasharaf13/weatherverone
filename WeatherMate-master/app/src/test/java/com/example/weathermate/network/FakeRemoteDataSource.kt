package com.example.weathermate.network

import com.example.weathermate.weather_data_fetcher.WeatherResponse
import retrofit2.Response



/*

class FakeRemoteDataSource : RemoteSource {
    override suspend fun getWeatherData(
        latitude: Double,
        longitude: Double,
        units: String,
        lang: String,
        exclude: String
    ): Response<WeatherResponse> {
        val weatherResponse = WeatherResponse(
            id = 1,
            cityLatitude = 0.0,
            cityLongitude = 0.0,
            locationName = "",
            timezone_offset = 0,
            currentForecast = null,
            hourlyForecast = emptyList(),
            dailyForecast = emptyList(),
            alerts = emptyList()
        )

        // Return a successful response with the weatherResponse
        return Response.success(weatherResponse)
    }
}

 */



class FakeRemoteDataSource : RemoteSource {
    override suspend fun getWeatherData(
        latitude: Double,
        longitude: Double,
        units: String,
        lang: String,
        exclude: String
    ): Response<WeatherResponse> {
        return Response.success(
            WeatherResponse(
                cityLatitude = latitude,
                cityLongitude = longitude,
            )
        )
    }
}


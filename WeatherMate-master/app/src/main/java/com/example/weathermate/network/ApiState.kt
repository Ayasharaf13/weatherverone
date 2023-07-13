package com.example.weathermate.network

import com.example.weathermate.weather_data_fetcher.FavoriteWeatherResponse
import com.example.weathermate.weather_data_fetcher.WeatherResponse
/*
sealed class ApiState {
    data class Success(val data: WeatherResponse) : ApiState()
    data class Failure(val error: Throwable?) : ApiState()
    object Loading : ApiState()
}

 */





sealed class ApiState {
    class Success(
        val data: WeatherResponse,
        favoriteWeatherResponse: FavoriteWeatherResponse? = null
    ) : ApiState()

    class Failure(val msg: Throwable) : ApiState()
    object Loading : ApiState()
}








/*
sealed class ApiState<out T> {
    data class Success<out T>(val data: T) : ApiState<T>()
    data class Error(val message: String) : ApiState<Nothing>()
    object Loading : ApiState<Nothing>()
}

 */
/*

sealed class ApiState <out T> {
    class Success<T>(val data:T): ApiState <T>()
    class Failure(val msg:Throwable): ApiState <Nothing>()
    object Loading: ApiState <Nothing>()
}

 */



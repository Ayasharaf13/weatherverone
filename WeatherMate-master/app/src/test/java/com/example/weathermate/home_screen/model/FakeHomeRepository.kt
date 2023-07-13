package com.example.weathermate.home_screen.model

import androidx.work.ListenableWorker.Result.success
import com.airbnb.lottie.utils.Logger.error
import java.io.IOException
import kotlin.Result.Companion.success


import com.example.weathermate.database.LocalSource
import com.example.weathermate.network.RemoteSource
import com.example.weathermate.weather_data_fetcher.WeatherResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import retrofit2.Response

class FakeHomeRepository(
    private val localSource: LocalSource,
    private val remoteSource: RemoteSource
)  : HomeRepositoryInterface {


    override fun getWeatherData(
        latitude: Double,
        longitude: Double,
        units: String,
        lang: String
    ): Flow<Response<WeatherResponse>> {
        return flow {
            emit(remoteSource.getWeatherData(latitude, longitude))
        }
    }

    override fun getWeatherDetails(): Flow<List<WeatherResponse>> {
        return flow {
            emitAll(localSource.getWeatherDetails())
        }
    }
}










/*
class FakeHomeRepository(
    private val localSource: LocalSource,
    private val remoteSource: RemoteSource
) : HomeRepositoryInterface {

    override fun getWeatherData(
        latitude: Double,
        longitude: Double,
        units: String,
        lang: String
    ): Flow<Response<WeatherResponse>> {
        return flow {
            try {
                val response = remoteSource.getWeatherData(latitude, longitude)
                emit(Response.success(response))
            } catch (e: IOException) {
                emit(Response.error(e.message ?: "Network error", null))
            }
        }
    }

    override fun getWeatherDetails(): Flow<List<WeatherResponse>> {
        return localSource.getWeatherDetails()
    }
}

 */


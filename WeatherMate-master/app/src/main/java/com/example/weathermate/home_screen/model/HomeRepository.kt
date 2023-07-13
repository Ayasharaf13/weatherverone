package com.example.weathermate.home_screen.model

import com.example.weathermate.database.LocalSource
import com.example.weathermate.network.RemoteSource
import com.example.weathermate.weather_data_fetcher.WeatherResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import retrofit2.Response
/*
class HomeRepository private constructor(
    private val remoteSource: RemoteSource,
    private val localSource: LocalSource
) : HomeRepositoryInterface {

    companion object {
        @Volatile
        private var instance: HomeRepository? = null

        fun getInstance(remoteSource: RemoteSource, localSource: LocalSource): HomeRepository =
            instance ?: synchronized(this) {
                instance ?: HomeRepository(remoteSource, localSource).also { instance = it }
            }
    }

    override fun getWeatherData(
        latitude: Double,
        longitude: Double,
        units: String,
        lang: String
    ): Flow<Response<WeatherResponse>> =
        flow {
            emit(remoteSource.getWeatherData(latitude, longitude, units, lang))
        }

    override fun getLocalWeatherDetails(): Flow<List<WeatherResponse>> =
        localSource.getLocalWeatherDetails()

    override suspend fun insertWeatherDetails(weatherResponse: WeatherResponse) {
        localSource.insertWeatherDetails(weatherResponse)
    }
}

 */
class HomeRepository(
    private val remoteSource: RemoteSource,
    private val localSource: LocalSource
    ) : HomeRepositoryInterface {
    companion object {
        @Volatile
        private var instance: HomeRepository? = null

        @Synchronized
        fun getInstance(remoteSource: RemoteSource, localSource: LocalSource): HomeRepository {
            if (instance == null) {
                instance = HomeRepository(remoteSource, localSource)
            }
            return instance  as HomeRepository
        }
    }




    override fun getWeatherData(
        latitude: Double,
        longitude: Double,
        units: String,
        lang: String
    ): Flow<Response<WeatherResponse>> {
        return flow {
            emit(
                remoteSource.getWeatherData(
                  latitude,
                    longitude,
                     units,
                     lang
                )
            )
        }
    }
//change All
    override fun getWeatherDetails(): Flow<List<WeatherResponse>> {
        return flow{
            emitAll(localSource.getWeatherDetails())
        }
    }
















/*
    override suspend fun insertWeatherDetails(weatherResponse: WeatherResponse) {

    }

 */
/*
    override suspend fun insertWeatherDetails(weatherResponse: WeatherResponse) {
        concreteLocalSource.insertWeatherDetails(weatherResponse)
    }

 */

    /*override suspend fun updateWeatherDetails(weatherResponse: WeatherResponse): Int {
        return concreteLocalSource.updateWeatherDetails(weatherResponse)
    }*/
}
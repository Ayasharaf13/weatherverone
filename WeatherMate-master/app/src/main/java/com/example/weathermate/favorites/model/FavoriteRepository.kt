package com.example.weathermate.favorites.model

import com.example.weathermate.database.LocalSource
import com.example.weathermate.network.RemoteSource
import com.example.weathermate.weather_data_fetcher.FavoriteWeatherResponse

import com.example.weathermate.weather_data_fetcher.WeatherResponse

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import retrofit2.Response

class FavoriteRepository(
    private val remoteSource: RemoteSource,
    private val localSource: LocalSource
) : FavoriteRepositoryInterface {

    companion object {
        @Volatile
        private var instance: FavoriteRepository? = null

        fun getInstance(remoteSource: RemoteSource, localSource: LocalSource): FavoriteRepository {
            if (instance == null) {
                instance = FavoriteRepository(remoteSource, localSource)
            }
            return instance as FavoriteRepository
        }
    }



    override fun getWeatherData(
        latitude: Double,
        longitude: Double,
        units: String,
        lang: String,
        exclude: String
    ): Flow<Response<WeatherResponse>> {
        return flow {
            emit(
                remoteSource.getWeatherData(
                    latitude,
                    longitude,
                    units,
                    lang,
                    exclude
                )
            )
        }
    }

    override fun getLocalFavDetails(): Flow<List<FavoriteWeatherResponse>> {
        return flow {
            emitAll(localSource.getLocalFavDetails())
        }
    }



    override suspend fun insertNewFavorite(favoriteWeatherResponse: FavoriteWeatherResponse) {
        localSource.insertNewFavorite(favoriteWeatherResponse)
    }

    override suspend fun deleteFavorite(favoriteWeatherResponse: FavoriteWeatherResponse) {
        localSource.deleteFavorite(favoriteWeatherResponse)
    }
}









/*
class FavoriteRepository(
    private val concreteRemoteSource: RemoteSource,
    private val concreteLocalSource: LocalSource
) : FavoriteRepositoryInterface {

    companion object {
        private var INSTANCE: FavoriteRepository? = null
        fun getInstance(
            concreteRemoteSource: RemoteSource,
            concreteLocalSource: LocalSource
        ): FavoriteRepository {
            return INSTANCE ?: synchronized(this) {
                val instance = FavoriteRepository(concreteRemoteSource, concreteLocalSource)
                INSTANCE = instance
                instance
            }
        }
    }

    override fun getWeatherData(
        latitude: Double,
        longitude: Double,
        units: String,
        lang: String,
        exclude: String
    ): Flow<Response<OneCallResponse>> {
        return flow {
            emit(
                concreteRemoteSource.getWeatherData(
                    latitude,
                    longitude,
                    units,
                    lang,
                    exclude
                )
            )
        }
    }
    // change emitAll
    override fun getLocalFavDetails(): Flow<List<FavoriteWeatherResponse>> {
        return flow {
            emitAll(concreteLocalSource.getLocalFavDetails())
        }
    }

    override suspend fun insertNewFavorite(favoriteWeatherResponse: FavoriteWeatherResponse) {
        concreteLocalSource.insertNewFavorite(favoriteWeatherResponse)
    }

    override suspend fun deleteFavorite(favoriteWeatherResponse: FavoriteWeatherResponse) {
        concreteLocalSource.deleteFavorite(favoriteWeatherResponse)
    }
}

 */



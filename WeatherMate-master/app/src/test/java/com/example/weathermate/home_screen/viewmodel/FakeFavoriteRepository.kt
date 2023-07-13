package com.example.weathermate.home_screen.viewmodel



import com.example.weathermate.database.LocalSource
import com.example.weathermate.favorites.model.FavoriteRepositoryInterface
import com.example.weathermate.network.RemoteSource
import com.example.weathermate.weather_data_fetcher.FavoriteWeatherResponse
import com.example.weathermate.weather_data_fetcher.WeatherResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.Response

class FakeFavoriteRepository(
    private val localSource: LocalSource,
    private val remoteSource: RemoteSource
) : FavoriteRepositoryInterface {

    private val weatherData = mutableListOf<WeatherResponse>()
    private val localFavDetails = mutableListOf<FavoriteWeatherResponse>()

    override fun getWeatherData(
        latitude: Double,
        longitude: Double,
        units: String,
        lang: String,
        exclude: String
    ): Flow<Response<WeatherResponse>> {
        return flow {
            if (weatherData.isNotEmpty()) {
                emit(Response.success(weatherData.first()))
            } else {
                emit(Response.error(404, null))
            }
        }
    }

    override fun getLocalFavDetails(): Flow<List<FavoriteWeatherResponse>> {
        return flow {
            emit(localFavDetails)
        }
    }

    override suspend fun insertNewFavorite(favoriteWeatherResponse: FavoriteWeatherResponse) {
        localFavDetails.add(favoriteWeatherResponse)
    }

    override suspend fun deleteFavorite(favoriteWeatherResponse: FavoriteWeatherResponse) {
        localFavDetails.remove(favoriteWeatherResponse)
    }

    // Additional methods for setting fake data

    fun setWeatherData(weatherResponse: WeatherResponse) {
        weatherData.clear()
        weatherData.add(weatherResponse)
    }

    fun setLocalFavDetails(favoriteWeatherResponses: List<FavoriteWeatherResponse>) {
        localFavDetails.clear()
        localFavDetails.addAll(favoriteWeatherResponses)
    }
}

package com.example.weathermate.database

import com.example.weathermate.weather_data_fetcher.FavoriteWeatherResponse
import com.example.weathermate.weather_data_fetcher.WeatherResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow


class FakeLocalDataSource(
    var favoriteWeatherResponseList: MutableList<FavoriteWeatherResponse> =  mutableListOf(),

    var weatherResponseList: MutableList<WeatherResponse> = mutableListOf<WeatherResponse>()

) : LocalSource {

    override fun getWeatherDetails(): Flow<List<WeatherResponse>> {
      //  return weatherResponseList
        return flow {
            emit(weatherResponseList)
        }
    }








    override fun getLocalFavDetails(): Flow<List<FavoriteWeatherResponse>> {

        val favoriteWeatherResponseList = weatherResponseList.map { weatherResponse ->
            // Convert each WeatherResponse object to FavoriteWeatherResponse
            // and add it to the list
            FavoriteWeatherResponse(/* conversion logic here */)
        }
        return flow {
            emit(favoriteWeatherResponseList)
        }


    }





    override suspend fun insertNewFavorite(favoriteWeatherResponse: FavoriteWeatherResponse) {
      //   var favoriteWeatherResponseList: MutableList<FavoriteWeatherResponse> = mutableListOf<FavoriteWeatherResponse>()
        favoriteWeatherResponseList.add(favoriteWeatherResponse)




    }

    override suspend fun deleteFavorite(favoriteWeatherResponse: FavoriteWeatherResponse) {
      //  var favoriteWeatherResponseList: MutableList<FavoriteWeatherResponse> = mutableListOf<FavoriteWeatherResponse>()
        favoriteWeatherResponseList.remove(favoriteWeatherResponse)

    }

    override fun getAllAlerts(): Flow<List<RoomAlertPojo>> {
        TODO("Not yet implemented")
    }

    override suspend fun insertAlert(alert: RoomAlertPojo) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteAlert(alert: RoomAlertPojo) {
        TODO("Not yet implemented")
    }

/*
    override suspend fun insertWeatherDetails(weatherResponse: WeatherResponse) {
        weatherResponseList.add(weatherResponse)
    }

 */


/*
    override fun getLocalFavDetails(): Flow<List<FavoriteWeatherResponse>> {
        return flow {
            emit(weatherResponseList)
        }
    }

 */

}
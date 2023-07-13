package com.example.weathermate.database


import com.example.weathermate.weather_data_fetcher.FavoriteWeatherResponse
import com.example.weathermate.weather_data_fetcher.WeatherResponse
import kotlinx.coroutines.flow.Flow

interface LocalSource {

   fun getWeatherDetails() :Flow <List<WeatherResponse>>




     fun getLocalFavDetails() :Flow <List<FavoriteWeatherResponse>>
    suspend fun insertNewFavorite(favoriteWeatherResponse: FavoriteWeatherResponse)
    suspend fun deleteFavorite(favoriteWeatherResponse: FavoriteWeatherResponse)
    // Alert

 fun getAllAlerts(): Flow<List<RoomAlertPojo>>
 suspend fun  insertAlert(alert:RoomAlertPojo)
 suspend  fun deleteAlert(alert:RoomAlertPojo)




}





/*
interface LocalSource {

 //  fun getLocalWeatherDetails() : List<WeatherResponse>
 fun getLocalWeatherDetails() :Flow <List<OneCallResponse>>
 suspend fun insertWeatherDetails(oneCallResponse: OneCallResponse)


 // fun getLocalFavDetails() : List<FavoriteWeatherResponse>

 fun getLocalFavDetails() :Flow <List<FavoriteWeatherResponse>>
 suspend fun insertNewFavorite(favoriteWeatherResponse: FavoriteWeatherResponse)
 suspend fun deleteFavorite(favoriteWeatherResponse: FavoriteWeatherResponse)
 // Alert

 fun getAllAlerts(): Flow<List<RoomAlertPojo>>
 suspend fun  insertAlert(alert:RoomAlertPojo)
 suspend  fun deleteAlert(alert:RoomAlertPojo)




}

 */

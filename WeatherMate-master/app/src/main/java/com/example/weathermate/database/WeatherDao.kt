package com.example.weathermate.database

import androidx.room.*
import com.example.weathermate.weather_data_fetcher.FavoriteWeatherResponse
import com.example.weathermate.weather_data_fetcher.WeatherResponse
import kotlinx.coroutines.flow.Flow

@Dao
interface WeatherDao {

    @Query("SELECT * FROM weather")
  //  fun getLocalWeatherDetails() : List<WeatherResponse>
        fun getWeatherDetails(): Flow<List<WeatherResponse>>



/*
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWeatherDetails(weatherResponse: WeatherResponse)

 */



    @Query("SELECT * FROM favorites")
  //  fun getLocalFavDetails() : List<FavoriteWeatherResponse>
    fun getLocalFavDetails() :Flow <List<FavoriteWeatherResponse>>

    @Insert
    suspend fun insertNewFavorite(favoriteWeatherResponse: FavoriteWeatherResponse)

    @Delete
    suspend fun deleteFavorite(favoriteWeatherResponse: FavoriteWeatherResponse)

    @Query("Select * from AlertTable")
    fun  getAllAlerts(): Flow<List<RoomAlertPojo>>
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAlert(alert:RoomAlertPojo)
    @Delete
    suspend fun deleteAlert(alert:RoomAlertPojo)




}



/*

@Dao
interface WeatherDao {

    @Query("SELECT * FROM weather")
//  fun getLocalWeatherDetails() : List<WeatherResponse>
    fun getLocalWeatherDetails(): Flow<List<OneCallResponse>>


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWeatherDetails(oneCallResponse: OneCallResponse )


    @Query("SELECT * FROM favorites")
//  fun getLocalFavDetails() : List<FavoriteWeatherResponse>
    fun getLocalFavDetails(): Flow<List<FavoriteWeatherResponse>>

    @Insert
    suspend fun insertNewFavorite(favoriteWeatherResponse: FavoriteWeatherResponse)

    @Delete
    suspend fun deleteFavorite(favoriteWeatherResponse: FavoriteWeatherResponse)

    @Query("Select * from AlertTable")
    fun getAllAlerts(): Flow<List<RoomAlertPojo>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAlert(alert: RoomAlertPojo)

    @Delete
    suspend fun deleteAlert(alert: RoomAlertPojo)

}

 */
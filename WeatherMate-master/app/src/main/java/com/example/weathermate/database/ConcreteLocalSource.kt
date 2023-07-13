package com.example.weathermate.database

import android.content.Context
import com.example.weathermate.weather_data_fetcher.FavoriteWeatherResponse
import com.example.weathermate.weather_data_fetcher.WeatherResponse
import kotlinx.coroutines.flow.Flow


class ConcreteLocalSource(private val weatherDao: WeatherDao) : LocalSource {



    override suspend fun insertNewFavorite(favoriteWeatherResponse: FavoriteWeatherResponse) {
        weatherDao.insertNewFavorite(favoriteWeatherResponse)
    }

    override suspend fun deleteFavorite(favoriteWeatherResponse: FavoriteWeatherResponse) {
        weatherDao.deleteFavorite(favoriteWeatherResponse)
    }

    override fun getAllAlerts(): Flow<List<RoomAlertPojo>> = weatherDao.getAllAlerts()

    override suspend fun insertAlert(alert: RoomAlertPojo) {
        weatherDao.insertAlert(alert)
    }

    override suspend fun deleteAlert(alert: RoomAlertPojo) {
        weatherDao.deleteAlert(alert)
    }

    constructor(context: Context) : this(WeatherDB.getInstance(context).getWeatherDao())

    override fun getWeatherDetails(): Flow<List<WeatherResponse>> = weatherDao.getWeatherDetails()

    override fun getLocalFavDetails(): Flow<List<FavoriteWeatherResponse>> = weatherDao.getLocalFavDetails()
}



/*

class ConcreteLocalSource(private val weatherDao: WeatherDao): LocalSource {

   constructor(context: Context) : this(WeatherDB.getInstance(context).getWeatherDao())

    override fun getWeatherDetails(): Flow<List<WeatherResponse>> {
        return weatherDao.getWeatherDetails()
    }

    override fun getLocalFavDetails(): Flow<List<FavoriteWeatherResponse>> {
      return weatherDao.getLocalFavDetails()
    }



    override suspend fun insertNewFavorite(favoriteWeatherResponse: FavoriteWeatherResponse) {
        weatherDao.insertNewFavorite(favoriteWeatherResponse)
    }

    override suspend fun deleteFavorite(favoriteWeatherResponse: FavoriteWeatherResponse) {
        weatherDao.deleteFavorite(favoriteWeatherResponse)
    }

    override fun getAllAlerts(): Flow<List<RoomAlertPojo>> {
        return weatherDao.getAllAlerts()
    }

    override suspend fun insertAlert(alert: RoomAlertPojo) {
        weatherDao.insertAlert(alert)
    }

    override suspend fun deleteAlert(alert: RoomAlertPojo) {
        weatherDao.deleteAlert(alert)
    }


    */

/*
    override fun getLocalWeatherDetails(): List<WeatherResponse>{
         return weatherDao.getLocalWeatherDetails()
     }

 */
 

/*
    fun getWeatherDetails():Flow <List<WeatherResponse>>{
        return weatherDao.getWeatherDetails()
    }

 */



/*
    override suspend fun insertWeatherDetails(weatherResponse: WeatherResponse) {
        weatherDao.insertWeatherDetails(weatherResponse)
    }

 */




/*
    override fun getLocalFavDetails(): List<FavoriteWeatherResponse> {
        return weatherDao.getLocalFavDetails()
    }

 */

// delete

    /*
    override fun getLocalFavDetails(): Flow<List<FavoriteWeatherResponse>> {
        return weatherDao.getLocalFavDetails()
    }

     */














/*

class ConcreteLocalSource(private val weatherDao: WeatherDao): LocalSource {

    /*
     override fun getLocalWeatherDetails(): List<WeatherResponse>{
         return weatherDao.getLocalWeatherDetails()
     }

     */

    override fun getLocalWeatherDetails():Flow <List<OneCallResponse>>{
        return weatherDao.getLocalWeatherDetails()
    }

    override suspend fun insertWeatherDetails(oneCallResponse: OneCallResponse) {
        weatherDao.insertWeatherDetails(oneCallResponse)
    }


/*
    override fun getLocalFavDetails(): List<FavoriteWeatherResponse> {
        return weatherDao.getLocalFavDetails()
    }

 */

    // delete
    override fun getLocalFavDetails(): Flow<List<FavoriteWeatherResponse>> {
        return weatherDao.getLocalFavDetails()
    }

    override suspend fun insertNewFavorite(favoriteWeatherResponse: FavoriteWeatherResponse) {
        weatherDao.insertNewFavorite(favoriteWeatherResponse)
    }

    override suspend fun deleteFavorite(favoriteWeatherResponse: FavoriteWeatherResponse) {
        weatherDao.deleteFavorite(favoriteWeatherResponse)
    }

    override fun getAllAlerts(): Flow<List<RoomAlertPojo>> {
        return weatherDao.getAllAlerts()
    }

    override suspend fun insertAlert(alert: RoomAlertPojo) {
        weatherDao.insertAlert(alert)
    }

    override suspend fun deleteAlert(alert: RoomAlertPojo) {
        weatherDao.deleteAlert(alert)
    }




}

 */
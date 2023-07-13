package com.example.weathermate.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.weathermate.weather_data_fetcher.FavoriteWeatherResponse


import com.example.weathermate.weather_data_fetcher.WeatherResponse
/*
@Database(entities = [WeatherResponse::class,FavoriteWeatherResponse::class], version = 1)
abstract class WeatherDB : RoomDatabase(){

    companion object{
        private var INSTANCE : WeatherDB? = null
        fun getInstance(context: Context) : WeatherDB{
            return INSTANCE ?: synchronized(this){
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    WeatherDB::class.java,
                    "weather_base"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }


    abstract fun getWeatherDao() : WeatherDao
}

 */



//2

@Database(entities = [WeatherResponse::class ,FavoriteWeatherResponse::class,RoomAlertPojo::class], version = 4, exportSchema = false)
abstract class WeatherDB : RoomDatabase() {

    abstract fun getWeatherDao(): WeatherDao

    companion object {
        @Volatile
        private var INSTANCE: WeatherDB? = null


        @Synchronized
        fun getInstance(context: Context): WeatherDB {
            if (INSTANCE == null) {
                INSTANCE = Room.databaseBuilder(
                    context.applicationContext,
                    WeatherDB::class.java,
                    "weather_base"


                )
                 .fallbackToDestructiveMigration()
                    .build()
            }
            return INSTANCE as WeatherDB
        }
    }
}


/*
@Database(entities = [WeatherResponse::class, FavoriteWeatherResponse::class, RoomAlertPojo::class], version = 2, exportSchema = false)
abstract class WeatherDB : RoomDatabase() {

    abstract fun getWeatherDao(): WeatherDao

    companion object {
        @Volatile
        private var INSTANCE: WeatherDB? = null

        @Synchronized
        fun getInstance(context: Context): WeatherDB {
            if (INSTANCE == null) {
                INSTANCE = Room.databaseBuilder(
                    context.applicationContext,
                    WeatherDB::class.java,
                    "weather_base"
                )
                    .fallbackToDestructiveMigration()
                    .build()
            }
            return INSTANCE as WeatherDB
        }
    }
}

 */


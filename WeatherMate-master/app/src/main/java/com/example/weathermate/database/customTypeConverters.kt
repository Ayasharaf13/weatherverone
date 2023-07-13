package com.example.weathermate.database


import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import androidx.room.TypeConverter
import com.example.weathermate.weather_data_fetcher.*


//Room by default can store primitive types only, to store custom objects we need to use
// TypeConverter to convert the custom objects into known database types. In our case

class Converters {
    private val gson = Gson()


    @TypeConverter
    fun fromJsonToCurrentForecast(json: String): CurrentForecast {
        return gson.fromJson(json, CurrentForecast::class.java)
    }

    @TypeConverter
    fun fromCurrentForecastToJson(forecast: CurrentForecast): String = gson.toJson(forecast)

    @TypeConverter
    fun fromJsonToWeatherStat(json: String): WeatherStat? {
        return gson.fromJson(json, WeatherStat::class.java)
    }

    @TypeConverter
    fun fromWeatherStatToJson(stat: WeatherStat?): String = gson.toJson(stat)

    @TypeConverter
    fun fromJsonToCurrentWeatherList(json: String): List<CurrentWeather> {
        val type = object : TypeToken<List<CurrentWeather>>() {}.type
        return gson.fromJson(json, type)
    }

    @TypeConverter
    fun fromCurrentWeatherListToJson(list: List<CurrentWeather>): String = gson.toJson(list)

    @TypeConverter
    fun fromJsonToHourlyList(json: String): List<HourlyForecast> {
        val type = object : TypeToken<List<HourlyForecast>>() {}.type
        return gson.fromJson(json, type)
    }

    @TypeConverter
    fun fromHourlyListToJson(list: List<HourlyForecast>): String = gson.toJson(list)

    @TypeConverter
    fun fromJsonToTempDetails(json: String): TempDetails {
        return gson.fromJson(json, TempDetails::class.java)
    }

    @TypeConverter
    fun fromTempDetailsToJson(temp: TempDetails): String = gson.toJson(temp)

    @TypeConverter
    fun fromJsonToFeelDetails(json: String): FeelLikeDetails {
        return gson.fromJson(json, FeelLikeDetails::class.java)
    }

    @TypeConverter
    fun fromFeelDetailsToJson(feel: FeelLikeDetails): String = gson.toJson(feel)

    @TypeConverter
    fun fromJsonToDailyList(json: String): List<DailyForecast> {
        val type = object : TypeToken<List<DailyForecast>>() {}.type
        return gson.fromJson(json, type)
    }

    @TypeConverter
    fun fromDailyListToJson(list: List<DailyForecast>): String = gson.toJson(list)


    @TypeConverter
    fun fromAlertsToString(alerts: List<Alert>?): String? {
        return gson.toJson(alerts)
    }

    @TypeConverter
    fun fromStringToAlerts(alerts: String?): List<Alert>? {
        return gson.fromJson(alerts, object : TypeToken<List<Alert>>() {}.type)
    }



/*

    @TypeConverter
    fun fromAlertsToString(alerts: List<Alert>?): String {
        if (!alerts.isNullOrEmpty()) {
            return Gson().toJson(alerts)
        }
        return ""
    }

    @TypeConverter
    fun fromStringToAlerts(alerts: String?): List<Alert> {
        if (alerts.isNullOrEmpty()) {
            return emptyList()
        }
        val listType = object : TypeToken<List<Alert?>?>() {}.type
        return Gson().fromJson(alerts, listType)
    }

 */

}


/*
import com.example.weathermate.weather_data_fetcher.DailyForecast
import com.example.weathermate.weather_data_fetcher.HourlyForecast
import com.example.weathermate.weather_data_fetcher.WeatherResponse

import com.google.gson.TypeAdapter
import com.google.gson.TypeAdapterFactory

import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import com.google.gson.stream.JsonWriter
import androidx.room.TypeConverter
import com.example.weathermate.weather_data_fetcher.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken








private val gson = Gson()
//Room by default can store primitive types only, to store custom objects we need to use
// TypeConverter to convert the custom objects into known database types. In our case
class CurrentWeatherConverter {

    @TypeConverter
    fun fromJson(json: String): CurrentForecast {
        return gson.fromJson(json, CurrentForecast::class.java)
    }

    @TypeConverter
    fun toJson(forecast: CurrentForecast): String = gson.toJson(forecast)
}

class WeatherStatConverter {

    @TypeConverter
    fun fromJson(json: String): WeatherStat? {
        return gson.fromJson(json, WeatherStat::class.java)
    }

    @TypeConverter
    fun toJson(stat: WeatherStat?): String = gson.toJson(stat)
}

class CurrentWeatherListConverter {

    @TypeConverter
    fun fromJson(json: String): List<CurrentWeather> {

        val type = object : TypeToken<List<CurrentWeather>>() {}.type
        return gson.fromJson(json, type)
    }

    @TypeConverter
    fun toJson(list: List<CurrentWeather>): String = gson.toJson(list)
}

class HourlyListConverter{
    @TypeConverter
    fun fromJson(json: String) : List<HourlyForecast>{
        val type = object : TypeToken<List<HourlyForecast>>() {}.type
        return gson.fromJson(json, type)
    }

    @TypeConverter
    fun toJson(list: List<HourlyForecast>) : String = gson.toJson(list)
}

class TempDetailsConverter{
    @TypeConverter
    fun fromJson(json: String) : TempDetails{
        return gson.fromJson(json,TempDetails::class.java)
    }

    @TypeConverter
    fun toJson(temp: TempDetails): String = gson.toJson(temp)
}

class FeelDetailsConverter{
    @TypeConverter
    fun fromJson(json: String) : FeelLikeDetails{
        return gson.fromJson(json,FeelLikeDetails::class.java)
    }

    @TypeConverter
    fun toJson(feel: FeelLikeDetails): String = gson.toJson(feel)
}

class DailyListConverter{
    @TypeConverter
    fun fromJson(json: String) : List<DailyForecast>{
        val type = object : TypeToken<List<DailyForecast>>() {}.type
        return gson.fromJson(json, type)
    }

    @TypeConverter
    fun toJson(list: List<DailyForecast>) : String = gson.toJson(list)
}

 */




package com.example.weathermate.weather_data_fetcher

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.weathermate.database.*
import com.google.gson.annotations.SerializedName
import java.io.Serializable


/*

//@Entity("weather",primaryKeys = ["id"])
@TypeConverters(
    CurrentWeatherConverter::class,
    CurrentWeatherListConverter::class,
    WeatherStatConverter::class,
    HourlyListConverter::class,
    TempDetailsConverter::class,
    FeelDetailsConverter::class,
    DailyListConverter::class
)

 */








//calling all the converters here and room will handle if a non primitive type object
//has another non primitive type object inside it. Room will manipulate this.
/*
fun toJsonString(weatherResponse: WeatherResponse): String {
    val gson = Gson()
    return gson.toJson(weatherResponse)
}

// Function to convert JSON string to WeatherResponse object using Gson
fun fromJsonString(json: String): WeatherResponse {
    val gson = Gson()
    return gson.fromJson(json, WeatherResponse::class.java)
}

 */



//@Entity(tableName = "CurrentWeather")
/*
@Entity(tableName = "weather")
@TypeConverters(Converters::class)
data class OneCallResponse(
    @PrimaryKey(autoGenerate = true)
    var id: Int=1,
    val lat: Double,
    val lon: Double,
    val timezone: String,
    val timezone_offset: Long,
    val current: CurrentForecast,
    val hourly: List<HourlyForecast>,
    val daily: List<DailyForecast>,
    val alerts: List<Alert>?
): Serializable

 */

@Entity(tableName = "weather")
@TypeConverters(Converters::class)
    data class WeatherResponse(
@PrimaryKey
    var id: Int = 1,


    @ColumnInfo
    @SerializedName("lat")
    var cityLatitude: Double = 0.0,

    @ColumnInfo
    @SerializedName("lon")
    var cityLongitude: Double = 0.0,

    @ColumnInfo
    @SerializedName("timezone")
    var locationName: String = "",

    @ColumnInfo
    var timezone_offset: Int = 0,

    @ColumnInfo
    @SerializedName("current")
    var currentForecast: CurrentForecast? = null,

    @ColumnInfo
    @SerializedName("hourly")
    var hourlyForecast: List<HourlyForecast> = listOf(),

    @ColumnInfo
    @SerializedName("daily")
    var dailyForecast: List<DailyForecast> = listOf(),
/*
     @ColumnInfo
     @SerializedName("timezone")
      val timezone: String = "",

 */

        @ColumnInfo
        @SerializedName("alerts")
        val alerts: List<Alert> = emptyList()








) : Serializable


/*
 var id: Int=1,
    val lat: Double,
    val lon: Double,
    val timezone: String,
    val timezone_offset: Long,
    val current: CurrentForecast,
    val hourly: List<HourlyForecast>,
    val daily: List<DailyForecast>,
    val alerts: List<Alert>?
 */

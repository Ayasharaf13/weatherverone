package com.example.weathermate.weather_data_fetcher

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.fasterxml.jackson.annotation.JsonProperty
import com.google.gson.annotations.SerializedName
import java.io.Serializable


data class WeatherStat(
    @SerializedName("1h") var precipitation: Double
) : Serializable {
    constructor() : this(0.0)
}

data class CurrentWeather(
    var id: Int,
    var main: String,
    var description: String,
    var icon: String
) : Serializable {
    constructor() : this(0, "", "", "")
}


data class CurrentForecast(

    @SerializedName("dt") var time: Long,
    @SerializedName("sunrise") var sunrise: Long,
    @SerializedName("sunset") var sunset: Long,
    @SerializedName("temp")var temp: Double,
    @SerializedName("feels_like") var feelsLike: Double,
    @SerializedName("pressure") var pressure: Int,
    @SerializedName("humidity") var humidity: Int,
    @SerializedName("dew_point") var atmosphericTemp: Double,
    @SerializedName("uvi") var uvi: Double,
    @SerializedName("clouds") var clouds: Int,
    @SerializedName("visibility") var visibility: Int,
    @SerializedName("wind_speed") var windSpeed: Double,
    @SerializedName("wind_gust") var windGust: Double?,
    @SerializedName("wind_deg") var windDeg: Int,
    @SerializedName("rain") var rain: WeatherStat?,
    @SerializedName("snow") var snow: WeatherStat?,
    var weather: List<CurrentWeather>
) : Serializable{
    constructor() : this(
        0, 0, 0, 0.0, 0.0, 0, 0, 0.0, 0.0, 0, 0, 0.0, null, 0, null, null, emptyList()
    )
}



data class TempDetails(
    var day: Double,
    var min: Double,
    var max: Double,
    var night: Double,
    var eve: Double,
    var morn: Double
) : Serializable {
    constructor() : this(0.0, 0.0, 0.0, 0.0, 0.0, 0.0)
}

data class FeelLikeDetails(
    var day: Double,
    var night: Double,
    var eve: Double,
    var morn: Double
) : Serializable {
    constructor() : this(0.0, 0.0, 0.0, 0.0)
}
data class DailyForecast(
    @SerializedName("dt") var time: Long,
    @SerializedName("sunrise") var sunrise: Long,
    @SerializedName("sunset") var sunset: Long,
    @SerializedName("moonrise") var moonrise: Long,
    @SerializedName("moonset") var moonset: Long,
    @SerializedName("moon_phase")var moon_phase: Double,
    @SerializedName("temp")var temp: TempDetails,
    @SerializedName("feels_like") var feelsLike: FeelLikeDetails,
    @SerializedName("pressure") var pressure: Int,
    @SerializedName("humidity") var humidity: Int,
    @SerializedName("dew_point") var atmosphericTemp: Double,
    @SerializedName("uvi") var uvi: Double,
    @SerializedName("pop") var percentOfProbability: Double,
    @SerializedName("clouds")var clouds: Int,
    @SerializedName("wind_speed") var windSpeed: Double,
    @SerializedName("wind_gust") var windGust: Double,
    @SerializedName("wind_deg") var windDeg: Int,
    var rain: Double?,
    var snow: Double?,
    var weather: List<CurrentWeather>
): Serializable {
    constructor() : this(
        0, 0, 0, 0, 0, 0.0, TempDetails(), FeelLikeDetails(), 0, 0, 0.0, 0.0, 0.0, 0, 0.0, 0.0, 0, null, null, emptyList()
    )
}


data class Minutely(
    @JsonProperty("dt") var dt: Int,
    @JsonProperty("precipitation") var precipitation: Int
) : Serializable {
    constructor() : this(0, 0)
}

data class Alert(
    val sender_name: String,
    val event: String,
    val start: Long,
    val end: Long,
    val description: String
):Serializable



data class HourlyForecast(
    @SerializedName("dt") var time: Long = 0,
    @SerializedName("temp") var temp: Double = 0.0,
    @SerializedName("feels_like") var feelsLike: Double = 0.0,
    @SerializedName("pressure") var pressure: Int = 0,
    @SerializedName("humidity") var humidity: Int = 0,
    @SerializedName("dew_point") var atmosphericTemp: Double = 0.0,
    @SerializedName("uvi")var uvi: Double = 0.0,
    @SerializedName("clouds") var clouds: Int = 0,
    @SerializedName("visibility")  var visibility: Int = 0,
    @SerializedName("wind_speed") var windSpeed: Double = 0.0,
    @SerializedName("wind_gust") var windGust: Double? = null,
    @SerializedName("wind_deg") var windDeg: Int = 0,
    @SerializedName("pop") var percentOfProbability: Double = 0.0,
    var rain: WeatherStat? = null,
    var snow: WeatherStat? = null,
    var weather: List<CurrentWeather> = emptyList()
) : Serializable






/*
data class Json4KotlinBase(
    @JsonProperty("lat") var lat: Double,
    @JsonProperty("lon") var lon: Double,
    @JsonProperty("timezone") var timezone: String,
    @JsonProperty("timezone_offset") var timezone_offset: Int,
    @JsonProperty("current") var current: CurrentForecast,
    @JsonProperty("minutely") var minutely: List<Minutely>,
    @JsonProperty("hourly") var hourly: List<HourlyForecast>,
    @JsonProperty("daily") var daily: List<DailyForecast>
) : Serializable {
    constructor() : this(
        0.0, 0.0, "", 0, CurrentForecast(), emptyList(), emptyList(), emptyList()
    )
    }
 */



//Generic means hourly or current or daily - forecast


/*
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class CurrentForecast(

    @SerializedName("dt") var time: Long,
    var sunrise: Long,
    var sunset: Long,
    var temp: Double,
    @SerializedName("feels_like") var feelsLike: Double,
    var pressure: Int,
    var humidity: Int,
    @SerializedName("dew_point") var atmosphericTemp: Double,
    var uvi: Double,
    var clouds: Int,
    var visibility: Int,
    @SerializedName("wind_speed") var windSpeed: Double,
    @SerializedName("wind_gust") var windGust: Double?,
    @SerializedName("wind_deg") var windDeg: Int,
    var rain: WeatherStat?,
    var snow: WeatherStat?,
    var weather: List<CurrentWeather>
) : Serializable

data class WeatherStat(//rain - snow
    @SerializedName("1h") var precipitation: Double
): Serializable

data class CurrentWeather(
    var id: Int,
    var main: String,
    var description: String,
    var icon: String//icon : 04d
): Serializable

data class HourlyForecast(
    @SerializedName("dt") var time: Long,
    var temp: Double,
    @SerializedName("feels_like") var feelsLike: Double,
    var pressure: Int,
    var humidity: Int,
    @SerializedName("dew_point") var atmosphericTemp: Double,
    var uvi: Double,
    var clouds: Int,
    var visibility: Int,
    @SerializedName("wind_speed") var windSpeed: Double,
    @SerializedName("wind_gust") var windGust: Double?,
    @SerializedName("wind_deg") var windDeg: Int,
    @SerializedName("pop") var percentOfProbability: Double,
    var rain: WeatherStat?,
    var snow: WeatherStat?,
    var weather: List<CurrentWeather>
): Serializable

data class TempDetails(
    var day: Double,
    var min: Double,
    var max: Double,
    var night: Double,
    var eve: Double,
    var morn: Double,
): Serializable

data class FeelLikeDetails(
    var day: Double,
    var night: Double,
    var eve: Double,
    var morn: Double,
): Serializable

data class DailyForecast(
    @SerializedName("dt") var time: Long,
    var sunrise: Long,
    var sunset: Long,
    var moonrise: Long,
    var moonset: Long,
    var moon_phase: Double,
    var temp: TempDetails,
    @SerializedName("feels_like") var feelsLike: FeelLikeDetails,
    var pressure: Int,
    var humidity: Int,
    @SerializedName("dew_point") var atmosphericTemp: Double,
    var uvi: Double,
    @SerializedName("pop") var percentOfProbability: Double,
    var clouds: Int,
    @SerializedName("wind_speed") var windSpeed: Double,
    @SerializedName("wind_gust") var windGust: Double,
    @SerializedName("wind_deg") var windDeg: Int,
    var rain: Double?,
    var snow: Double?,
    var weather: List<CurrentWeather>
): Serializable


data class Minutely (

    @SerializedName("dt") var dt : Int,
    @SerializedName("precipitation") var precipitation : Int
): Serializable

data class Json4Kotlin_Base (

    @SerializedName("lat") var lat : Double,
    @SerializedName("lon") var lon : Double,
    @SerializedName("timezone") var timezone : String,
    @SerializedName("timezone_offset") var timezone_offset : Int,
    @SerializedName("current") var current : CurrentForecast,
    @SerializedName("minutely") var minutely : List<Minutely>,
    @SerializedName("hourly") var hourly : List<HourlyForecast>,
    @SerializedName("daily") var daily : List<DailyForecast>
): Serializable

 */

/*

data class CurrentForecast (

    @SerializedName("dt") var dt : Int,
    @SerializedName("sunrise") var sunrise : Int,
    @SerializedName("sunset") var sunset : Int,
    @SerializedName("temp") var temp : Double,
   // @SerializedName("feels_like") var feels_like : Double,
    @SerializedName("feels_like")  var feelsLike: Double,
    @SerializedName("dt")
    var time: Long ,
    @SerializedName("pressure") var pressure : Int,
    @SerializedName("humidity") var humidity : Int,
    @SerializedName("dew_point") var dew_point : Double,
    @SerializedName("uvi") var uvi : Double,
    @SerializedName("clouds") var clouds : Int,
    @SerializedName("visibility") var visibility : Int,
  //  @SerializedName("wind_speed") var wind_speed : Double,
    @SerializedName("wind_speed")
    var windSpeed: Double, // Add the property
    @SerializedName("wind_deg") var wind_deg : Int,
    @SerializedName("weather") var weather : List<CurrentWeather>
): Serializable




data class FeelLikeDetails (

    @SerializedName("day") var day : Double,
    @SerializedName("night") var night : Double,
    @SerializedName("eve") var eve : Double,
    @SerializedName("morn") var morn : Double
): Serializable



data class CurrentWeather (

    @SerializedName("id") var id : Int,
    @SerializedName("main") var main : String,
    @SerializedName("description") var description : String,
    @SerializedName("icon") var icon : String
): Serializable


data class WeatherStat(//rain - snow
    @SerializedName("1h") var precipitation: Double
): Serializable

data class TempDetails (

    @SerializedName("day") var day : Double,
    @SerializedName("min") var min : Double,
    @SerializedName("max") var max : Double,
    @SerializedName("night") var night : Double,
    @SerializedName("eve") var eve : Double,
    @SerializedName("morn") var morn : Double
): Serializable



data class DailyForecast (

   // @SerializedName("dt") var dt : Int,
    @SerializedName("dt") var time: Long,
    @SerializedName("sunrise") var sunrise : Int,
    @SerializedName("sunset") var sunset : Int,
    @SerializedName("moonrise") var moonrise : Int,
    @SerializedName("moonset") var moonset : Int,
    @SerializedName("moon_phase") var moon_phase : Double,
    @SerializedName("temp") var temp :  TempDetails,
    @SerializedName("feels_like") var feels_like :  FeelLikeDetails,
    @SerializedName("pressure") var pressure : Int,
    @SerializedName("humidity") var humidity : Int,
    @SerializedName("dew_point") var dew_point : Double,
    @SerializedName("wind_speed") var wind_speed : Double,
    @SerializedName("wind_deg") var wind_deg : Int,
    @SerializedName("wind_gust") var wind_gust : Double,
    @SerializedName("weather") var weather : List<CurrentWeather>,
    @SerializedName("clouds") var clouds : Int,
    @SerializedName("pop") var pop : Int,
    @SerializedName("uvi") var uvi : Double
): Serializable





data class HourlyForecast (
    @SerializedName("dt") var time: Long,
  //  @SerializedName("dt") var dt : Int,
    @SerializedName("temp") var temp : Double,
    @SerializedName("feels_like") var feels_like : Double,
    @SerializedName("pressure") var pressure : Int,
    @SerializedName("humidity") var humidity : Int,
    @SerializedName("dew_point") var dew_point : Double,
    @SerializedName("uvi") var uvi : Double,
    @SerializedName("clouds") var clouds : Int,
    @SerializedName("visibility") var visibility : Int,
    @SerializedName("wind_speed") var wind_speed : Double,
    @SerializedName("wind_deg") var wind_deg : Int,
    @SerializedName("wind_gust") var wind_gust : Double,
    @SerializedName("weather") var weather : List<CurrentWeather>,
    @SerializedName("pop") var pop : Int
): Serializable

data class Minutely (

    @SerializedName("dt") var dt : Int,
    @SerializedName("precipitation") var precipitation : Int
): Serializable

data class Json4Kotlin_Base (

    @SerializedName("lat") var lat : Double,
    @SerializedName("lon") var lon : Double,
    @SerializedName("timezone") var timezone : String,
    @SerializedName("timezone_offset") var timezone_offset : Int,
    @SerializedName("current") var current : CurrentForecast,
    @SerializedName("minutely") var minutely : List<Minutely>,
    @SerializedName("hourly") var hourly : List<HourlyForecast>,
    @SerializedName("daily") var daily : List<DailyForecast>
): Serializable

 */

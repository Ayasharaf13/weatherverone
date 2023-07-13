package com.example.weathermate.utilities

import com.example.weathermate.R

val photos = mapOf(
    "01d" to R.mipmap.sun,
    "01n" to R.mipmap.moon,
    "02d" to R.mipmap.few_clouds_sun,
    "02n" to R.mipmap.few_clouds_moon,
    "03d" to R.mipmap.scattered_clouds,
    "03n" to R.mipmap.scattered_clouds,
    "04d" to R.mipmap.scattered_clouds,
    "04n" to R.mipmap.scattered_clouds,
    "09d" to R.mipmap.shower_rain_sun,
    "09n" to R.mipmap.shower_rain_moon,
    "10d" to R.mipmap.rain_sun,
    "10n" to R.mipmap.rain_moon,
    "11d" to R.mipmap.thunder_sun,
    "11n" to R.mipmap.thunder_moon,
    "13d" to R.mipmap.snow_sun,
    "13n" to R.mipmap.snow_moon,
    "50d" to R.mipmap.mist_sun,
    "50n" to R.mipmap.mist_moon

)

object Constants{
    const val LOCATION = "LOCATION"
    const val LANGUAGE="LANGUAGE"
    const val UNITS="units"
    enum class ENUM_LOCATION(){Map,Gps}
    const val NOTIFICATIONS = "NOTIFICATIONS"
    enum class ENUM_NOTIFICATIONS(){Enabled,Disabled}
    const val GPS_LONGITUDE="GPS_LONGITUDE"
    const val GPS_LATITUDE="GPS_LATITUDE"
    const val SHARED_PREFERENCE_NAME="SetupSharedPreferences"
   // const val API_ID="40dac0af7018969cbb541943f944ba29"
   // const val BASE_URL="https://api.openweathermap.org/data/2.5/"
    enum class ENUM_UNITS(){standard,metric,imperial}
    enum class Enum_lANGUAGE(){ar,en}
    enum class Enum_ALERT(){ALARM,NOTIFICATION}
    const val GPS_LON="GPS_LON"
    const val GPS_LAT="GPS_LAT"
    const val MAP_LONH="MAP_LONH"
    const val MAP_LATH="MAP_LATH"
    const val COUNTRY_NAME="CountryName"
    const val ALERT_TYPE="ALERT_TYPE"
    var FIRST_INSTALL=0




}
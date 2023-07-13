package com.example.weathermate.network



import com.example.weathermate.weather_data_fetcher.WeatherResponse
import retrofit2.Response

class ConcreteRemoteSource : RemoteSource {
    override suspend fun getWeatherData(
        latitude: Double,
        longitude: Double,
        units: String,
        lang: String,
        exclude: String
    ): Response<WeatherResponse> {
        return API.retrofitService.getWeatherData(
            lat = latitude,
            lon = longitude,
            units = units,
            lang = lang,
            exclude = exclude
        )
    }

    companion object {
        private var instance: ConcreteRemoteSource? = null
        fun getInstance(): ConcreteRemoteSource {
            return instance ?: synchronized(this) {
                val temp = ConcreteRemoteSource()
                instance = temp
                temp
            }


        }
    }
}





/*

class ConcreteRemoteSource : RemoteSource {

    override suspend fun getWeatherData(
        latitude: Double,
        longitude: Double,
        units: String,
        lang: String,
        exclude: String
    ): OneCallResponse {
        return API.retrofitService.getWeatherData(  lat = latitude,
            lon = longitude,
            units = units,
            lang = lang,
            exclude = exclude)
    }

    companion object {
        private var instance: ConcreteRemoteSource? = null
        fun getInstance(): ConcreteRemoteSource {
            return instance ?: synchronized(this) {
                val temp = ConcreteRemoteSource()
                instance = temp
                temp
            }
        }
    }
}

 */


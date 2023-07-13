package com.example.weathermate.home_screen.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weathermate.home_screen.model.HomeRepositoryInterface
import com.example.weathermate.network.ApiState
import com.google.android.gms.common.api.Response
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.io.IOException

class HomeViewModel(
    private val _repo: HomeRepositoryInterface,

) : ViewModel() {
    private val TAG = "HomeViewModel"

    private var _retrofitStateFlow = MutableStateFlow<ApiState>(ApiState.Loading)
    var retrofitStateFlow: MutableStateFlow<ApiState> = _retrofitStateFlow


    fun getWeatherDetails(
        latitude: Double?,
        longitude: Double?,
        units: String?,
        lang: String?

    ) {


        viewModelScope.launch {
            if (latitude != null && longitude != null && units != null && lang != null) {
                try {
                    val response = _repo.getWeatherData(latitude!!, longitude, units, lang)
                    response.collect { apiResponse ->
                        if (apiResponse.isSuccessful) {
                            val weatherResponse = apiResponse.body()
                            if (weatherResponse != null) {
                                _retrofitStateFlow.value = ApiState.Success(weatherResponse)
                            } else {
                                val errorMessage = "Empty weather response"
                                _retrofitStateFlow.value = ApiState.Failure(Throwable(errorMessage))
                                // _retrofitStateFlow.value = ApiState.Failure("Empty weather response")
                            }
                        }
                    }
                } catch (e: IOException) {
                    val errorMessage = "NetWork error"
                    _retrofitStateFlow.value = ApiState.Failure(Throwable(errorMessage))
                    //_retrofitStateFlow.value = ApiState.Failure("Network error: ${e.message}")
                } catch (e: Exception) {
                    val errorMessage = "Failed to fetch weather data"
                    _retrofitStateFlow.value = ApiState.Failure(Throwable(errorMessage))
                    // _retrofitStateFlow.value = ApiState.Failure("Failed to fetch weather data: ${e.message}")
                }
            } else {
                val errorMessage = "One or more parameters are null"
                _retrofitStateFlow.value = ApiState.Failure(Throwable(errorMessage))
                // _retrofitStateFlow.value = ApiState.Failure("One or more parameters are null")
            }
        }
    }



    private var units: String =""
    private var lang: String = ""

    fun setUnits(units: String) {
        this.units = units
    }

    fun getUnits(): String {
        return units
    }

    fun setLanguage(lang: String) {
        this.lang = lang
    }

    fun getLanguage(): String {
        return lang

    }
}








/*

        viewModelScope.launch {
            Dispatchers.IO
            if (latitude != null && longitude != null && units != null && lang != null) {

                _repo.getWeatherData(
                    latitude,
                    longitude,
                    units,
                    lang
                ).collect {
                    if (it.isSuccessful) {
                        _retrofitStateFlow.value = ApiState.Success(it.body()!!)

                    }
                }
            }
        }
    }
}

 */
        //  } else
        // retrofitStateFlow.value = ApiState.Failure("${it.errorBody().toString()}")
        // }


        //     }


/*
    fun getWeatherDetails(
        latitude: Double,
        longitude: Double,
        units: String,
        lang:String



    ) {





        viewModelScope.launch(Dispatchers.IO) {
            _repo.getWeatherData(
                latitude,
                longitude,
                units,
                lang
            ).catch {
                _retrofitStateFlow.value = ApiState.Failure(it)
                Log.i(TAG, "getWeatherDetails: ${it.message}")
            }.collect {
                if (it.isSuccessful) {
                    _retrofitStateFlow.value = ApiState.Success(it.body()!!)
                } else {
                    Log.i(TAG, "getWeatherDetails: failed ${it.errorBody().toString()}")
                }
            }
        }

    }
}

 */

/*
    fun insertWeatherDetails(weatherResponse: WeatherResponse) {
        viewModelScope.launch(Dispatchers.IO) {
            _repo.insertWeatherDetails(weatherResponse)
        }
    }

 */

        /*fun updateWeatherDetails(weatherResponse: WeatherResponse) {
        viewModelScope.launch(Dispatchers.IO) {
            _repo.updateWeatherDetails(weatherResponse)
        }
    }*/



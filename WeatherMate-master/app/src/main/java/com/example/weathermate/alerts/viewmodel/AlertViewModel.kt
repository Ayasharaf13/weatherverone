package com.example.weathermate.alerts.viewmodel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weathermate.alerts.model.AlertRepo
import com.example.weathermate.database.RoomAlertPojo
import com.example.weathermate.network.ResponseState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch


class AlertViewModel(private val _irpo: AlertRepo) : ViewModel() {
    var alertResponse = MutableStateFlow<ResponseState<List<RoomAlertPojo>>>(ResponseState.Loading)


    init {
            getAllAlerts()
        }

        fun getAllAlerts() {
            viewModelScope.launch(Dispatchers.IO) {
                _irpo.getAllAlerts()
                    ?.catch { e->alertResponse.value= ResponseState.Failure(e) }
                    ?.collect{data->alertResponse.value= ResponseState.Success(data)}

            }
        }
        fun  insertAlert(alert:RoomAlertPojo) {
            viewModelScope.launch(Dispatchers.IO) {
                _irpo.insertAlert(alert)
            }
        }
        fun deleteAlert(alert:RoomAlertPojo) {
            viewModelScope.launch(Dispatchers.IO) {
                _irpo.deleteAlert(alert)
            }
        }

    }

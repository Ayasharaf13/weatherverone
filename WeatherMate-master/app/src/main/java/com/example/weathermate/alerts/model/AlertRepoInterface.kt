package com.example.weathermate.alerts.model

import com.example.weathermate.database.RoomAlertPojo
import kotlinx.coroutines.flow.Flow

interface AlertRepoInterface {

    fun getAllAlerts(): Flow<List<RoomAlertPojo>>
    suspend fun  insertAlert(alert: RoomAlertPojo)
    suspend  fun deleteAlert(alert: RoomAlertPojo)

}
package com.example.weathermate.alerts.model

import com.example.weathermate.database.LocalSource
import com.example.weathermate.database.RoomAlertPojo
import com.example.weathermate.favorites.model.FavoriteRepository
import com.example.weathermate.network.RemoteSource
import kotlinx.coroutines.flow.Flow

/*
class AlertRepo(
    private val concreteRemoteSource: RemoteSource,
    private val concreteLocalSource: LocalSource
) : AlertRepoInterface {

    companion object {
        private var INSTANCE: FavoriteRepository? = null
        fun getInstance(
            concreteRemoteSource: RemoteSource,
            concreteLocalSource: LocalSource

        )

        : FavoriteRepository {
            return INSTANCE ?: synchronized(this) {
                val instance = FavoriteRepository(concreteRemoteSource, concreteLocalSource)
                INSTANCE = instance
                instance
            }
        }
    }

    override fun getAllAlerts(): Flow<List<RoomAlertPojo>> {
        return concreteLocalSource.getAllAlerts()
    }

    override suspend fun insertAlert(alert: RoomAlertPojo) {
        concreteLocalSource.insertAlert(alert)
    }

    override suspend fun deleteAlert(alert: RoomAlertPojo) {
        concreteLocalSource.deleteAlert(alert)
    }


}

 */




class AlertRepo(
    private val concreteRemoteSource: RemoteSource,
    private val concreteLocalSource: LocalSource
) : AlertRepoInterface {

    companion object {
        private var INSTANCE: AlertRepo? = null
        fun getInstance(
            concreteRemoteSource: RemoteSource,
            concreteLocalSource: LocalSource
        ): AlertRepo {
            return INSTANCE ?: synchronized(this) {
                val instance = AlertRepo(concreteRemoteSource, concreteLocalSource)
                INSTANCE = instance
                instance
            }
        }
    }

    override fun getAllAlerts(): Flow<List<RoomAlertPojo>> {
        return concreteLocalSource.getAllAlerts()
    }

    override suspend fun insertAlert(alert: RoomAlertPojo) {
        concreteLocalSource.insertAlert(alert)
    }

    override suspend fun deleteAlert(alert: RoomAlertPojo) {
        concreteLocalSource.deleteAlert(alert)
    }


}


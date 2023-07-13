package com.example.weathermate.alerts.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.weathermate.alerts.model.AlertRepo
import com.example.weathermate.alerts.model.AlertRepoInterface


class AlertViewModelFactory (private val _irpo: AlertRepoInterface): ViewModelProvider.Factory{
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return if (modelClass.isAssignableFrom(AlertViewModel::class.java))
            {
                AlertViewModel(_irpo as AlertRepo) as T
            }else{
                throw IllegalAccessException("View Model Class not found")
            }
        }
    }

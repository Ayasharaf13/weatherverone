package com.example.weathermate.settings.view




import android.Manifest
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager

import android.util.Log

import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import androidx.fragment.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.weathermate.R
import com.example.weathermate.databinding.FragmentSettingsBinding
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import java.util.*
import android.content.res.*
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.text.TextUtils
import com.example.weathermate.utilities.Constants
import com.google.android.material.snackbar.Snackbar

class SettingsFragment : Fragment() {
    private val TAG = "SettingsFragment"
    private val PERMISSION_ID = 10
    private lateinit var _binding : FragmentSettingsBinding
    private lateinit var sharedPreferences : SharedPreferences
    private lateinit var editor :SharedPreferences.Editor
    val args : SettingsFragmentArgs by navArgs()
    private var isSTart : Boolean? = null
    private val unitFlow = MutableSharedFlow<String>(replay = 1)
    private val langFlow = MutableSharedFlow<String>(replay = 1)


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        isSTart = true
        _binding = DataBindingUtil.inflate(inflater, R.layout.fragment_settings, container, false)
        _binding.myFragment = this
          //_binding.btnSwitchNotify.isChecked = notification ==Constants.ENUM_NOTIFICATIONS.Enabled.toString()
        sharedPreferences = getSharedPreferences(requireActivity())
        editor = sharedPreferences.edit()
         var notification = sharedPreferences.getString(Constants.NOTIFICATIONS, Constants.ENUM_NOTIFICATIONS.Enabled.toString())
        _binding.btnSwitchNotify.isChecked = notification == Constants.ENUM_NOTIFICATIONS.Enabled.toString()


        return _binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.i(TAG, "onViewCreated: ")

        _binding.btnSwitchNotify.setOnCheckedChangeListener{_,isChecked->
            if(isChecked)
            {
                sharedPreferences.edit()
                    .putString(Constants.NOTIFICATIONS, Constants.ENUM_NOTIFICATIONS.Enabled.toString())
                    .apply()
            }
            else{
                sharedPreferences.edit()
                    .putString(Constants.NOTIFICATIONS, Constants.ENUM_NOTIFICATIONS.Disabled.toString())
                    .apply()
            }

        }




        lifecycleScope.launch {
            unitFlow.collect { units ->
                when (units) {
                    "metric" -> switchToCelsius()
                    "imperial" -> switchToFahrenheit()
                    "standard" -> switchToKelvin()
                    else -> Log.i(TAG, "onViewCreated: Error in initialize views")
                }
            }
        }

        lifecycleScope.launch {
            langFlow.collect { lang ->
                when (lang) {
                    "en" -> switchToEn(isSTart!!)
                    "ar" -> switchToAr(isSTart!!)
                    else -> Log.i(TAG, "onViewCreated: e in lang")
                }
            }
        }

        when(sharedPreferences.getString("units", "metric")) {
            "metric" -> unitFlow.tryEmit("metric")
            "imperial" -> unitFlow.tryEmit("imperial")
            "standard" -> unitFlow.tryEmit("standard")
        }

        when(sharedPreferences.getString("lang", "en")) {
            "en" -> langFlow.tryEmit("en")
            "ar" -> langFlow.tryEmit("ar")
        }
    }

    fun onRbClicked(view: View) {
        when(view.id) {
            _binding.rbMap.id -> {
                Log.i(TAG, "onRbClicked: rbMap")

                if (checkPermissions() && checkForInternet(requireActivity())) {
                    switchToMap()
                    val navController = Navigation.findNavController(requireActivity(),
                        R.id.nav_host_fragment_content_main)
                    val action = SettingsFragmentDirections.actionNavSettingsToMapFragment(true)
                    navController.navigate(action)
                } else {
                    switchToGps()
                    Snackbar.make(view, "Check internet and GPS", Snackbar.LENGTH_LONG).show()
                }
            }
            _binding.rbGps.id -> {
                switchToGps()
            }


            _binding.rbCelsius.id -> {
                unitFlow.tryEmit("metric")
            }
            _binding.rbFahrenheit.id -> {
                unitFlow.tryEmit("imperial")
            }
            _binding.rbKelvin.id -> {
                unitFlow.tryEmit("standard")
            }

            _binding.rbEn.id -> {
                if (sharedPreferences.getString("lang", "en") == "ar") {
                    langFlow.tryEmit("en")
                }
            }
            _binding.rbAr.id -> {
                if (sharedPreferences.getString("lang", "en") == "en") {
                    langFlow.tryEmit("ar")
                } else {
                    print(sharedPreferences.getString("lang", "z"))
                }
            }
        }
    }


    private fun getSharedPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences("weather_prefs", Context.MODE_PRIVATE)
    }

    private fun switchToGps() {
        _binding.rbGps.isChecked = true
        _binding.rbGps.setTextColor(ContextCompat.getColor(requireContext(), R.color.main_color))
        _binding.rbMap.isChecked = false
        _binding.rbMap.setTextColor(ContextCompat.getColor(requireContext(), R.color.degree_type))
        editor.putBoolean("is_gps", true)
        editor.apply()
    }

    private fun switchToMap() {
        _binding.rbGps.isChecked = false
        _binding.rbGps.setTextColor(ContextCompat.getColor(requireContext(), R.color.degree_type))
        _binding.rbMap.isChecked = true
        _binding.rbMap.setTextColor(ContextCompat.getColor(requireContext(), R.color.main_color))
        editor.putBoolean("is_gps", false)
        editor.apply()
    }



    private fun switchToCelsius() {
        _binding.rbFahrenheit.isChecked = false
        _binding.rbFahrenheit.setTextColor(ContextCompat.getColor(requireContext(), R.color.degree_type))
        _binding.rbKelvin.isChecked = false
        _binding.rbKelvin.setTextColor(ContextCompat.getColor(requireContext(), R.color.degree_type))

        _binding.rbCelsius.isChecked = true
        _binding.rbCelsius.setTextColor(ContextCompat.getColor(requireContext(), R.color.main_color))
        editor.putString("units", "metric")
        editor.apply()
    }

    private fun switchToFahrenheit() {
        _binding.rbCelsius.isChecked = false
        _binding.rbCelsius.setTextColor(ContextCompat.getColor(requireContext(), R.color.degree_type))
        _binding.rbKelvin.isChecked = false
        _binding.rbKelvin.setTextColor(ContextCompat.getColor(requireContext(), R.color.degree_type))

        _binding.rbFahrenheit.isChecked = true
        _binding.rbFahrenheit.setTextColor(ContextCompat.getColor(requireContext(), R.color.main_color))
        editor.putString("units", "imperial")
        editor.apply()
    }

    private fun switchToKelvin() {
        _binding.rbCelsius.isChecked = false
        _binding.rbCelsius.setTextColor(ContextCompat.getColor(requireContext(), R.color.degree_type))
        _binding.rbFahrenheit.isChecked = false
        _binding.rbFahrenheit.setTextColor(ContextCompat.getColor(requireContext(), R.color.degree_type))

        _binding.rbKelvin.isChecked = true
        _binding.rbKelvin.setTextColor(ContextCompat.getColor(requireContext(), R.color.main_color))
        editor.putString("units", "standard")
        editor.apply()
    }


    private fun switchToEn(flag: Boolean) {
        _binding.rbAr.isChecked = false
        _binding.rbAr.setTextColor(ContextCompat.getColor(requireContext(), R.color.degree_type))

        if (!flag) {
            setLocal("en")
        } else {
            isSTart = false
        }

        _binding.rbEn.isChecked = true
        _binding.rbEn.setTextColor(ContextCompat.getColor(requireContext(), R.color.main_color))
        editor.putString("lang", "en")
        editor.apply()
    }

    private fun switchToAr(flag: Boolean) {
        _binding.rbEn.isChecked = false
        _binding.rbEn.setTextColor(ContextCompat.getColor(requireContext(), R.color.degree_type))

        if (!flag) {
            setLocal("ar")
        } else {
            isSTart = false
        }

        _binding.rbAr.isChecked = true
        _binding.rbAr.setTextColor(ContextCompat.getColor(requireContext(), R.color.main_color))
        editor.putString("lang", "ar")
        editor.apply()
    }

    private fun setLocal(lang: String) {
        val local = Locale(lang)
        Locale.setDefault(local)
        val config = Configuration()
        config.setLocale(local)
        requireActivity().baseContext.resources.updateConfiguration(config, requireActivity().baseContext.resources.displayMetrics)

        // Determine layout direction based on selected language
        val layoutDirection = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            if (TextUtils.getLayoutDirectionFromLocale(local) == View.LAYOUT_DIRECTION_RTL) {
                View.LAYOUT_DIRECTION_RTL
            } else {
                View.LAYOUT_DIRECTION_LTR
            }
        } else {
            View.LAYOUT_DIRECTION_LTR
        }

        // Set layout direction on the root view
        requireActivity().window.decorView.layoutDirection = layoutDirection

        Log.i(TAG, "setLocal: hi")
        this.requireActivity().recreate()
    }


    private fun checkPermissions(): Boolean {
        return ActivityCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermissions() {
        requestPermissions(
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            ),
            PERMISSION_ID
        )
    }

    private fun checkForInternet(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val network = connectivityManager.activeNetwork ?: return false
            val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false

            return when {
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                else -> false
            }
        } else {
            @Suppress("DEPRECATION") val networkInfo = connectivityManager.activeNetworkInfo ?: return false
            @Suppress("DEPRECATION")
            return networkInfo.isConnected
        }
    }


}



package com.example.weathermate.favorite_weather.view

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.example.weathermate.R
import com.example.weathermate.database.ConcreteLocalSource
import com.example.weathermate.database.WeatherDB
import com.example.weathermate.databinding.FragmentFavoriteWeatherBinding
import com.example.weathermate.home_screen.model.HomeRepository
import com.example.weathermate.home_screen.view.DailyAdapter
import com.example.weathermate.home_screen.view.HourlyAdapter
import com.example.weathermate.home_screen.viewmodel.HomeViewModel
import com.example.weathermate.home_screen.viewmodel.HomeViewModelFactory
import com.example.weathermate.network.ApiState
import com.example.weathermate.network.ConcreteRemoteSource
import com.example.weathermate.utilities.photos
import com.example.weathermate.weather_data_fetcher.WeatherResponse
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.io.IOException
import java.util.*

class FavoriteWeatherFragment : Fragment() {

    private val TAG = "FavoriteWeatherFragment"
    private lateinit var _binding: FragmentFavoriteWeatherBinding

    private lateinit var favoriteWeatherVm: HomeViewModel
    private lateinit var factory: HomeViewModelFactory

    private lateinit var hourlyAdapter: HourlyAdapter
    private lateinit var dailyAdapter: DailyAdapter

    val args: FavoriteWeatherFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentFavoriteWeatherBinding.inflate(inflater)

        hourlyAdapter = HourlyAdapter()
        dailyAdapter = DailyAdapter()

        factory = HomeViewModelFactory(
            HomeRepository.getInstance(
                ConcreteRemoteSource(),
                ConcreteLocalSource(WeatherDB.getInstance(requireContext()).getWeatherDao())
            )
        )

        favoriteWeatherVm = ViewModelProvider(this, factory).get(HomeViewModel::class.java)


        fun checkForInternet(context: Context): Boolean {
            val connectivityManager =
                context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val network = connectivityManager.activeNetwork ?: return false
                val activeNetwork =
                    connectivityManager.getNetworkCapabilities(network) ?: return false

                return when {
                    activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                    activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                    else -> false
                }
            } else {
                @Suppress("DEPRECATION")
                val networkInfo = connectivityManager.activeNetworkInfo ?: return false
                @Suppress("DEPRECATION")
                return networkInfo.isConnected
            }
        }

        if (checkForInternet(requireContext())) {
            val units = favoriteWeatherVm.getUnits()
            val lang = favoriteWeatherVm.getLanguage()
            getWeatherDetails(
                args.latlong.split(",").get(0).toDouble(),
                args.latlong.split(",").get(1).toDouble(),
                units,
                lang


            )
        }
        return _binding.root
    }

    private fun getWeatherDetails(
        latitude: Double, longitude: Double, units: String,
        lang: String,
    ) {
        favoriteWeatherVm.getWeatherDetails(latitude, longitude, units, lang)






       fun onResponseState(weatherResponse: WeatherResponse) {
            _binding.todayImg.setImageResource(
                photos.get(
                    weatherResponse.currentForecast!!.weather.get(
                        0
                    ).icon
                )!!
            )


            hourlyAdapter.setHourlyForecasts(weatherResponse.hourlyForecast.take(24))
            _binding.recHourly.adapter = hourlyAdapter

            dailyAdapter.setDailyForecasts(weatherResponse.dailyForecast.drop(1).take(7))
            _binding.recNextDays.adapter = dailyAdapter

            _binding.progressBar.visibility = View.GONE
            _binding.mainGroup.visibility = View.VISIBLE
            _binding.progressBar.pauseAnimation()
        }

        lifecycleScope.launch {
            favoriteWeatherVm.retrofitStateFlow.collectLatest { apiState ->
                when (apiState) {
                    is ApiState.Success -> {
                        val units = favoriteWeatherVm.getUnits()
                        val lang = favoriteWeatherVm.getLanguage()

                        _binding.windValPer.text =
                            getString(if (units == "imperial") R.string.wind_unit_2 else R.string.wind_unit)
                        _binding.weatherApiResponse = apiState.data

                        try {
                            val geocoder = Geocoder(requireContext(), Locale.getDefault())
                            val address = geocoder.getFromLocation(
                                apiState.data.cityLatitude,
                                apiState.data.cityLongitude,
                                1
                            ) as List<Address>
                            apiState.data.locationName = address.getOrNull(0)?.let { address ->
                                address.getAddressLine(0).split(",").getOrNull(1)
                                    ?: address.getAddressLine(0).split(",").getOrNull(0)
                            } ?: "-"
                        } catch (e: IOException) {
                            apiState.data.locationName = "-"

                        }

                        if (favoriteWeatherVm.getLanguage() == "en") {
                            _binding.tvCurrentLocation.text =
                                apiState.data.locationName.split(",").getOrNull(1)
                                    ?: apiState.data.locationName
                        }

                        onResponseState(apiState.data)
                    }
                    is ApiState.Loading -> {
                        _binding.progressBar.playAnimation()
                        _binding.progressBar.visibility = View.VISIBLE
                        _binding.mainGroup.visibility = View.GONE
                    }
                    is ApiState.Failure -> {
                        _binding.progressBar.pauseAnimation()

                    }
                }
            }
        }

    }
}






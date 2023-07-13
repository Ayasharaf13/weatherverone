package com.example.weathermate.favorites.view






import android.Manifest
import android.content.Context

import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.weathermate.R
import com.example.weathermate.database.ConcreteLocalSource
import com.example.weathermate.database.DbState
import com.example.weathermate.database.WeatherDB
import com.example.weathermate.databinding.FragmentFavoritesBinding
import com.example.weathermate.favorites.model.FavoriteRepository
import com.example.weathermate.favorites.viewmodel.FavoriteViewModel
import com.example.weathermate.favorites.viewmodel.FavoriteViewModelFactory
import com.example.weathermate.map.MapFragment
import com.example.weathermate.network.ApiState
import com.example.weathermate.network.ConcreteRemoteSource
import com.example.weathermate.weather_data_fetcher.FavoriteWeatherResponse
import com.example.weathermate.weather_data_fetcher.WeatherResponse
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import java.util.*




class FavoritesFragment : Fragment() {
    private val TAG = "FavoritesFragment"

    private lateinit var _binding: FragmentFavoritesBinding

    private lateinit var favoriteViewModel: FavoriteViewModel
    private lateinit var factory: FavoriteViewModelFactory
    private lateinit var favoritesAdapter: FavoritesAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFavoritesBinding.inflate(inflater)
        _binding.myFragment = this

        factory =
            FavoriteViewModelFactory(
                FavoriteRepository.getInstance(
                    ConcreteRemoteSource(),
                    ConcreteLocalSource(WeatherDB.getInstance(requireContext()).getWeatherDao())
                )
            )

        favoriteViewModel = ViewModelProvider(this, factory).get(FavoriteViewModel::class.java)

        return _binding.root
    }


    fun getLocalWeatherDetails() {
        Log.i(TAG, "getLocalWeatherDetails: ")
        lifecycleScope.launchWhenResumed {
            favoriteViewModel.roomStateFlow.collect {
                when (it) {
                    is DbState.Success -> {
                        Log.i(TAG, "getLocalWeatherDetails: ${it.favoriteWeatherResponse!!.size}")
                        if (it.favoriteWeatherResponse.isNotEmpty()) {
                            favoritesAdapter.setFavoriteWeatherResponses(it.favoriteWeatherResponse)

                            _binding.progressBar.visibility = View.GONE
                            _binding.progressBar.pauseAnimation()
                            Log.i(TAG, "getLocalWeatherDetails: success in if")
                        } else {
                            Log.i(TAG, "getLocalWeatherDetails: success in else")
                            _binding.progressBar.visibility = View.GONE
                            _binding.progressBar.pauseAnimation()
                            favoritesAdapter.setFavoriteWeatherResponses(listOf())
                        }
                        _binding.mainGroup.visibility = View.VISIBLE
                    }

                    else -> {}
                }

            }
        }

    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val args: FavoritesFragmentArgs by navArgs()
        favoritesAdapter = FavoritesAdapter(favoriteViewModel, requireActivity(), view)

        _binding.rvFavs.adapter = favoritesAdapter

        Log.i(TAG, "onCreateView:  ${args.locationLatLong.length}")
        Log.i(
            TAG,
            "stack: ${findNavController().previousBackStackEntry?.destination?.id}\n${R.id.mapFragment}"
        )
        if (args.locationLatLong != "hi" && MapFragment.isFromMap) {
            val hasInternetAndPermissions = checkForInternet(requireContext()) && checkPermissions()
            if (hasInternetAndPermissions) {
                lifecycleScope.launch {
                    val lat = args.locationLatLong.split(",").getOrNull(0)?.toDoubleOrNull()
                    val lon = args.locationLatLong.split(",").getOrNull(1)?.toDoubleOrNull()
                    if (lat != null && lon != null) {
                        MapFragment.isFromMap = false
                        val units = favoriteViewModel.getUnits()
                        val lang = favoriteViewModel.getLanguage()
                        val exclude = "minutely,hourly,daily,alerts"
                        getWeatherDetails(lat, lon, units, lang, exclude)
                    } else {
                        Snackbar.make(view, "Invalid location", Snackbar.LENGTH_LONG).show()
                    }
                }
            } else {
                Snackbar.make(view, "Check internet & GPS", Snackbar.LENGTH_LONG).show()
            }
        }

        favoriteViewModel.getLocalFavDetails()
        getLocalWeatherDetails()
    }

    fun checkPermissions(): Boolean {
        Log.i(TAG, "checkPermissions: ")
        return ActivityCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun onFabClicked(view: View) {
        if (checkForInternet(requireContext())) {
            val navController = Navigation.findNavController(requireActivity(),
                R.id.nav_host_fragment_content_main)
            val action = FavoritesFragmentDirections.actionNavFavsToMapFragment(false)
            navController.navigate(action)
        } else {
            // No internet
        }
    }

    private fun checkForInternet(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val network = connectivityManager.activeNetwork ?: return false
            val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false

            return when {
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                else -> false
            }
        } else {
            @Suppress("DEPRECATION") val networkInfo =
                connectivityManager.activeNetworkInfo ?: return false
            @Suppress("DEPRECATION")
            return networkInfo.isConnected
        }
    }

    private fun getWeatherDetails(
        latitude: Double,
        longitude: Double,
        units: String,
        lang: String,
        exclude: String
    ) {
        favoriteViewModel.getWeatherDetails(
            latitude,
            longitude,
            units,
            lang,
            exclude
        )

        fun handleApiSuccess(data: WeatherResponse) {
            Log.i(TAG, "getWeatherDetails:apisuccess ${data.currentForecast!!.temp}")

            val locationName = runCatching {
                val geocoder = Geocoder(requireContext(), Locale.getDefault())
                val address = geocoder.getFromLocation(data.cityLatitude, data.cityLongitude, 1)
                if (address?.isNotEmpty()!!) {
                    val firstAddress = address[0]
                    val addressLine = firstAddress.getAddressLine(0)
                    addressLine.split(",").getOrNull(1) ?: addressLine.split(",").getOrNull(0)
                    ?: "-"
                } else {
                    "-"
                }
            }.getOrElse {
                Log.i(TAG, "getWeatherDetails: failed")
                "-"
            }

            val favoriteWeatherResponse = FavoriteWeatherResponse(
                latitude = latitude,
                longitude = longitude,
                cityName = locationName,
                description = data.currentForecast!!.weather.firstOrNull()?.description.orEmpty(),
                dt = data.currentForecast!!.time,
                temp = data.currentForecast!!.temp,
                img = data.currentForecast!!.weather.firstOrNull()?.icon.orEmpty()
            )
            favoriteViewModel.insertNewFavorite(favoriteWeatherResponse)
        }

        fun handleApiLoading() {
            Log.i(TAG, "getWeatherDetails: loading")
            _binding.progressBar.playAnimation()
            _binding.progressBar.visibility = View.VISIBLE
            _binding.mainGroup.visibility = View.GONE
        }

        fun handleApiFailure() {
            _binding.progressBar.pauseAnimation()
            // Log.i(TAG, "getWeatherDetails: error: ${it.msg.printStackTrace()}")
        }




        lifecycleScope.launchWhenResumed {
            favoriteViewModel.retrofitStateFlow.collect { apiState ->
                when (apiState) {
                    is ApiState.Success -> handleApiSuccess(apiState.data)
                    is ApiState.Loading -> handleApiLoading()
                    is ApiState.Failure -> handleApiFailure()
                }
            }
        }








        fun requestPermissions() {
            Log.i(TAG, "requestPermissions: ")
            requestPermissions(
                arrayOf(
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ),
                10
            )
        }
    }
}




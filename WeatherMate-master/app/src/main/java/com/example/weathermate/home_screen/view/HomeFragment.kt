package com.example.weathermate.home_screen.view
import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.constraintlayout.motion.widget.Debug.getLocation
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.weathermate.R
import com.example.weathermate.database.ConcreteLocalSource
import com.example.weathermate.database.DbState
import com.example.weathermate.database.WeatherDB
import com.example.weathermate.databinding.FragmentHomeBinding
//import com.example.weathermate.dialog.MyDialogFragment
import com.example.weathermate.home_screen.model.HomeRepository
import com.example.weathermate.utilities.photos
import com.example.weathermate.home_screen.viewmodel.HomeViewModel
import com.example.weathermate.home_screen.viewmodel.HomeViewModelFactory
import com.example.weathermate.network.ApiState
import com.example.weathermate.network.ConcreteRemoteSource
import com.example.weathermate.weather_data_fetcher.WeatherResponse
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus
import kotlinx.coroutines.withContext
import java.io.IOException
import java.util.*


class HomeFragment : Fragment() {
    private val TAG = "HomeFragment"
    private val PERMISSION_ID = 10
    private val REQUEST_CODE_MY_DIALOG = 10

    private lateinit var editor: SharedPreferences.Editor

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private lateinit var _binding: FragmentHomeBinding

    private lateinit var homeViewModel: HomeViewModel
    private lateinit var factory: HomeViewModelFactory

    private lateinit var hourlyAdapter: HourlyAdapter
    private lateinit var dailyAdapter: DailyAdapter
    override  fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.i(TAG, "onCreateView: ")
        _binding = FragmentHomeBinding.inflate(inflater)


        hourlyAdapter = HourlyAdapter()
        dailyAdapter = DailyAdapter()

        factory =
            HomeViewModelFactory(
                HomeRepository.getInstance(
                    ConcreteRemoteSource(),
                    ConcreteLocalSource(WeatherDB.getInstance(requireContext()).getWeatherDao())
                )
            )
        homeViewModel = ViewModelProvider(this, factory).get(HomeViewModel::class.java)
        if (checkForInternet(requireContext())) {
            Log.i(TAG, "checkForInternet: yes")
            getLocation()
        } else {
            lifecycleScope.launch(Dispatchers.Main) {
                Snackbar.make(requireView(), "Check your internet connection", Snackbar.LENGTH_LONG).show()
            }
        }
        return _binding.root

    }


    private fun getWeatherDetails(
        onlineData: Boolean,
        latitude: Double? = null,
        longitude: Double? = null,
        units: String? = null,
        lang: String? = null
    ) {

        if (onlineData) {
            homeViewModel.getWeatherDetails(latitude!!, longitude!!, units!!,lang!!)
            //Start Consuming
            lifecycleScope.launch {
                homeViewModel.retrofitStateFlow.collectLatest {
                    when (it) {
                        is ApiState.Success -> {
                            if (units == "imperial") {
                                _binding.windValPer.text = getString(R.string.wind_unit_2)
                            } else {
                                _binding.windValPer.text = getString(R.string.wind_unit)
                            }

                            _binding.weatherApiResponse = it.data

                            onResponseState(it.data)
                        }



                        is ApiState.Loading -> {
                            Log.i(TAG, "getWeatherDetails: loading")
                            _binding.progressBar.playAnimation()
                            _binding.progressBar.visibility = View.VISIBLE
                            _binding.mainGroup.visibility = View.GONE
                        }
                        is ApiState.Failure -> {
                            //---visiblity of whole layout gone and show error msg

                            _binding.progressBar.pauseAnimation()
                            Log.i(TAG, "getWeatherDetails: error: ${it.msg.printStackTrace()}")
                        }
                    }
                }
            }

        } else {
            if(units == "imperial"){
                _binding.windValPer.text = getString(R.string.wind_unit_2)
            }else{
                _binding.windValPer.text = getString(R.string.wind_unit)
            }

           // homeViewModel.getLocalWeatherDetails()

        }
    }




    private fun isLocationEnable(): Boolean {
        //reserve reference of location manager
        //condition could be modified in any case i want
        Log.i(TAG, "isLocationEnable: ")
        val locationManager: LocationManager =
            requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }



    @SuppressLint("MissingPermission")
    private fun getLocation() {
        if (checkPermissions()) {
            val locationManager = requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
            val isLocationEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                    locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

            if (isLocationEnabled) {
                fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
                fusedLocationClient.getCurrentLocation(LocationRequest.PRIORITY_HIGH_ACCURACY, null)
                    .addOnSuccessListener { location ->
                        if (location != null) {
                            val latitude = location.latitude
                            val longitude = location.longitude

                           lifecycleScope.launch {
                            withContext(Dispatchers.IO) {
                                getWeatherDetails(true, latitude, longitude, "standard", "en")
                            }
                            }

                          //  getWeatherDetails(true, latitude, longitude, "standard", "en")
                        } else {
                            Toast.makeText(requireContext(), "Unable to get current location", Toast.LENGTH_SHORT).show()
                        }
                    }
                    .addOnFailureListener { exception ->
                        Toast.makeText(requireContext(), "Failed to get current location: ${exception.message}", Toast.LENGTH_SHORT).show()
                    }
            } else {
                // GPS is disabled, prompt the user to enable it
                AlertDialog.Builder(requireContext())
                    .setTitle("Enable GPS")
                    .setMessage("GPS is disabled. Do you want to enable it?")
                    .setPositiveButton("Yes") { dialog, _ ->
                        dialog.dismiss()
                        val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                        startActivity(intent)
                    }
                    .setNegativeButton("No") { dialog, _ ->
                        dialog.dismiss()
                        // Handle when the user declines to enable GPS
                    }
                    .show()
            }
        } else {
            requestPermissions()
        }
    }




    private fun requestPermissions() {
        Log.i(TAG, "requestPermissions: ")
        //define permissions i want to check and custom unique permission id

        //if from activity compat onRequestPermissionsResult is not called and i need it
        requestPermissions(
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            ),
            PERMISSION_ID
        )
        //at this function the prompt is displayed and to check on the action of it
        //through this callback fun onRequestPermissionsResult()
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        Log.i(TAG, "onRequestPermissionsResult: worked")

        if (requestCode == PERMISSION_ID) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, do something with the location
                // getLocation()
            } else {
                // Permission denied, show an error message
                val alertDialog = AlertDialog.Builder(requireContext())
                    .setTitle("Permission Denied")
                    .setMessage("Location permission is required to access the current location.")
                    .setPositiveButton("OK") { dialog, _ ->
                        dialog.dismiss()
                        // Handle OK button click
                    }
                    .setCancelable(false)
                    .create()

                alertDialog.show()
            }
        }
    }



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_CODE_MY_DIALOG && resultCode == Activity.RESULT_OK) {
            if (data != null) {//i don't need any data from the intent
                Log.i(TAG, "onActivityResult: testing")
                getLocation()
            }
        }
    }

    private fun checkPermissions(): Boolean {
        Log.i(TAG, "checkPermissions: ")
        return ActivityCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun checkForInternet(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val networkRequest = NetworkRequest.Builder()
            .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
            .build()

        val networkCallback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                Toast.makeText(context, "Internet is available", Toast.LENGTH_SHORT).show()

                // Internet is available
            }

            override fun onLost(network: Network) {
                Toast.makeText(context, "Internet is lost", Toast.LENGTH_SHORT).show()

                // Internet is lost
            }
        }

        connectivityManager.registerNetworkCallback(networkRequest, networkCallback)

        // Check initial network availability
        val networkCapabilities =
            connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        val isInternetAvailable =
            networkCapabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                ?: false

        return isInternetAvailable
    }



    private fun onResponseState(weatherResponse: WeatherResponse) {
    Log.i(TAG, "onResponseState: ")

    _binding.apply {
        todayImg.setImageResource(photos[weatherResponse.currentForecast?.weather?.get(0)?.icon] ?: 0)

        hourlyAdapter.setHourlyForecasts(weatherResponse.hourlyForecast.take(23))
        recHourly.adapter = hourlyAdapter

        dailyAdapter.setDailyForecasts(weatherResponse.dailyForecast.drop(1).take(6))
        recNextDays.adapter = dailyAdapter

        progressBar.visibility = View.GONE
        mainGroup.visibility = View.VISIBLE
        progressBar.pauseAnimation()
    }
}



    override fun onDestroy() {
        super.onDestroy()
        Log.i(TAG, "onDestroy: ")
    }
}




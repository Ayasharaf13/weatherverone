package com.example.weathermate.map

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.SearchView.OnQueryTextListener
import android.widget.Toast
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import com.example.weathermate.R
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.*
import java.io.IOException

class MapFragment : Fragment() , OnMapReadyCallback{
    private val TAG = "MapFragment"
    private lateinit var supportMapFragment: SupportMapFragment
    private lateinit var client: FusedLocationProviderClient
  //  private lateinit var sharedPreferences : SharedPreferences
    private  lateinit var editor :SharedPreferences.Editor
    private lateinit var searchTextField: SearchView
    private lateinit var fab : FloatingActionButton
    private lateinit var map : GoogleMap
    private lateinit var marker : Marker
    val args : MapFragmentArgs by navArgs()

    companion object{
        var isFromMap = false
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
      //  sharedPreferences = getSharedPreferences(requireActivity())
      //  editor = sharedPreferences.edit()
        return inflater.inflate(R.layout.fragment_map, container, false)
    }

    @SuppressLint("MissingPermission", "SuspiciousIndentation")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Inflate the layout for this fragment
        searchTextField = view.findViewById(R.id.tf)
        fab = view.findViewById(R.id.fab)
       // searchTextField.setOnQueryTextListener(object : OnQueryTextListener {
            searchTextField.setOnQueryTextListener(object : OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    val searchResult = query?.trim()
                    if (!searchResult.isNullOrEmpty()) {
                        val geocoder = Geocoder(requireActivity())
                        try {
                            val addresses = geocoder.getFromLocationName(searchResult, 1)
                            if (addresses != null) {
                                if (addresses.isNotEmpty()) {
                                    val address = addresses[0]
                                    val latLng = LatLng(address.latitude, address.longitude)
                                    marker?.remove()
                                    map?.addMarker(MarkerOptions().position(latLng).title(searchResult))
                                    map?.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10f))
                                } else {
                                    Toast.makeText(requireContext(), "No results found", Toast.LENGTH_SHORT).show()
                                }
                            }
                        } catch (e: IOException) {
                            e.printStackTrace()
                            Log.i(TAG, "onQueryTextSubmit: ${e.message}")
                            Toast.makeText(requireContext(), "Error occurred", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(requireContext(), "Enter a search query", Toast.LENGTH_SHORT).show()
                    }


                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }

        })

        client = LocationServices.getFusedLocationProviderClient(requireActivity())
        supportMapFragment =
            childFragmentManager.findFragmentById(R.id.google_maps) as SupportMapFragment
       // getCurrentLocation()


         suspend fun showConfirmationDialog(): Boolean = withContext(Dispatchers.Main) {
            return@withContext suspendCancellableCoroutine { continuation ->
                val dialog = AlertDialog.Builder(requireContext())
                    .setTitle("Confirm location")
                    .setMessage("Are you sure you want to choose this location?")
                    .setPositiveButton("Yes") { dialog, which ->
                        continuation.resume(true) {
                            // This block is executed when the coroutine is cancelled
                        }
                    }
                    .setNegativeButton("No") { dialog, which ->
                        continuation.resume(false) {
                            // This block is executed when the coroutine is cancelled
                        }
                    }
                    .setOnCancelListener {
                        continuation.resume(false) {
                            // This block is executed when the coroutine is cancelled
                        }
                    }
                    .create()

                continuation.invokeOnCancellation {
                    dialog.dismiss()
                }

                dialog.show()
            }
        }


        fun confirmLocation() {
            CoroutineScope(Dispatchers.Main).launch {
                val confirmed = showConfirmationDialog()
                if (confirmed) {
                    val location = marker.position
                    Log.i(TAG, "getCurrentLocation: ${location.longitude} ${location.latitude}")
              //      editor.putString("location", "${location.longitude},${location.latitude}")
                //    editor.apply()

                    val navController = Navigation.findNavController(
                        requireActivity(),
                        R.id.nav_host_fragment_content_main
                    )

                    val action = if (args.isFromSettings) {
                        MapFragmentDirections.actionMapFragmentToNavSettings("${location.longitude},${location.latitude}")
                    } else {
                        isFromMap = true
                        MapFragmentDirections.actionMapFragmentToNavFavs("${location.longitude},${location.latitude}")
                    }

                    navController.navigate(action)
                }
            }
        }



        fab.setOnClickListener {
            confirmLocation()
        }




        supportMapFragment.getMapAsync { googleMap ->
            client.getCurrentLocation(
                Priority.PRIORITY_HIGH_ACCURACY, null
            ).addOnSuccessListener { location: Location? ->
                Log.i(TAG, "getCurrentLocation: success")
                if (location != null) {
                    // Initialize lat and long
                    val latlng = LatLng(location.latitude, location.longitude)

                    // Initialize marker options
                    val markerOptions = MarkerOptions().position(latlng).title("I'm here")

                    // Zoom map
                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latlng, 10f))

                    // Add marker on map
                    marker = googleMap.addMarker(markerOptions)!!

                    googleMap.setOnMapClickListener { latLng ->
                        marker.remove()
                        marker = googleMap.addMarker(
                            MarkerOptions()
                                .position(latLng)
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                                .draggable(false)
                                .snippet("Additional information")
                                .title("Confirm location")

                        )!!
                    }
                }
            }.addOnCanceledListener {
                Log.i(TAG, "getCurrentLocation: failed")
            }
        }








    }
    private fun getSharedPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences("weather_prefs", Context.MODE_PRIVATE)
    }

    override fun onMapReady(p0: GoogleMap) {
        map = p0
    }
}
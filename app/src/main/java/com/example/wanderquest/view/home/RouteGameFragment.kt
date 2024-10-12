package com.example.wanderquest.view.home

import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import androidx.core.app.ActivityCompat
import com.example.wanderquest.databinding.FragmentRouteGameBinding
import com.example.wanderquest.view.base.BaseFragment
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import dagger.hilt.android.AndroidEntryPoint
import android.Manifest
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import com.example.wanderquest.R
import com.example.wanderquest.domain.model.Location
import com.example.wanderquest.domain.model.LocationDto
import com.example.wanderquest.view.home.viewmodel.RouteGameViewModel
import com.example.wanderquest.view.state.ViewState
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import java.text.DecimalFormat
@AndroidEntryPoint
class RouteGameFragment : BaseFragment<FragmentRouteGameBinding>
    (FragmentRouteGameBinding::inflate), OnMapReadyCallback {

    private lateinit var googleMap: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback
    private val markersMap = mutableMapOf<String, Marker>()

    private var previousLocation: android.location.Location? = null

    private val locationsIds  = mutableListOf<String>()
    val routeGameViewModel by viewModels<RouteGameViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setToolBarOptions("Gra", true, false);

        val routeId = arguments?.getString("routeId")
        val locations = arguments?.getParcelableArrayList<Location>("locations") as List<Location>

        for (l in locations) {
            locationsIds.add(l.id)
        }
        setUpMap()

        routeGameViewModel.locations.observe(viewLifecycleOwner) {response ->
            when (response) {
                is ViewState.Success -> {
                    var locations = (routeGameViewModel.locations.value as ViewState.Success<List<LocationDto>>).result
                    addMarkersToMap(locations)

                    var visitedLocationCount : Int = 0
                    for (l in locations) {
                        if (l.done) visitedLocationCount+=1
                    }
                    binding.visitedLocationsText.text = "Liczba odwiedzonych atrkacji ${visitedLocationCount} / ${locations.count()}"

                }
                else -> {

                }
            }
        }

        routeGameViewModel.totalDistance.observe(viewLifecycleOwner) {d ->
            binding.totalDistanceText.text = "${DecimalFormat("#.00").format(d)} km"
        }

        locationRequest = LocationRequest.create().apply {
            interval = 10000
            fastestInterval = 5000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(p0: LocationResult) {
                p0.lastLocation?.let {


                    previousLocation?.let { prevLoc ->
                        val distance = prevLoc.distanceTo(it)
                        routeGameViewModel.updateTotalDistance(distance) // Update the total distance in ViewModel

                        Log.d("Distance", "Distance traveled: $distance meters")
                        Log.d("TotalDistance", "Total distance: ${routeGameViewModel.totalDistance.value} meters")
                    }


                    previousLocation = it



                    val gameLocations = routeGameViewModel.getLocationsValues()
                    for (gameLoc in gameLocations) {
                        if (gameLoc.done ) {
                            markersMap[gameLoc.id]?.let { marker ->
                                marker.remove() // Remove the marker from the map
                                markersMap.remove(gameLoc.id) // Remove from the map reference
                            }
                            continue
                        }

                        val loc = android.location.Location(gameLoc.id)
                        loc.apply {
                            latitude = gameLoc.lat.toDouble()
                            longitude = gameLoc.lng.toDouble()
                        }
                        val distance = it.distanceTo(loc)

                        if (distance < 50) {
                            routeGameViewModel.setLocationDone(gameLoc.id)
                            markersMap[gameLoc.id]?.let { marker ->
                                marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)) // Change to green or any color indicating "done"
                            } ?: Log.e("MarkerError", "Marker not found for ID: ${gameLoc.id}")
                        }
                    }
                }
            }
        }

    }


    fun addMarkersToMap(gameLocations: List<LocationDto>) {
        for (gameLoc in gameLocations) {
            val latLng = LatLng(gameLoc.lat.toDouble(), gameLoc.lng.toDouble())
            val marker = googleMap.addMarker(MarkerOptions().position(latLng).title(gameLoc.name))
            if (marker != null) {
                markersMap[gameLoc.id] = marker
            }
        }
    }
    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        routeGameViewModel.loadRouteLocations(locationsIds)
        requestForPermissionAndEnableLocation()





        if (checkPermission()) {
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            )
            moveMapToUserLocalization()
        }
    }

    private fun setUpMap() {
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }
    private fun moveMapToUserLocalization() {
        if (checkPermission()) {
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location ->
                    if (location != null) {
                        val userLatLng = LatLng(location.latitude, location.longitude)
                        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLatLng, DEFAULT_ZOOM))


                    }
                }
        }
    }

    private fun enableLocation() {
        if (!checkPermission()) return
        googleMap.isMyLocationEnabled = true
    }

    private fun requestForPermissionAndEnableLocation() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            enableLocation()
        } else {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                enableLocation()
            }
        }
    }

    private fun checkPermission() : Boolean {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return false
        }
        return true
    }



    override fun onDestroyView() {
        super.onDestroyView()
        if (fusedLocationClient != null) {
            fusedLocationClient.removeLocationUpdates(locationCallback)

        }
    }

    override fun onResume() {
        super.onResume()

    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
        private const val DEFAULT_ZOOM = 15f
    }


}
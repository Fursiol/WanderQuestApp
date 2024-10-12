package com.example.wanderquest.view.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.wanderquest.R
import com.example.wanderquest.databinding.FragmentDetailsCityEventBinding
import com.example.wanderquest.databinding.FragmentWeatherBinding
import com.example.wanderquest.domain.model.cityevents.toDomain
import com.example.wanderquest.view.base.BaseFragment
import com.example.wanderquest.view.state.ViewState
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import okhttp3.internal.wait


class DetailsCityEventFragment : BaseFragment<FragmentDetailsCityEventBinding>
    (FragmentDetailsCityEventBinding::inflate), OnMapReadyCallback {

    private lateinit var googleMap: GoogleMap
    private lateinit var coordinates: LatLng

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setToolBarOptions("Szczegóły wydarzenia", true, false);

        val latitude = arguments?.getString("event_coord_lat")
        val longitute = arguments?.getString("event_coord_long")
        coordinates = LatLng(latitude!!.toDouble(), longitute!!.toDouble())

        navController = findNavController()

        binding.layoutDetailCityEvent.visibility = View.VISIBLE
        binding.detailsCityEventTitle.text = arguments?.getString("event_title")
        binding.detailsCityEventTime.text = "Czas trwania: ".plus(arguments?.getString("event_time"))

        if(arguments?.getString("event_desc") == "Sourced from predicthq.com"){
            binding.detailsCityEventDescription.text = "Opis: ".plus("Brak danych.")
        } else {
            binding.detailsCityEventDescription.text = "Opis: ".plus(arguments?.getString("event_desc"))
        }
        binding.detailsCityEventCategory.text = "Kategoria :  ".plus(arguments?.getString("event_category"))

        setUpMap()
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map

        googleMap.clear()
        googleMap.addMarker(MarkerOptions().position(coordinates).title(arguments?.getString("event_title")))

        val cameraPosition = CameraPosition.Builder()
            .target(coordinates)
            .zoom(15f)
            .build()

        val cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition)

        googleMap.moveCamera(cameraUpdate)
    }

    private fun setUpMap() {
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }
}
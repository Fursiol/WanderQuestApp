package com.example.wanderquest.view.home
import android.os.Bundle
import android.view.Gravity
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.PopupWindow
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.example.wanderquest.R
import com.example.wanderquest.databinding.FilterPopupBinding
import com.example.wanderquest.databinding.FragmentCityEventBinding
import com.example.wanderquest.domain.model.cityevents.CityEvent
import com.example.wanderquest.domain.model.cityevents.toDomain
import com.example.wanderquest.view.base.BaseFragment
import com.example.wanderquest.view.home.adapter.CityEventsAdapter
import com.example.wanderquest.view.state.ViewState
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import dagger.hilt.android.AndroidEntryPoint
import java.lang.NumberFormatException

@AndroidEntryPoint
class CityEventsFragment : BaseFragment<FragmentCityEventBinding>
    (FragmentCityEventBinding::inflate), OnMapReadyCallback {

    private val adapter by lazy { CityEventsAdapter() }
    private lateinit var googleMap: GoogleMap
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        eventsViewModel.fetchCityEvents("6zSYPyk52DBw_KEewInVqg")
        observeEvents()
        setUpMap()
        initAdapter()

        navController = findNavController()

        setToolBarOptions("Wydarzenia w mieście", true, false);

    }
    override fun onMapReady(map: GoogleMap) {
        googleMap = map
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_city_event, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId)  {
            R.id.action_filter -> {
                showFilterPopup()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun observeEvents() {
        eventsViewModel.events.observe(viewLifecycleOwner) {response ->
            when (response) {
                is ViewState.Success -> {

                    binding.layoutCityEvents.visibility = View.VISIBLE
                    binding.loading.visibility = View.GONE

                    val data = response.result.toDomain()

                    handleListAdapter(data)
                    addMarkersToMap(data)
                    if (data.isNotEmpty()){
                        zoomMap(data.first().coordinates, 10f)
                    }

                }
                is ViewState.Error -> {
                    var item = response.exception
                }
                else -> {

                    binding.layoutCityEvents.visibility = View.GONE
                    binding.loading.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun setUpMap() {
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    private fun initAdapter() {
        adapter.eventListClicked = {res ->
            zoomMap(res.coordinates)
        }

        adapter.eventDetailsClicked = {res ->
            navController.navigate(R.id.action_cityEventsFragment_to_detailCityEventFragment,
                bundleOf("event_title" to res.title,
                    "event_desc" to res.description,
                    "event_time" to res.dateParsed,
                    "event_category" to res.category,
                    "event_coord_lat" to res.coordinates.latitude.toString(),
                    "event_coord_long" to res.coordinates.longitude.toString()),
                null)
        }

        binding.rvCityItems.adapter = adapter
    }

    private fun handleListAdapter(cityEvents: List<CityEvent>) {
        adapter.submitList(cityEvents)
    }


    private fun addMarkersToMap(cityEvents: List<CityEvent> ) {
        googleMap.clear()
        for (event in cityEvents) {
            googleMap.addMarker(MarkerOptions().position(event.coordinates).title(event.title))
        }
    }

    private fun zoomMap(coordinates : LatLng, zoomLevel : Float = 15f) {
        if (!::googleMap.isInitialized) {
            return
        }

        val cameraPosition = CameraPosition.Builder()
            .target(coordinates)
            .zoom(zoomLevel)
            .build()

        val cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition)
        googleMap.moveCamera(cameraUpdate)
    }

    private fun showFilterPopup() {
        val popupBinding = FilterPopupBinding.inflate(layoutInflater)
        val popupWindow = PopupWindow(popupBinding.root, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        popupWindow.showAtLocation(requireView(), Gravity.CENTER, 0, 0)
        popupWindow.isFocusable = true
        popupWindow.update();

        setUpFilterPopUpValues(popupBinding)

        popupBinding.btnApply.setOnClickListener {

            try {
                var limit : Int =  Integer.parseInt(popupBinding.etFilterLimit.text.toString())
                var category = eventsViewModel.categories.entries.first { it.value ==  popupBinding.spCategory.selectedItem.toString()}.key

                if (limit <= 0) throw NumberFormatException()

                eventsViewModel.setLimit(limit)
                eventsViewModel.setCategory(category)

                eventsViewModel.fetchCityEvents("6zSYPyk52DBw_KEewInVqg")
                popupWindow.dismiss()
            }
            catch (ex : NumberFormatException) {
                Toast.makeText(context, "Nieprawidłowy format danych", Toast.LENGTH_LONG).show()
            }
        }

        popupBinding.btnCancel.setOnClickListener {
            popupWindow.dismiss()
        }
    }

    private fun setUpFilterPopUpValues(popupBinding : FilterPopupBinding) {
        popupBinding.etFilterLimit.setText(eventsViewModel.limit.value.toString())

        val adapterCategory = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, eventsViewModel.categories.values.toList())
        adapterCategory.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        popupBinding.spCategory.adapter = adapterCategory

        var index = eventsViewModel.categories.entries.indexOfFirst { it.key ==  eventsViewModel.category.value}
        if (index == -1) index = 0
        popupBinding.spCategory.setSelection(index)
    }


}
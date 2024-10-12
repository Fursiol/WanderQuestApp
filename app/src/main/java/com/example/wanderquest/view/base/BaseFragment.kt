package com.example.wanderquest.view.base

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.example.wanderquest.R
import androidx.viewbinding.ViewBinding
import com.example.wanderquest.view.admin.locations.viewmodel.LocationViewModel
import com.example.wanderquest.view.admin.quests.viewmodel.QuestViewModel
import com.example.wanderquest.view.admin.routes.viewmodel.RouteViewModel

import com.example.wanderquest.view.auth.viewmodel.AuthViewModel
import com.example.wanderquest.view.home.viewmodel.CityEventsViewModel
import com.example.wanderquest.view.home.viewmodel.WeatherViewModel
import okhttp3.Route

abstract class BaseFragment<VB: ViewBinding> (
    private val bindingInflater: (inflater: LayoutInflater) -> VB
) : Fragment() {

    val authViewModel by viewModels<AuthViewModel>()
    val eventsViewModel by viewModels<CityEventsViewModel>()
    val weatherViewModel by viewModels<WeatherViewModel>()
    val locationViewModel by viewModels<LocationViewModel>()
    val questViewModel by viewModels<QuestViewModel>()
    val routeViewModel by viewModels<RouteViewModel>()

    private var _binding: VB? = null
    lateinit var navController: NavController
    val binding: VB
        get() = _binding as VB

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = bindingInflater.invoke(inflater)
        return binding.root
    }

    fun Toolbar.setupToolbar(title : Int, withNav : Boolean = true, withMenu : Boolean = true) {
        if (withNav) {
            val appBarConfiguration = AppBarConfiguration(navController.graph)
            this.setupWithNavController(navController, appBarConfiguration)
            this.setNavigationIcon(R.drawable.ic_arrow_left)
        }

        if (withMenu) {
            (activity as AppCompatActivity).setSupportActionBar(this)
            setHasOptionsMenu(true)
        }
        this.setTitle(title)
    }


    fun setToolBarOptions(title: String, withMenu : Boolean, hide : Boolean = false) {
        var actionBar = (activity as AppCompatActivity).supportActionBar

        if (actionBar != null) {
            actionBar.title = title
            if (withMenu) {
                setHasOptionsMenu(true)
            }

            if (hide) {
                actionBar.hide()
            } else {
                actionBar.show()
            }
        }
    }

    fun showShortToast(message: Int) {
        Toast.makeText(requireContext(), getString(message), Toast.LENGTH_SHORT).show()
    }

    fun showShortToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
}
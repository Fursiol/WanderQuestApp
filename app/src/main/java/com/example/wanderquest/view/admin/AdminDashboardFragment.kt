package com.example.wanderquest.view.admin

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.wanderquest.R
import com.example.wanderquest.databinding.FragmentAdminDashboardBinding
import com.example.wanderquest.databinding.FragmentLoginBinding
import com.example.wanderquest.view.base.BaseFragment

class AdminDashboardFragment : BaseFragment<FragmentAdminDashboardBinding>(
    FragmentAdminDashboardBinding::inflate) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = findNavController()

        setToolBarOptions("Panel administracyjny", true)

        binding.routeManagementButton.setOnClickListener {
            navController.navigate(R.id.manageRoutesFragment)
        }

        binding.locationManagementButton.setOnClickListener {
            navController.navigate(R.id.manageLocationsFragment)
        }

        binding.questManagementButton.setOnClickListener {
            navController.navigate(R.id.manageQuestsFragment)
        }
    }

}
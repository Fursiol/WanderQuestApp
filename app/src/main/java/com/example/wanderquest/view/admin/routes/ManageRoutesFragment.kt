package com.example.wanderquest.view.admin.routes

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.wanderquest.R
import com.example.wanderquest.databinding.FragmentManageQuestsBinding
import com.example.wanderquest.databinding.FragmentManageRoutesBinding
import com.example.wanderquest.view.admin.quests.adapter.QuestAdapter
import com.example.wanderquest.view.admin.routes.adapter.RouteAdapter
import com.example.wanderquest.view.base.BaseFragment
import com.example.wanderquest.view.state.ViewState
import dagger.hilt.android.AndroidEntryPoint

private lateinit var routeAdapter: RouteAdapter

@AndroidEntryPoint
class ManageRoutesFragment : BaseFragment<FragmentManageRoutesBinding>(
    FragmentManageRoutesBinding::inflate) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = findNavController()

        setToolBarOptions("Zarządzanie trasami", true)

        routeAdapter = RouteAdapter()

        binding.addRouteButton.setOnClickListener {
            navController.navigate(R.id.addRouteFragment)
        }

        binding.routesRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = routeAdapter
        }

        routeViewModel.loadRoutes()
        routeViewModel.routes.observe(viewLifecycleOwner) {viewState->
            when(viewState) {
                is ViewState.Loading -> {}
                is ViewState.Success -> {
                    val routeList = viewState.result
                    routeAdapter.submitList(routeList)
                }
                is ViewState.Error -> {
                    Toast.makeText(requireContext(), "Błąd: ${viewState.exception.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }

        routeAdapter.routeEditClicked = { route ->
            navController.navigate(R.id.editRouteFragment, bundleOf(
                "routeID" to route.id,
                "locations" to ArrayList(route.locations),
                "routeName" to route.name)
            )
        }
    }
}
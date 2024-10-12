package com.example.wanderquest.view.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.wanderquest.R
import com.example.wanderquest.databinding.FragmentMainBinding
import com.example.wanderquest.view.base.BaseFragment
import com.example.wanderquest.view.state.ViewState
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainFragment : BaseFragment<FragmentMainBinding>
    (FragmentMainBinding::inflate) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.loading.visibility = View.GONE
        navController = findNavController()
        setToolBarOptions("Strona główna", true, false);

        authViewModel.getCurrentUserData()

        binding.btnPlay.setOnClickListener {
            navController.navigate(R.id.action_mainFragment_to_chooseRouteFragment)
        }

        authViewModel.currentUserData.observe(viewLifecycleOwner) {
            when (it)
            {
                is ViewState.Success -> {
                    binding.userName.text = "Witaj ${it.result.name}!"
                    binding.loading.visibility = View.GONE
                }
                is ViewState.Error -> {
                    binding.loading.visibility = View.VISIBLE
                }
                is ViewState.Loading -> {
                    binding.loading.visibility = View.VISIBLE
                }

            }
        }

    }



}
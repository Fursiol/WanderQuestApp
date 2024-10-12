package com.example.wanderquest.view.auth

import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.example.wanderquest.R
import com.example.wanderquest.databinding.FragmentRegisterBinding

import com.example.wanderquest.utlis.Helpers.isEmailValid
import com.example.wanderquest.view.base.BaseFragment
import com.example.wanderquest.view.state.ViewState
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RegisterFragment : BaseFragment<FragmentRegisterBinding>(FragmentRegisterBinding::inflate) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.loading.visibility = View.GONE

        navController = findNavController()
        setToolBarOptions("Rejestracja", true, true);

        authViewModel.newLoggedInUser.observe(viewLifecycleOwner) {
            when (it)
            {
                is ViewState.Success -> {
                    binding.errorTv.visibility = View.GONE
                    binding.loading.visibility = View.GONE
                    binding.registerFragment.visibility = View.VISIBLE

                    val navigationOptions = NavOptions.Builder()
                        .setPopUpTo(R.id.registerFragment, true)
                        .setPopUpTo(R.id.loginFragment, true)
                        .build()

                    navController.navigate(R.id.action_registerFragment_to_loginFragment, null, navigationOptions)
                }
                is ViewState.Error -> {
                    binding.errorTv.visibility = View.VISIBLE
                    binding.loading.visibility = View.GONE
                    binding.errorTv.text = it.exception.message
                }
                else -> Unit
            }

        }

        binding.txtLogin.setOnClickListener {
            navController.popBackStack()
        }

        binding.btnRegister.setOnClickListener {

            val name = binding.nameInputEditText.text.toString()
            val email = binding.registerEmailInputEditText.text.toString()
            val password = binding.registerPasswordInputEditText.text.toString()
            val confirmPassword = binding.registerConfirmPasswordInputEditText.text.toString()

            if (name.trim().isEmpty()) {
                binding.errorTv.visibility = View.VISIBLE
                binding.errorTv.text = getString(R.string.fill_all_fields)
                return@setOnClickListener
            }

            if (!email.isEmailValid()) {
                binding.errorTv.visibility = View.VISIBLE
                binding.errorTv.text = getString(R.string.invalid_email_address)
                return@setOnClickListener
            }

            if (password != confirmPassword) {
                binding.errorTv.visibility = View.VISIBLE
                binding.errorTv.text = getString(R.string.invalid_password)
                return@setOnClickListener
            }

            binding.loading.visibility = View.VISIBLE
            binding.errorTv.visibility = View.GONE
            binding.registerFragment.visibility = View.GONE
            authViewModel.registerUserWihEmailAndPassword(name, email, password)
        }

    }

}
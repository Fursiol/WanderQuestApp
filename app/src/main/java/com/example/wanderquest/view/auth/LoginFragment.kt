package com.example.wanderquest.view.auth

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.example.wanderquest.MainActivity
import com.example.wanderquest.R
import com.example.wanderquest.databinding.FragmentLoginBinding
import com.example.wanderquest.di.FirebaseRepoModule
import com.example.wanderquest.utlis.Helpers.isEmailValid
import com.example.wanderquest.view.base.BaseFragment
import com.example.wanderquest.view.state.ViewState
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginFragment : BaseFragment<FragmentLoginBinding>(
    FragmentLoginBinding::inflate
) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.loading.visibility = View.GONE
        navController = findNavController()

        setToolBarOptions("Logowanie", true, true);

        binding.txtSignUp.setOnClickListener {
            navController.navigate(R.id.action_loginFragment_to_registerFragment)
        }

        binding.btnLogin.setOnClickListener {
            val emailAddress = binding.loginEmailInputEditText.text.toString()
            val password = binding.loginPasswordInputEditText.text.toString()

            if (!emailAddress.isEmailValid()) {
                binding.loginEmailInputLayout.isHelperTextEnabled = true
                binding.loginEmailInputLayout.helperText = "Nieprawidłowy adres e-mail"
            }
            if (password.isEmpty()) {
                binding.loginPasswordInputLayout.isHelperTextEnabled = true
                binding.loginPasswordInputLayout.helperText = "Wpisz hasło"
            }

            if (emailAddress.isEmailValid() && password.isNotEmpty()) {
                binding.loading.visibility = View.VISIBLE
                authViewModel.logInWithEmailAndPassword(emailAddress, password)
            }
        }

        authViewModel.newLoggedInUser.observe(viewLifecycleOwner) {
            when(it)
            {
                is ViewState.Success -> {
                    binding.errorTv.visibility = View.GONE
                    binding.loading.visibility = View.GONE
                    binding.loginEmailInputLayout.isHelperTextEnabled = false
                    binding.loginPasswordInputLayout.isHelperTextEnabled = false

                    val navigationOptions = NavOptions.Builder()
                        .setPopUpTo(R.id.loginFragment, true)
                        .build()

                    //Sprawdzenie czy użytkownik jest adminem
                    val user = FirebaseAuth.getInstance().currentUser
                    user?.let {
                        val uid = user.uid
                        val database = FirebaseRepoModule.provideFirebaseDatabase()
                        val usersRef = database.getReference("users")

                        usersRef.child(uid).get().addOnSuccessListener { dataSnapshot ->
                            if (dataSnapshot.exists()) {
                                // Uzyskanie danych użytkownika
                                val userData = dataSnapshot.value as Map<String, Any>
                                val isAdmin = userData["admin"] as Boolean? ?: false

                                // Przekazanie do aktywności
                                (activity as? MainActivity)?.isAdmin = isAdmin

                                navController.navigate(
                                    R.id.action_loginFragment_to_mainFragment,
                                    null,
                                    navigationOptions
                                )
                            }
                        }
                    }
                }

                is ViewState.Error -> {
                    binding.loading.visibility = View.GONE
                    binding.errorTv.visibility = View.VISIBLE
                    binding.errorTv.text = it.exception.message
                    binding.loginEmailInputLayout.isHelperTextEnabled = false
                    binding.loginPasswordInputLayout.isHelperTextEnabled = false
                }
                else -> Unit
            }
        }
    }
}
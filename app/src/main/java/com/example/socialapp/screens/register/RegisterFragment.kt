package com.example.socialapp.screens.register


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.example.socialapp.databinding.FragmentRegisterBinding
import com.google.android.material.snackbar.Snackbar


class RegisterFragment : Fragment() {

    private lateinit var binding: FragmentRegisterBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentRegisterBinding.inflate(inflater, container, false)

        val viewmodel = ViewModelProviders.of(this).get(RegisterViewModel::class.java)

        binding.viewModel = viewmodel

        viewmodel.registerSuccess.observe(viewLifecycleOwner, Observer {
            when (it) {
                //Login success
                true -> navigateToLoginScreen()
                //Login fail
                false -> showRegistrationFailedSnackbar()
            }
        })

        return binding.root
    }

    /**    Navigates user to Login Screen    **/

    private fun navigateToLoginScreen() {
        val direction =
            RegisterFragmentDirections.actionRegisterFragmentToLoginFragment()
        findNavController().navigate(direction)
    }

    /**    Shows SnackBar with proper message about failed registration attempt    **/

    private fun showRegistrationFailedSnackbar(message: String = "Registration failed") {
        val snackbar =
            Snackbar.make(
                activity!!.findViewById(android.R.id.content),
                message,
                Snackbar.LENGTH_SHORT
            )
        snackbar.show()
    }

}

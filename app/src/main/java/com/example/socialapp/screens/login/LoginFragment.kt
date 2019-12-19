package com.example.socialapp.screens.login


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.socialapp.common.hideKeyboard
import com.example.socialapp.common.showSnack
import com.example.socialapp.databinding.FragmentLoginBinding


class LoginFragment : Fragment() {

    private lateinit var binding: FragmentLoginBinding

    private val viewmodel: LoginViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.lifecycleOwner = this
        binding.viewModel = viewmodel

        viewmodel.isLoading.observe(viewLifecycleOwner, Observer { isLoading ->
            if (isLoading) {
                showProgressBar()
            } else {
                hideProgressBar()
            }
        })

        viewmodel.isOnLoginSuccessful.observe(viewLifecycleOwner, Observer {
            when (it) {
                //Login success - navigate to main screen
                true -> navigateToMainScreen()
                //Login failed - show error message
                false -> showSnack("Failed to sign in")
            }
        })

        binding.tvCreateAccountRedirect.setOnClickListener {
            navigateToRegisterScreen()
        }

        binding.btnLogin.setOnClickListener {
            hideKeyboard()
            viewmodel.login()
        }

    }

    // Hides login button and shows progress bar
    private fun showProgressBar() {
        binding.btnLogin.visibility = View.GONE
        binding.pbLogin.visibility = View.VISIBLE
    }

    // Shows login button and hides progress bar
    private fun hideProgressBar() {
        binding.btnLogin.visibility = View.VISIBLE
        binding.pbLogin.visibility = View.GONE
    }

    private fun navigateToRegisterScreen() {
        val direction = LoginFragmentDirections.actionLoginFragmentToRegisterFragment()
        findNavController().navigate(direction)
    }

    private fun navigateToMainScreen() {
        val direction = LoginFragmentDirections.actionLoginFragmentToMainFragment()
        findNavController().navigate(direction)
    }
}

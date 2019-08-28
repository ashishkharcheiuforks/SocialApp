package com.example.socialapp.screens.login


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.example.socialapp.databinding.FragmentLoginBinding
import com.google.android.material.snackbar.Snackbar


class LoginFragment : Fragment() {

    private lateinit var binding: FragmentLoginBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentLoginBinding.inflate(inflater, container, false)

        val viewmodel = ViewModelProviders.of(this).get(LoginViewModel::class.java)

        binding.viewModel = viewmodel

        binding.tvCreateAccountRedirect.setOnClickListener {
            navigateToRegisterScreen()
        }

        viewmodel.onLoginSuccess.observe(viewLifecycleOwner, Observer {
            when (it) {
                //Login success
                true -> navigateToMainScreen()
                //Login fail
                false -> showLoginFailedSnackbar()
            }
        })

        return binding.root
    }

    /**    Navigates user to Register Screen    **/

    private fun navigateToRegisterScreen() {
        val direction = LoginFragmentDirections.actionLoginFragmentToRegisterFragment()
        findNavController().navigate(direction)
    }

    /**    Navigates user to Main Screen    **/

    private fun navigateToMainScreen() {
        val direction = LoginFragmentDirections.actionLoginFragmentToMainFragment()
        findNavController().navigate(direction)
    }

    private fun showLoginFailedSnackbar(message: String = "Sign in failed") {
        val snackbar =
            Snackbar.make(
                activity!!.findViewById(android.R.id.content),
                message,
                Snackbar.LENGTH_LONG
            )
        snackbar.show()
    }

}

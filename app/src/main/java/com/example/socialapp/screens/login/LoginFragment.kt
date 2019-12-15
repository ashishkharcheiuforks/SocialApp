package com.example.socialapp.screens.login


import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.socialapp.R
import com.example.socialapp.databinding.FragmentLoginBinding
import com.google.android.material.snackbar.Snackbar


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
                false -> showLoginFailedSnackbar()
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

    private fun showLoginFailedSnackbar(message: String = "Failed to sign in") {
        Snackbar
            .make(
                activity!!.findViewById(android.R.id.content),
                message,
                Snackbar.LENGTH_LONG
            )
            .show()
    }

    // Extension functions for hiding keyboard
    // TODO("DEV"): Move declarations to the global level inside the app
    fun Fragment.hideKeyboard() {
        view?.let { activity?.hideKeyboard(it) }
    }

    fun Activity.hideKeyboard() {
        hideKeyboard(currentFocus ?: View(this))
    }

    fun Context.hideKeyboard(view: View) {
        val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }



}

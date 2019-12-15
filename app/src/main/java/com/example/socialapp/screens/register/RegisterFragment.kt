package com.example.socialapp.screens.register


import android.app.Activity
import android.app.DatePickerDialog
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
import com.example.socialapp.databinding.FragmentRegisterBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.Timestamp
import timber.log.Timber
import java.util.*


class RegisterFragment : Fragment() {

    private lateinit var binding: FragmentRegisterBinding

    private lateinit var dpd: DatePickerDialog

    private val viewModel: RegisterViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        viewModel.isRegisterSuccessful.observe(viewLifecycleOwner, Observer {
            when (it) {
                //Login success
                true -> {
                    navigateToMainScreen()
                }
            }
        })

        viewModel.displayErrorMessage.observe(viewLifecycleOwner, Observer {
            showRegistrationFailedSnackbar(it)
        })

        binding.etRegisterDateOfBirth.setOnClickListener { showDatePickerDialog() }

        binding.btnRegister.setOnClickListener {
            hideKeyboard()
            viewModel.createAccount()
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        /**
         * If date picker dialog was opened on configuration change
         * it is being dismissed to prevent activity leaking window by WindowManager
         * */
        //Checks if late init var is initialized
        if (::dpd.isInitialized) {
            if (dpd.isShowing) {
                Timber.d("isShowing: ${dpd.isShowing} | Date Picker Dialog Dismissed ")
                dpd.dismiss()
            }
        }
    }

    private fun navigateToMainScreen() {
        val direction =
            RegisterFragmentDirections.actionRegisterFragmentToMainFragment()
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

    //TODO: Do it right, you know how to do it now
    /**
     *   NOTE: Showing date picker this way needs dismiss manual handling in
     *   onDestroy() if it is currently showed in order to eliminate activity window leak
     */
    private fun showDatePickerDialog() {

        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val dayOfMonth = c.get(Calendar.DAY_OF_MONTH)
        dpd = DatePickerDialog(
            context!!,
            DatePickerDialog.OnDateSetListener { _, y, m, d ->
                c.set(Calendar.YEAR, y)
                c.set(Calendar.MONTH, m)
                c.set(Calendar.DAY_OF_MONTH, d)
                c.set(Calendar.HOUR_OF_DAY, 0)
                c.set(Calendar.MINUTE, 0)
                c.set(Calendar.SECOND, 0)
                val date = c.time
                val timestamp = Timestamp(date)
                viewModel.setDate(timestamp)
            }, year, month, dayOfMonth
        )
        dpd.datePicker.maxDate = Calendar.getInstance().timeInMillis
        dpd.show()
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
        val inputMethodManager =
            getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }
}

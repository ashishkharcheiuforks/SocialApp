package com.example.socialapp.screens.editprofile


import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.example.socialapp.AuthenticatedNestedGraphViewModel
import com.example.socialapp.DatePickerFragment
import com.example.socialapp.R
import com.example.socialapp.User
import com.example.socialapp.databinding.FragmentEditProfileBinding
import java.util.*


class EditProfileFragment : Fragment(), DatePickerDialog.OnDateSetListener {

    private val PICK_IMAGE_REQUEST = 71

    private lateinit var binding: FragmentEditProfileBinding

    private lateinit var editProfileViewModel: EditProfileViewmodel


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentEditProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupToolbar()

        binding.lifecycleOwner = this

        //Initialize Viewmodels
        val nestedGraphViewModel: AuthenticatedNestedGraphViewModel by navGraphViewModels(R.id.authenticated_nested_graph)
        editProfileViewModel = ViewModelProviders.of(this).get(EditProfileViewmodel::class.java)
        binding.editViewModel = editProfileViewModel

        //Set Observers to the observables
        nestedGraphViewModel.user.observe(viewLifecycleOwner, Observer<User> { binding.user = it })

        //Setting OnClick Listeners
        binding.editProfileChooseImage.setOnClickListener { launchGallery() }
        binding.etEditProfileDateOfBirth.setOnClickListener { showDatePickerDialog(this) }

        binding.btnSaveChangesPersonalInfo.setOnClickListener {
            val fName = binding.etEditProfileFirstName.text.toString()
            val nickname = binding.etEditProfileNickname.text.toString()
            val dateOfBirth = binding.etEditProfileDateOfBirth.text.toString()
            saveChanges(fName, nickname, dateOfBirth)
        }


    }

    override fun onDateSet(p0: DatePicker?, year: Int, monthOfYear: Int, dayOfMonth: Int) {
        val c = Calendar.getInstance()
        c.set(Calendar.YEAR, year)
        c.set(Calendar.MONTH, monthOfYear)
        c.set(Calendar.DAY_OF_MONTH, dayOfMonth)
        val date = "${dayOfMonth}/${monthOfYear + 1}/${year}"
        binding.etEditProfileDateOfBirth.setText(date)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            //Check if picture was successfully given back from the gallery
            if (data == null || data.data == null) {
                return
            }
            val pictureUri = data.data
            editProfileViewModel.loadPicture(pictureUri!!)
        }
    }

    private fun launchGallery() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST)
    }

    private fun setupToolbar() {
        val navController = findNavController()
        val appBarConfiguration = AppBarConfiguration(navController.graph)
        binding.toolbar.setupWithNavController(navController, appBarConfiguration)
    }

    private fun showDatePickerDialog(dateSetListener: DatePickerDialog.OnDateSetListener) {
        val datePickerDialog = DatePickerFragment(dateSetListener)
        datePickerDialog.show(activity!!.supportFragmentManager, "datePicker")
    }

    private fun saveChanges(firstName: String, nickname: String, dateOfBirth: String) {
        editProfileViewModel.updateUserProfileInfo(firstName, nickname, dateOfBirth)
    }

}

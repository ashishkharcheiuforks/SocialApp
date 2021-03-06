package com.example.socialapp.screens.editprofile


import android.app.Activity
import android.app.DatePickerDialog
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.example.socialapp.AuthenticatedNestedGraphViewModel
import com.example.socialapp.R
import com.example.socialapp.common.*
import com.example.socialapp.databinding.FragmentEditProfileBinding
import com.google.firebase.Timestamp
import org.koin.android.viewmodel.ext.android.viewModel
import timber.log.Timber
import java.util.*


class EditProfileFragment : Fragment() {

    private lateinit var binding: FragmentEditProfileBinding

    private val viewModel: EditProfileViewModel by viewModel()
    private val nestedGraphViewModel: AuthenticatedNestedGraphViewModel by navGraphViewModels(R.id.authenticated_graph)

    private lateinit var dpd: DatePickerDialog

    private var imageUri: Uri? = null

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

        binding.apply {
            lifecycleOwner = this@EditProfileFragment
            vm = viewModel
        }

        setupToolbar()

        //Set Observers to the observables
        nestedGraphViewModel.user.observe(viewLifecycleOwner) { binding.user = it }

        // Shows message indicating update result
        viewModel.updateFinishedMessage.observe(viewLifecycleOwner) { showToast(it) }

        // Setting On click listeners
        binding.btnChooseImageGallery.setOnClickListener { launchGallery() }
        binding.btnChooseImageCamera.setOnClickListener { checkPermissionAndOpenCamera() }
        binding.etEditProfileDateOfBirth.setOnClickListener { showDatePickerDialog() }

        binding.btnSaveChanges.setOnClickListener {
            hideKeyboard()
            viewModel.updateUserProfileInfo()
        }

        // Goes to previous fragment from backstack
        binding.btnCancelChanges.setOnClickListener {
            findNavController().popBackStack()
        }


    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        //Check if picture was successfully retrieved from the gallery
        if (data == null || data.data == null) {
            return
        }
        // Image from gallery
        if (requestCode == REQUEST_PICK_IMAGE && resultCode == Activity.RESULT_OK) {
            val pictureUri = data.data
            viewModel.loadedImageUri.value = pictureUri.toString()
        }
        // Image from camera
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            imageUri?.let { viewModel.loadedImageUri.value = it.toString() }
        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            CAMERA_PERMISSION_CODE -> {
                // Not all permissions granted
                if (grantResults.isEmpty()
                    || grantResults[0] != PackageManager.PERMISSION_GRANTED
                    || grantResults[1] != PackageManager.PERMISSION_GRANTED
                ) {
                    showToast("Unsufficient Permissions")
                } else {
                    // All permissions granted
                    openCamera()
                }
            }
        }

    }

    override fun onDestroy() {
        Timber.i("onDestroy() called")
        super.onDestroy()
        /**
         * If date picker dialog was opened on configuration change
         * it is being dismissed to prevent activity leaking window by WindowManager
         * */
        //Checks if lateinit variable is initialized
        if (::dpd.isInitialized) {
            if (dpd.isShowing) {
                Timber.d("isShowing: ${dpd.isShowing} | Date Picker Dialog Dismissed ")
                dpd.dismiss()
            }
        }
    }

    /**    Opens gallery for picking image    **/
    private fun launchGallery() {
        Intent().also {
            it.type = "image/*"
            it.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(Intent.createChooser(it, "Select Picture"), REQUEST_PICK_IMAGE)
        }
    }

    private fun setupToolbar() {
        val navController = findNavController()
        val appBarConfiguration = AppBarConfiguration(navController.graph)
        binding.toolbar.setupWithNavController(navController, appBarConfiguration)
    }


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
                viewModel.dateOfBirth.value = timestamp
            }, year, month, dayOfMonth
        )
        dpd.datePicker.maxDate = c.timeInMillis
        dpd.show()
    }

    private fun checkPermissionAndOpenCamera() {
        val permission = arrayOf(
            android.Manifest.permission.CAMERA,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        if (activity!!.checkSelfPermission(permission[0]) == PackageManager.PERMISSION_DENIED ||
            activity!!.checkSelfPermission(permission[1]) == PackageManager.PERMISSION_DENIED
        ) {
            // permissions not sufficient - request permissions
            requestPermissions(permission, CAMERA_PERMISSION_CODE)
        } else {
            openCamera()
        }
    }

    private fun openCamera() {
        val values = ContentValues().apply {
            put(MediaStore.Images.Media.TITLE, "New Picture")
            put(MediaStore.Images.Media.DESCRIPTION, "From the camera")
        }

        imageUri =
            context!!.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)

        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also {
            it.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
            startActivityForResult(it, REQUEST_IMAGE_CAPTURE)
        }
    }
}

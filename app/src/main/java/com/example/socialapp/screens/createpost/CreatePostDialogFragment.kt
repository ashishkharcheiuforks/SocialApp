package com.example.socialapp.screens.createpost

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.navGraphViewModels
import com.example.socialapp.AuthenticatedNestedGraphViewModel
import com.example.socialapp.R
import com.example.socialapp.databinding.DialogCreatePostBinding
import timber.log.Timber

//class CreatePostDialogFragment : DialogFragment(), Toolbar.OnMenuItemClickListener {
class CreatePostDialogFragment : DialogFragment() {

    private lateinit var binding: DialogCreatePostBinding

    private val viewModel: CreatePostViewModel by viewModels()

    private val nestedGraphViewModel: AuthenticatedNestedGraphViewModel by navGraphViewModels(
        R.id.authenticated_graph
    )

    private val REQUEST_PICK_IMAGE = 71


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.AppTheme_FullScreenDialog)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DialogCreatePostBinding.inflate(inflater, container, false)
        binding.toolbar.navigationIcon!!.setTint(Color.WHITE)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.lifecycleOwner = this
        binding.viewmodel = viewModel

        setupToolbar()

        viewModel.postImage.observe(this@CreatePostDialogFragment, Observer {
            if (it != null) { showImagePreview() } else { hideImagePreview() }
        })

        nestedGraphViewModel.user.observe(viewLifecycleOwner, Observer { binding.user = it })

        binding.btnAddPicture.setOnClickListener { openGallery() }

    }

    private fun showImagePreview(){
        binding.ivLoadedPicture.setImageURI(viewModel.postImage.value)
        binding.ivLoadedPicture.visibility = View.VISIBLE
    }

    private fun hideImagePreview(){
        binding.ivLoadedPicture.visibility = View.GONE
    }

    override fun onStart() {
        super.onStart()
        val dialog = dialog
        dialog?.let {
            val width = ViewGroup.LayoutParams.MATCH_PARENT
            val height = ViewGroup.LayoutParams.MATCH_PARENT
            dialog.window!!.setLayout(width, height)
            dialog.window!!.setWindowAnimations(R.style.AppTheme_Slide)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Timber.d("resultcode: $resultCode, requestCode: $requestCode")
        if (requestCode == REQUEST_PICK_IMAGE && resultCode == Activity.RESULT_OK) {
            //Check if picture was successfully given back from the gallery
            if (data == null || data.data == null) {
                return
            }
            val pictureUri = data.data
            viewModel.postImage.value = pictureUri!!
        }
    }

    private fun openGallery() {
        Intent().also {
            it.type = "image/*"
            it.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(Intent.createChooser(it, "Select Picture"), REQUEST_PICK_IMAGE)
        }
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener { dismiss() }
        binding.btnAddNewPost.setOnClickListener {
            viewModel.addPost()
            dismiss()
        }
    }

}

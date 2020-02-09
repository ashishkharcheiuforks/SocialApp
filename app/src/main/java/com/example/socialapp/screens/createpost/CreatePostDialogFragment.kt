package com.example.socialapp.screens.createpost

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.observe
import androidx.navigation.navGraphViewModels
import com.example.socialapp.AuthenticatedNestedGraphViewModel
import com.example.socialapp.R
import com.example.socialapp.common.REQUEST_PICK_IMAGE
import com.example.socialapp.databinding.DialogCreatePostBinding
import org.koin.android.viewmodel.ext.android.viewModel

class CreatePostDialogFragment : DialogFragment() {

    private lateinit var binding: DialogCreatePostBinding

    private val viewModel: CreatePostViewModel by viewModel()

    private val nestedGraphViewModel: AuthenticatedNestedGraphViewModel by navGraphViewModels(
        R.id.authenticated_graph
    )

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

        binding.apply {
            lifecycleOwner = this@CreatePostDialogFragment
            vm = viewModel
        }

        setupToolbar()

        nestedGraphViewModel.user.observe(viewLifecycleOwner) { binding.user = it }

        binding.btnAddPicture.setOnClickListener { openGallery() }

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
        // Check if picture was successfully retrieved from the gallery
        if (data == null || data.data == null) {
            return
        }
        // Image from gallery
        if (requestCode == REQUEST_PICK_IMAGE && resultCode == Activity.RESULT_OK) {
            val pictureUri = data.data
            viewModel.postImage.value = pictureUri.toString()
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

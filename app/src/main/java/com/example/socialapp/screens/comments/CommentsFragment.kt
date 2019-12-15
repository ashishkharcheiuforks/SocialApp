package com.example.socialapp.screens.comments

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.navGraphViewModels
import com.example.socialapp.AuthenticatedNestedGraphViewModel
import com.example.socialapp.R
import com.example.socialapp.adapter.CommentsAdapter
import com.example.socialapp.databinding.FragmentCommentsBinding
import timber.log.Timber

class CommentsFragment : DialogFragment(), CommentsAdapter.OnCommentClickListener {

    private lateinit var binding: FragmentCommentsBinding

    private lateinit var postId: String

    private lateinit var viewModel: CommentsViewModel

    private val adapter by lazy { CommentsAdapter(this) }

    private val authenticatedNestedGraphViewModel: AuthenticatedNestedGraphViewModel by navGraphViewModels(
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
        binding = FragmentCommentsBinding.inflate(inflater, container, false)
        binding.toolbar.navigationIcon!!.setTint(Color.WHITE)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Get passed postId in bundle
        arguments?.let {
            postId = it.getString("postId").toString()
        }
        Timber.i("Passed postId: $postId")

        // Initialize viewmodel with received postId
        viewModel = ViewModelProviders
            .of(this, CommentsViewModelFactory(postId))
            .get(CommentsViewModel::class.java)

        binding.lifecycleOwner = this

        binding.viewModel = viewModel

        // Set navigation icon behaviour to dismiss the dialog
        binding.toolbar.setNavigationOnClickListener { dismiss() }

        binding.recyclerview.adapter = adapter

        viewModel.comments.observe(viewLifecycleOwner, Observer {
//                        adapter.submit(it)
        })

        authenticatedNestedGraphViewModel.user.observe(this, Observer {
            binding.user = it
        })
    }

    // Makes dialog display as fully maximized
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

    override fun onProfilePictureClicked(userId: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


}

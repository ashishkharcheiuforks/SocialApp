package com.example.socialapp.screens.comments

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import com.example.socialapp.AuthenticatedNestedGraphViewModel
import com.example.socialapp.R
import com.example.socialapp.adapter.CommentsAdapter
import com.example.socialapp.databinding.FragmentCommentsBinding
import com.example.socialapp.model.Comment
import com.example.socialapp.screens.main.MainScreenFragmentDirections
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.ExperimentalCoroutinesApi
import timber.log.Timber

class CommentsFragment : DialogFragment(), CommentsAdapter.onCommentClickListener {

    private lateinit var binding: FragmentCommentsBinding
    private val viewModel by lazy {
        ViewModelProviders.of(this, CommentsViewModelFactory(postId))
            .get(CommentsViewModel::class.java)
    }
    private lateinit var postId: String

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

        binding.toolbar.apply {
            navigationIcon!!.setTint(Color.WHITE)
            setNavigationOnClickListener { dismiss() }
        }

        return binding.root
    }

    @ExperimentalCoroutinesApi
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Get passed postId in bundle
        arguments?.let {
            postId = it.getString("postId")!!
            Timber.i("Passed postId: $postId")
        }

        binding.lifecycleOwner = this@CommentsFragment
        binding.viewModel = viewModel

        authenticatedNestedGraphViewModel.user.observe(this, Observer {
            binding.user = it
        })

        viewModel.comments.observe(this@CommentsFragment){
            updateRecyclerView(it)
        }

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

    private fun updateRecyclerView(comments: List<Comment>) {
        binding.recyclerview.apply {
            adapter = CommentsAdapter(comments, this@CommentsFragment)
            scrollToPosition(comments.lastIndex - 1)
        }
    }

    override fun openUserProfile(uid: String) {
        val action = MainScreenFragmentDirections.actionGlobalProfileFragment(uid)
        findNavController().navigate(action)
    }

}

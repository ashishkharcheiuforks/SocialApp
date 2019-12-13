package com.example.socialapp.screens.main.home


import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import com.example.socialapp.AuthenticatedNestedGraphViewModel
import com.example.socialapp.R
import com.example.socialapp.adapter.PostsAdapter
import com.example.socialapp.databinding.FragmentHomeBinding
import com.example.socialapp.screens.comments.CommentsFragment
import com.example.socialapp.screens.createpost.CreatePostDialogFragment
import com.example.socialapp.screens.main.MainScreenFragmentDirections
import com.google.firebase.auth.FirebaseAuth
import timber.log.Timber


class HomeFragment : Fragment(), PostsAdapter.OnPostClickListener {

    private lateinit var binding: FragmentHomeBinding

    private val adapter by lazy { PostsAdapter(this) }

    private val homeViewModel: HomeViewModel by viewModels()

    private val authenticatedNestedGraphViewModel: AuthenticatedNestedGraphViewModel by navGraphViewModels(
        R.id.authenticated_nested_graph
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set recyclerview adapter for posts
        binding.recyclerview.adapter = adapter

        //** Set the colors of the Pull To Refresh View
        binding.swipeRefreshLayout.setProgressBackgroundColorSchemeColor(ContextCompat.getColor(context!!.applicationContext, R.color.colorPrimary))
        binding.swipeRefreshLayout.setColorSchemeColors(Color.WHITE)

        authenticatedNestedGraphViewModel.user.observe(viewLifecycleOwner, Observer {
            binding.user = it
        })

        homeViewModel.posts.observe(viewLifecycleOwner, Observer {
            binding.swipeRefreshLayout.isRefreshing = false
            adapter.submitList(it)
        })

        binding.createNewPost.setOnClickListener {
            openNewPostDialog()
        }

        binding.swipeRefreshLayout.setOnRefreshListener {
            homeViewModel.refreshPosts()
        }


    }

    override fun onDestroy() {
        Timber.i("onDestroy() called")
        super.onDestroy()
    }


    override fun onLikeButtonClicked(postId: String) {
        homeViewModel.likeThePost(postId)
    }

    override fun onUnlikeButtonClicked(postId: String) {
        homeViewModel.unlikeThePost(postId)
    }

    override fun onCommentButtonClicked(postId: String) {
        openCommentsSection(postId)
    }

    // When user clicks on the avatar of user that added the post
    override fun onProfilePictureClicked(userId: String) {
        // Prevents opening self profile
        if (userId != FirebaseAuth.getInstance().uid) {
            navigateToUserProfile(userId)
        }
    }

    private fun openNewPostDialog() {
        val dialog = CreatePostDialogFragment()
        dialog.show(childFragmentManager, "create_post_dialog")
    }

    private fun navigateToUserProfile(userId: String) {
        val action = MainScreenFragmentDirections.actionGlobalProfileFragment(userId)
        findNavController().navigate(action)
    }

    private fun openCommentsSection(postId: String) {
        // Pass postId in bundle to dialog fragment instance
        val bottomSheetDialogFragment = CommentsFragment().apply {
            arguments = Bundle().apply { putString("postId", postId) }
        }
        bottomSheetDialogFragment.show(childFragmentManager, "CancelSentInviteDialog")
    }

}

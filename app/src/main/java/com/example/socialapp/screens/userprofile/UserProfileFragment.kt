package com.example.socialapp.screens.userprofile


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.navGraphViewModels
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.example.socialapp.AuthenticatedNestedGraphViewModel
import com.example.socialapp.R
import com.example.socialapp.adapter.PostsAdapter
import com.example.socialapp.databinding.FragmentUserProfileBinding
import com.example.socialapp.model.FriendshipStatus
import com.example.socialapp.model.User
import com.example.socialapp.screens.comments.CommentsFragment
import com.google.firebase.auth.FirebaseAuth
import timber.log.Timber


class UserProfileFragment : Fragment(),
    ModalBottomSheetListener,
    PostsAdapter.OnPostClickListener {

    private lateinit var binding: FragmentUserProfileBinding

    private val nestedGraphViewModel: AuthenticatedNestedGraphViewModel by navGraphViewModels(R.id.authenticated_graph)
    private val userProfileViewModel: UserProfileViewModel by lazy {
        ViewModelProviders.of(this, UserProfileViewModelFactory(args.uid))
            .get(UserProfileViewModel::class.java)
    }

    private val adapter by lazy { PostsAdapter(this) }
    private val args: UserProfileFragmentArgs by navArgs()
    private val auth = FirebaseAuth.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentUserProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.lifecycleOwner = this

        setupToolbar()

        //Shows friendship status button if opened profile is not currently logged user
        setupFriendshipStatusButton()

        // Binds user object to the data binding variable that displays
        // header of the opened user profile
        bindUser()

        // Set adapter for recyclerview that displays user posts
        binding.recyclerview.adapter = adapter

        userProfileViewModel.posts.observe(viewLifecycleOwner, Observer {
            binding.swipeRefreshLayout.isRefreshing = false
            adapter.submitList(it)
        })

        binding.swipeRefreshLayout.setOnRefreshListener { userProfileViewModel.refreshPosts() }

    }

    override fun onDestroy() {
        Timber.i("onDestroy() called")
        super.onDestroy()
    }

    override fun onAcceptInvitation() {
        userProfileViewModel.acceptFriendRequest()
            .addOnCompleteListener {
                // Handle UI
            }
    }

    override fun onDeleteInvitation() {
        userProfileViewModel.cancelFriendRequest()
            .addOnCompleteListener {
                // Handle UI
            }
    }

    override fun onLikeButtonClicked(postId: String) {
        userProfileViewModel.likePost(postId)
            .addOnCompleteListener {
                // Handle UI
            }
    }

    override fun onUnlikeButtonClicked(postId: String) {
        userProfileViewModel.unlikePost(postId)
            .addOnCompleteListener {
                // Handle UI
            }
    }

    override fun onCommentButtonClicked(postId: String) {
        openCommentsSection(postId)
    }

    override fun onProfilePictureClicked(userId: String) {
        // Do nothing - No need to reopen the same user profile
    }

    private fun openCommentsSection(postId: String) {
        // Pass postId in bundle to dialog fragment that displays comments
        val bottomSheetDialogFragment = CommentsFragment().apply {
            arguments = Bundle().apply { putString("postId", postId) }
        }
        bottomSheetDialogFragment.show(childFragmentManager, "CancelSentInviteDialog")
    }

    // Shows and handles behaviour of friendship status button on other users profiles
    private fun setupFriendshipStatusButton() {
        if (!isAuthenticatedUserProfile()) {
            userProfileViewModel.friendshipStatus
                .observe(viewLifecycleOwner, Observer { result ->
                    binding.btnFriendshipStatus.apply {
                        when (result.data().get("status")) {
                            null -> {
                                text = FriendshipStatus.NO_STATUS.status
                                setOnClickListener { userProfileViewModel.inviteToFriends() }
                            }
                            FriendshipStatus.ACCEPTED.status -> {
                                text = FriendshipStatus.ACCEPTED.status
                                setOnClickListener { openDeleteFromFriendsDialog() }
                            }
                            FriendshipStatus.INVITATION_SENT.status -> {
                                text = FriendshipStatus.INVITATION_SENT.status
                                setOnClickListener { openCancelFriendInviteDialog() }
                            }
                            FriendshipStatus.INVITATION_RECEIVED.status -> {
                                text = FriendshipStatus.INVITATION_RECEIVED.status
                                setOnClickListener { openAcceptOrCancelInviteDialog() }
                            }
                        }
                        visibility = View.VISIBLE
                        binding.btnMessage.visibility = View.VISIBLE
                    }
                })
        }
    }

    private fun bindUser() = if (isAuthenticatedUserProfile()) {
        nestedGraphViewModel.user.observe(viewLifecycleOwner, Observer<User> { binding.user = it })
    } else {
        userProfileViewModel.user.observe(viewLifecycleOwner, Observer<User> { binding.user = it })
    }

    private fun isAuthenticatedUserProfile(): Boolean = args.uid == auth.uid

    private fun navigateToEditUserProfileScreen() {
        val action = UserProfileFragmentDirections.actionProfileFragmentToEditProfileFragment()
        findNavController().navigate(action)
    }

    private fun setupToolbar() {
        val navController = findNavController()
        val appBarConfiguration = AppBarConfiguration(navController.graph)
        binding.toolbar.setupWithNavController(navController, appBarConfiguration)

        // Shows edit button on toolbar when inside logged in user profile
        if (isAuthenticatedUserProfile()) {
            binding.toolbar.inflateMenu(R.menu.menu_toolbar_user_profile)
            binding.toolbar.setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.action_edit_profile ->
                        navigateToEditUserProfileScreen()
                }
                true
            }
        }
    }

    private fun openAcceptOrCancelInviteDialog() {
        AcceptOrCancelInviteDialog()
            .show(childFragmentManager, "AcceptOrCancelInviteDialog")
    }

    private fun openCancelFriendInviteDialog() {
        CancelSentInviteDialog().show(
            childFragmentManager,
            "CancelSentInviteDialog"
        )
    }

    private fun openDeleteFromFriendsDialog() {
        DeleteFromFriendsDialog()
            .show(childFragmentManager, "DeleteFromFriendsDialog")
    }

}

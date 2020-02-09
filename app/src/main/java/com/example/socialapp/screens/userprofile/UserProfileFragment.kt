package com.example.socialapp.screens.userprofile


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.observe
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
import com.example.socialapp.screens.comments.CommentsFragment
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.koin.android.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import timber.log.Timber


@ExperimentalCoroutinesApi
class UserProfileFragment : Fragment(),
    ModalBottomSheetListener,
    PostsAdapter.OnPostClickListener {

    private lateinit var binding: FragmentUserProfileBinding

    private val nestedGraphViewModel: AuthenticatedNestedGraphViewModel by navGraphViewModels(R.id.authenticated_graph)

    private val userProfileViewModel: UserProfileViewModel by viewModel { parametersOf(args.uid) }

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

        userProfileViewModel.posts.observe(viewLifecycleOwner) {
            binding.swipeRefreshLayout.isRefreshing = false
            adapter.submitList(it)
        }

        binding.swipeRefreshLayout.setOnRefreshListener { userProfileViewModel.refreshPosts() }

        binding.btnMessage.setOnClickListener { navigateToChatScreen() }

    }

    override fun onDestroy() {
        Timber.i("onDestroy() called")
        super.onDestroy()
    }

    override fun onAcceptInvitation() {
        userProfileViewModel.acceptFriendRequest()
    }

    override fun onDeleteInvitation() {
        userProfileViewModel.cancelFriendRequest()
    }

    override fun onLikeButtonClicked(postId: String) {
        userProfileViewModel.likePost(postId)
    }

    override fun onUnlikeButtonClicked(postId: String) {
        userProfileViewModel.unlikePost(postId)
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

    private fun buttonInviteToFriends() = binding.btnFriendshipStatus.apply {
        icon = context.getDrawable(R.drawable.ic_add_black_24dp)
        text = FriendshipStatus.NO_STATUS.status
        setOnClickListener { userProfileViewModel.inviteToFriends() }
    }

    private fun buttonInvitationReceived() = binding.btnFriendshipStatus.apply {
        icon = context.getDrawable(R.drawable.ic_add_black_24dp)
        text = FriendshipStatus.INVITATION_RECEIVED.status
        setOnClickListener { openAcceptOrCancelInviteDialog() }
    }

    private fun buttonInvitationSent() = binding.btnFriendshipStatus.apply {
        icon = context.getDrawable(R.drawable.ic_close_black_24dp)
        text = FriendshipStatus.INVITATION_SENT.status
        setOnClickListener { openCancelFriendInviteDialog() }
    }

    private fun buttonFriends() = binding.btnFriendshipStatus.apply {
        icon = context.getDrawable(R.drawable.ic_groups_black_24dp)
        text = FriendshipStatus.ACCEPTED.status
        setOnClickListener { openDeleteFromFriendsDialog() }
    }

    // Shows and handles behaviour of friendship status button on other users profiles
    private fun setupFriendshipStatusButton() {
        if (!isAuthenticatedUserProfile()) {
            userProfileViewModel.friendshipStatus.observe(viewLifecycleOwner) { result ->
                when (result.data().get("status")) {
                    null -> {
                        buttonInviteToFriends()
                    }
                    FriendshipStatus.ACCEPTED.status -> {
                        buttonFriends()
                    }
                    FriendshipStatus.INVITATION_SENT.status -> {
                        buttonInvitationSent()
                    }
                    FriendshipStatus.INVITATION_RECEIVED.status -> {
                        buttonInvitationReceived()
                    }
                }
            }
            binding.linearLayoutButtons.visibility = View.VISIBLE
        }
    }

    private fun bindUser() {
        if (isAuthenticatedUserProfile()) {
            nestedGraphViewModel.user.observe(
                viewLifecycleOwner
            ) { binding.user = it }
        } else {
            userProfileViewModel.user.observe(
                viewLifecycleOwner
            ) { binding.user = it }
        }
    }

    private fun isAuthenticatedUserProfile(): Boolean = args.uid == auth.uid

    private fun navigateToEditUserProfileScreen() {
        val action = UserProfileFragmentDirections.actionProfileFragmentToEditProfileFragment()
        findNavController().navigate(action)
    }

    private fun navigateToChatScreen() {
        val action = UserProfileFragmentDirections.actionGlobalConversationFragment(args.uid)
        findNavController().navigate(action)
    }

    private fun setupToolbar() {
        val navController = findNavController()
        val appBarConfiguration = AppBarConfiguration(navController.graph)
        binding.toolbar.setupWithNavController(navController, appBarConfiguration)

        // Shows edit button on toolbar when inside logged in user profile
        if (isAuthenticatedUserProfile()) {
            binding.toolbar.apply {
                inflateMenu(R.menu.menu_user_profile)
                setOnMenuItemClickListener {
                    when (it.itemId) {
                        R.id.action_edit_profile ->
                            navigateToEditUserProfileScreen()
                    }
                    true
                }
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

package com.example.socialapp.screens.userprofile


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.navGraphViewModels
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.example.socialapp.AuthenticatedNestedGraphViewModel
import com.example.socialapp.FriendshipStatus
import com.example.socialapp.R
import com.example.socialapp.User
import com.example.socialapp.databinding.FragmentUserProfileBinding
import com.google.firebase.auth.FirebaseAuth
import timber.log.Timber

class UserProfileFragment : Fragment(), View.OnClickListener {
    private lateinit var binding: FragmentUserProfileBinding

    private lateinit var userUid: String

    private lateinit var userProfileViewModel: UserProfileViewmodel

    private val args: UserProfileFragmentArgs by navArgs()

    private val nestedGraphViewModel: AuthenticatedNestedGraphViewModel by navGraphViewModels(R.id.authenticated_nested_graph)

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

        //Unique Id of user
        userUid = args.uid

        binding.lifecycleOwner = this

        setupToolbar()

        //Initialize Viewmodels
        userProfileViewModel =
            ViewModelProviders.of(this, UserProfileViewModelFactory(userUid))
                .get(UserProfileViewmodel::class.java)


        //Set Observers
        setupFriendshipStatusButton()

        bindUser()

        binding.userProfileViewmodel = userProfileViewModel


        //Set Listeners
        binding.btnFriendshipStatus.setOnClickListener(this)

    }

    /**    On Click Handling    **/

    override fun onClick(view: View?) {
        when (view?.id) {
            /**    Friendship status buttom    **/
            R.id.btn_friendship_status -> {
                val btn = view as Button
                when (btn.text.toString()) {
                    FriendshipStatus.INVITATION_SENT.status -> {
                        openCancelFriendInviteBottomSheet()
                    }
                    FriendshipStatus.INVITATION_RECEIVED.status -> {
                        openAcceptOrCancelInvitationBottomSheet()
                    }
                    FriendshipStatus.ACCEPTED.status -> {
                        openDeleteFromFriendsBottomSheet()
                    }
                    "Add to friends" -> {
                        userProfileViewModel.inviteToFriends()
                    }
                }
            }
            /**    Delete user from friend list - items in bottom sheets */
            R.id.bottom_sheet_item_delete_from_friends,
            R.id.bottom_sheet_item_cancel_friend_invite,
            R.id.bottom_sheet_item_cancel_friend_request-> {
                userProfileViewModel.cancelFriendRequest()
            }
            /**    Accept received friend request - bottom sheet dialog item */
            R.id.bottom_sheet_item_accept_friend_request -> {
                userProfileViewModel.acceptFriendRequest()
            }
        }
    }

    override fun onDestroy() {
        Timber.i("onDestroy() called")
        super.onDestroy()
    }

    private fun setupFriendshipStatusButton() {
        if(isAuthenticatedUserProfile()) return
        userProfileViewModel.friendshipStatus
            .observe(viewLifecycleOwner, Observer { result ->
                if (result.isSuccessful) {
                    val status = result.data().getString("status")
                    Timber.d("Friendship status: $status")
                    userProfileViewModel.updateStatus(status)
                    binding.btnFriendshipStatus.visibility = View.VISIBLE
                }
            })
    }

    private fun bindUser() {
        if (isAuthenticatedUserProfile()) {
            Timber.i("Observing user inside nested graph viewmodel")
            nestedGraphViewModel.user.observe(
                viewLifecycleOwner,
                Observer<User> { binding.user = it })
        } else {
            Timber.i("Observing user inside user viewmodel")
            userProfileViewModel.user.observe(
                viewLifecycleOwner,
                Observer<User> { binding.user = it })
        }
    }

    private fun isAuthenticatedUserProfile(): Boolean =
        userUid == FirebaseAuth.getInstance().uid


    private fun navigateToEditUserProfileScreen() {
        val action = UserProfileFragmentDirections.actionProfileFragmentToEditProfileFragment()
        findNavController().navigate(action)
    }

    private fun setupToolbar() {
        val navController = findNavController()
        val appBarConfiguration = AppBarConfiguration(navController.graph)
        binding.toolbar.setupWithNavController(navController, appBarConfiguration)

        //Adds edit profile action icon to the toolbar
        if (isAuthenticatedUserProfile()) {
            binding.toolbar.inflateMenu(R.menu.menu_toolbar_user_profile)
            binding.toolbar.setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.action_edit_profile -> {
                        navigateToEditUserProfileScreen()
                        true
                    }
                    else -> {
                        super.onOptionsItemSelected(it)
                    }
                }
            }
        }
    }

    private fun openDeleteFromFriendsBottomSheet() {
        DeleteFriendBottomSheetDialogFragment(this).show(
            childFragmentManager,
            "Delete From Friends Bottom Sheet"
        )
    }

    private fun openCancelFriendInviteBottomSheet() {
        CancelFriendInviteBottomSheetDialogFragment(this).show(
            childFragmentManager,
            "Cancel Friend Invite Bottom Sheet"
        )
    }

    private fun openAcceptOrCancelInvitationBottomSheet() {
        AcceptOrCancelInviteBottomSheetDialogFragment(this).show(
            childFragmentManager,
            "Accept or Cancel Invite Bottom Sheet"
        )
    }

}

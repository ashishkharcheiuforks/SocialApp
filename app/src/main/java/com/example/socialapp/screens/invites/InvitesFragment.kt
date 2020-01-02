package com.example.socialapp.screens.invites


import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import com.example.socialapp.R
import com.example.socialapp.adapter.InvitesAdapter
import com.example.socialapp.databinding.FragmentInvitesBinding
import com.example.socialapp.screens.userprofile.UserProfileFragmentDirections
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.ExperimentalCoroutinesApi

class InvitesFragment : Fragment(), InvitesAdapter.onInviteItemClickListener {

    private lateinit var binding: FragmentInvitesBinding

    private val viewModel: InvitesViewModel by viewModels()

    private val db = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentInvitesBinding.inflate(inflater, container, false)
        // Set colors of Swipe To Refresh Layout Widget
        binding.swipeRefreshLayout.apply {
            setColorSchemeColors(Color.WHITE)
            setProgressBackgroundColorSchemeColor(
                ContextCompat.getColor(context!!.applicationContext, R.color.colorPrimary)
            )
        }
        return binding.root
    }

    @ExperimentalCoroutinesApi
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.swipeRefreshLayout.apply {
            setOnRefreshListener {
                isRefreshing = false
            }
        }

        viewModel.invites.observe(viewLifecycleOwner) {
            binding.invitesRecyclerview.adapter = InvitesAdapter(it, this)
        }


    }


    override fun acceptInvite(uid: String) {
        viewModel.acceptFriendRequest(uid)
    }

    override fun deleteInvite(uid: String) {
        viewModel.deleteFriendRequest(uid)
    }

    override fun openUserProfile(uid: String) {
        val action = UserProfileFragmentDirections.actionGlobalProfileFragment(uid)
        findNavController().navigate(action)
    }


}

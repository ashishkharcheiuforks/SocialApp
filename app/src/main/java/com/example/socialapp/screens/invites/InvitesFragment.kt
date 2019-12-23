package com.example.socialapp.screens.invites


import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.socialapp.R
import com.example.socialapp.adapter.InvitesAdapter
import com.example.socialapp.databinding.FragmentInvitesBinding
import com.example.socialapp.model.User
import com.example.socialapp.screens.userprofile.UserProfileFragmentDirections
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import timber.log.Timber

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.swipeRefreshLayout.apply {
            setOnRefreshListener {
                initRecyclerView()
                isRefreshing = false
            }
        }

        initRecyclerView()

    }

    private fun initRecyclerView() {
        viewModel.fetchInvitesLiveData().observe(this, Observer { result ->
            if (result.isSuccessful) {
                val users: MutableList<User> = ArrayList()
                //Returned document snapshots contains only uid of users that sent invites to friends
                val data: List<DocumentSnapshot> = result.data().documents

                Timber.d("queried documents: ${data.size}")

                // If invites list is empty show placeholder
                binding.noInvitesPlaceholder.apply {
                    visibility = if(data.isEmpty()) View.VISIBLE else View.GONE
                }

                //Create adapter and pass new list of Users
                data.forEach {
                    db.collection("users").document(it.id).get()
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                users.add(
                                    User(
                                        uid = it.id,
                                        firstName = task.result!!.getString("firstName").toString(),
                                        nickname = task.result!!.getString("nickname").toString(),
                                        profilePictureUri = Uri.parse(task.result!!.getString("profilePictureUrl").toString())
                                    )
                                )
                            }
                            binding.invitesRecyclerview.adapter = InvitesAdapter(users, this)
                        }
                }
            }//End of if statement
        })
    }

    override fun acceptInvite(uid: String) {
        viewModel.acceptFriendRequest(uid)
    }

    override fun deleteInvite(uid: String) {
        viewModel.deleteFriendRequest(uid)
    }

    override fun openUserProfile(uid: String) {
        if (uid != FirebaseAuth.getInstance().uid) {
            val action = UserProfileFragmentDirections.actionGlobalProfileFragment(uid)
            findNavController().navigate(action)
        }
    }


}

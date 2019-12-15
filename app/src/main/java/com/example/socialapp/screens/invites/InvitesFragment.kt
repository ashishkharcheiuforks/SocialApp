package com.example.socialapp.screens.invites


import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.socialapp.adapter.InvitesAdapter
import com.example.socialapp.databinding.FragmentInvitesBinding
import com.example.socialapp.model.User
import com.example.socialapp.screens.userprofile.UserProfileFragmentDirections
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
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
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        viewModel.fetchInvitesLiveData().observe(this, Observer { result ->
            if (result.isSuccessful) {
                val users: MutableList<User> = ArrayList()
                //Returned document snapshots contains only uid of users that sent invites to friends
                val data: List<DocumentSnapshot> = result.data().documents

                Timber.d("queried documents: ${data.size}")

                //Create adapter and pass new list of Users
                data.forEach {
                    try {
                    db.collection("users").document(it.id).get()
                        .addOnCompleteListener { task ->
                            Timber.d(task.result!!.metadata.toString())
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
                            binding.invitesRecyclerview.adapter =
                                InvitesAdapter(users, this)
                        }
                }
                    catch(e: FirebaseFirestoreException ){
                        Timber.d(e, "exception occured")
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
        if(uid != FirebaseAuth.getInstance().uid){
            val action = UserProfileFragmentDirections.actionGlobalProfileFragment(uid)
            findNavController().navigate(action)
        }
    }


}

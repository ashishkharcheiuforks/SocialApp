package com.example.socialapp.screens.friends


import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import androidx.paging.PagedList
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.socialapp.FriendshipStatus
import com.example.socialapp.User
import com.example.socialapp.databinding.FragmentFriendsBinding
import com.example.socialapp.databinding.FriendItemBinding
import com.firebase.ui.firestore.paging.FirestorePagingAdapter
import com.firebase.ui.firestore.paging.FirestorePagingOptions
import com.firebase.ui.firestore.paging.LoadingState
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


class FriendsFragment : Fragment(), FriendItemListener {
    private lateinit var binding: FragmentFriendsBinding

    private var adapter: FirestorePagingAdapter<User, FriendViewHolder>? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFriendsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupToolbar()

        setupRecyclerView()

    }

    override fun onFriendItemClick(uid: String, view: View) {
        navigateToUserProfile(uid)
    }

    private fun setupToolbar() {
        val navController = findNavController()
        val appBarConfiguration = AppBarConfiguration(navController.graph)
        binding.toolbar.setupWithNavController(navController, appBarConfiguration)
    }

    private fun setupRecyclerView() {
        binding.recyclerview.setHasFixedSize(true)

        val collectionRef = FirebaseFirestore.getInstance()
            .collection("users")
            .document(FirebaseAuth.getInstance().uid.toString())
            .collection("friends")

        val query =
            collectionRef.whereEqualTo("status", FriendshipStatus.ACCEPTED.status)

        val config = PagedList.Config.Builder()
            .setEnablePlaceholders(false)
            .setPrefetchDistance(10)
            .setPageSize(20)
            .build()

        val options = FirestorePagingOptions.Builder<User>()
            .setLifecycleOwner(this)
            .setQuery(query, config) { snapshot ->
                User(
                    uid = snapshot.id,
                    firstName = "",
                    nickname = "",
                    profilePictureUri = Uri.parse("")
                )
            }
            .setLifecycleOwner(this)
            .build()


        adapter =
            object : FirestorePagingAdapter<User, FriendViewHolder>(options) {
                override fun onCreateViewHolder(
                    parent: ViewGroup,
                    viewType: Int
                ): FriendViewHolder {
                    val inflater = LayoutInflater.from(parent.context)
                    val binding = FriendItemBinding.inflate(inflater, parent, false)
                    return FriendViewHolder(binding)
                }

                override fun onBindViewHolder(
                    holder: FriendViewHolder,
                    position: Int,
                    model: User
                ) {
                    //Is data here reloaded every time the adapter is recreated?
                    //Consider trying to retrieve cache first (#get(Source.Cache))

                    FirebaseFirestore.getInstance()
                        .collection("users").document(model.uid).get()
                        .addOnCompleteListener {
                            val newModel = User(
                                uid = model.uid,
                                firstName = it.result!!.get("first_name").toString(),
                                nickname = it.result!!.get("nickname").toString(),
                                profilePictureUri = Uri.parse(it.result!!.get("profile_picture_url").toString())
                            )
                            holder.bind(newModel, this@FriendsFragment)
                        }
                }

                override fun onLoadingStateChanged(state: LoadingState) {
                    super.onLoadingStateChanged(state)
                }
            }

        binding.recyclerview.addItemDecoration(
            DividerItemDecoration(
                context,
                LinearLayoutManager.VERTICAL
            )
        )
        binding.recyclerview.adapter = adapter

    }

    private fun navigateToUserProfile(uid: String) {
        val action = FriendsFragmentDirections.actionGlobalProfileFragment(uid)
        findNavController().navigate(action)
    }


}

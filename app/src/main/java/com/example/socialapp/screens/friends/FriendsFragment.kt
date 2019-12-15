package com.example.socialapp.screens.friends


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.example.socialapp.adapter.FriendsAdapter
import com.example.socialapp.databinding.FragmentFriendsBinding


class FriendsFragment : Fragment(), FriendsAdapter.ViewHolder.OnFriendItemClickListener {
    private lateinit var binding: FragmentFriendsBinding

    private val adapter by lazy { FriendsAdapter(this) }

    private val viewModel: FriendsViewModel by viewModels()

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

        binding.recyclerview.adapter = adapter

        viewModel.friends.observe(viewLifecycleOwner, Observer {
            binding.swipeRefreshLayout.isRefreshing = false
            adapter.submitList(it)
        })

        binding.swipeRefreshLayout.setOnRefreshListener {
            viewModel.refreshFriends()
        }

    }

    override fun onFriendItemClick(uid: String) {
        navigateToUserProfile(uid)
    }

    private fun setupToolbar() {
        val navController = findNavController()
        val appBarConfiguration = AppBarConfiguration(navController.graph)
        binding.toolbar.setupWithNavController(navController, appBarConfiguration)
    }


    private fun navigateToUserProfile(uid: String) {
        val action = FriendsFragmentDirections.actionGlobalProfileFragment(uid)
        findNavController().navigate(action)
    }


}

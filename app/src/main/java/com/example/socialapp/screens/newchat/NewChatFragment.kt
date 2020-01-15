package com.example.socialapp.screens.newchat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.example.socialapp.adapter.FriendsAdapter
import com.example.socialapp.databinding.FragmentNewChatBinding

class NewChatFragment : Fragment(), FriendsAdapter.ViewHolder.OnFriendItemClickListener {

    private lateinit var binding: FragmentNewChatBinding

    private val viewModel: NewChatViewModel by viewModels()

    private val adapter by lazy { FriendsAdapter(this) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentNewChatBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolbar()

        binding.recyclerview.adapter = adapter

        viewModel.friends.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }
    }

    override fun onFriendItemClick(uid: String) {
        navigateToConversationScreen(uid)
    }

    private fun navigateToConversationScreen(uid: String) {
        val action =
            NewChatFragmentDirections
                .actionNewChatFragmentToConversationFragment(uid)
        findNavController().navigate(action)
    }

    private fun setupToolbar() {
        val navController = findNavController()
        val appBarConfiguration = AppBarConfiguration(navController.graph)
        binding.toolbar.setupWithNavController(navController, appBarConfiguration)
    }
}

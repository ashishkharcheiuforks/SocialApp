package com.example.socialapp.screens.chat

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
import com.example.socialapp.adapter.ChatRoomsAdapter
import com.example.socialapp.databinding.FragmentChatRoomsBinding

class ChatRoomsFragment : Fragment(), ChatRoomsAdapter.OnChatItemClickListener {

    private lateinit var binding: FragmentChatRoomsBinding

    private val viewModel: ChatRoomsViewModel by viewModels()

    private val adapter by lazy { ChatRoomsAdapter(this) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentChatRoomsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupToolbar()

        binding.recyclerview.adapter = adapter

        viewModel.chatRooms.observe(this){
            adapter.submitList(it)
        }

        binding.fabNewChat.setOnClickListener {
            navigateToPickFriendForChat()
        }

        binding.swipeRefreshLayout.setOnRefreshListener {
            viewModel.refreshChatRooms()
            binding.swipeRefreshLayout.isRefreshing = false
        }

    }

    override fun onChatItemClick(userId: String) {
        navigateToConversationScreen(userId)
    }

    private fun navigateToPickFriendForChat() {
        val action =
            ChatRoomsFragmentDirections.actionChatRoomsFragmentToNewChatFragment()
        findNavController().navigate(action)
    }

    private fun navigateToConversationScreen(userId: String) {
        val action =
            ChatRoomsFragmentDirections.actionChatRoomsFragmentToConversationFragment(userId)
        findNavController().navigate(action)
    }

    private fun setupToolbar() {
        val navController = findNavController()
        val appBarConfiguration = AppBarConfiguration(navController.graph)
        binding.toolbar.setupWithNavController(navController, appBarConfiguration)
    }

}
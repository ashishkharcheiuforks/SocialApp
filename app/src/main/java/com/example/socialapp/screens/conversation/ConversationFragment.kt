package com.example.socialapp.screens.conversation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.navGraphViewModels
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.example.socialapp.AuthenticatedNestedGraphViewModel
import com.example.socialapp.R
import com.example.socialapp.adapter.ChatAdapter
import com.example.socialapp.databinding.FragmentConversationBinding
import kotlinx.coroutines.ExperimentalCoroutinesApi

class ConversationFragment : Fragment() {

    private lateinit var binding: FragmentConversationBinding

    private val args: ConversationFragmentArgs by navArgs()

    private val adapter by lazy { ChatAdapter() }

    @ExperimentalCoroutinesApi
    private val viewModel: ConversationViewModel by lazy {
        ViewModelProvider(this, ConversationViewModelFactory(args.userId))
            .get(ConversationViewModel::class.java)
    }

    private val authGraphViewModel: AuthenticatedNestedGraphViewModel by navGraphViewModels(R.id.authenticated_graph)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentConversationBinding.inflate(inflater, container, false)
        return binding.root
    }


    @ExperimentalCoroutinesApi
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupToolbar()
        binding.apply {
            lifecycleOwner = this@ConversationFragment
            vm = viewModel
            graphvm = authGraphViewModel
            recyclerview.adapter = adapter
        }

        viewModel.messagesList.observe(viewLifecycleOwner) {
            adapter.submitList(it)
            // Assures displaying from the bottom
            binding.recyclerview.post { binding.recyclerview.scrollToPosition(it.lastIndex) }
        }

    }

    private fun setupToolbar() {
        val navController = findNavController()
        val appBarConfiguration = AppBarConfiguration(navController.graph)
        binding.toolbar.setupWithNavController(navController, appBarConfiguration)
    }

}

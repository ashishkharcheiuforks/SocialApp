package com.example.socialapp.screens.chat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.example.socialapp.databinding.FragmentConversationBinding

class ConversationFragment : Fragment() {

    private lateinit var viewModel: ConversationViewModel

    private lateinit var binding: FragmentConversationBinding

//    private val args by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentConversationBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        retrieveSafeArguments()

        binding.lifecycleOwner = this

        viewModel = ViewModelProviders.of(this).get(ConversationViewModel::class.java)

    }

    private fun retrieveSafeArguments() {

    }

}

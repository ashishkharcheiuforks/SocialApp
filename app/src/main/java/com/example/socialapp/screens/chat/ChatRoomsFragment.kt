package com.example.socialapp.screens.chat

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.example.socialapp.R

class ChatRoomsFragment : Fragment() {

    companion object {
        fun newInstance() = ChatRoomsFragment()
    }

    private lateinit var viewModel: ChatRoomsViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.chat_rooms_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(ChatRoomsViewModel::class.java)
        // TODO: Use the ViewModel
    }

}

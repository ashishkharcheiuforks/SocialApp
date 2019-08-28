package com.example.socialapp.screens.main.invites


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.socialapp.databinding.FragmentInvitesBinding


class InvitesFragment : Fragment() {

    private lateinit var binding: FragmentInvitesBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentInvitesBinding.inflate(inflater, container, false)
        return binding.root
    }


}

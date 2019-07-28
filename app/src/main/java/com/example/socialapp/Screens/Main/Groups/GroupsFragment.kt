package com.example.socialapp.Screens.Main.Groups


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.socialapp.databinding.FragmentGroupsBinding


class GroupsFragment : Fragment() {

    private lateinit var binding: FragmentGroupsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentGroupsBinding.inflate(inflater, container, false)
        return binding.root
    }


}

package com.example.socialapp.screens.searchuser


import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.example.socialapp.adapter.SearchUserAdapter
import com.example.socialapp.databinding.FragmentSearchUserBinding
import timber.log.Timber

class SearchUserFragment : Fragment(), SearchUserAdapter.SearchUserItemListener {
    private lateinit var binding: FragmentSearchUserBinding

    private val viewModel: SearchUserViewmodel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentSearchUserBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupToolbar()

        binding.viewmodel = viewModel
        binding.lifecycleOwner = this

        viewModel.users.observe(this, Observer {
            val adapter = SearchUserAdapter(it, this)
            binding.recyclerview.adapter = adapter
            adapter.notifyDataSetChanged()
        })

    }

    override fun onSearchUserItemClick(userUid: String) {
        Timber.d("user clicked uid: $userUid")
        navigateToUserProfile(userUid)
    }

    private fun navigateToUserProfile(uid: String) {
        val action = SearchUserFragmentDirections.actionGlobalProfileFragment(uid)
        findNavController().navigate(action)
    }

    private fun setupToolbar() {
        val navController = findNavController()
        val appBarConfiguration = AppBarConfiguration(navController.graph)
        binding.toolbar.setupWithNavController(navController, appBarConfiguration)
    }
}

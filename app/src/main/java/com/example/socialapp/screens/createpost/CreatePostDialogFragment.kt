package com.example.socialapp.screens.createpost

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.example.socialapp.AuthenticatedNestedGraphViewModel
import com.example.socialapp.R
import com.example.socialapp.databinding.FragmentCreatePostBinding

class CreatePostFragment : Fragment(), Toolbar.OnMenuItemClickListener {

    private lateinit var binding: FragmentCreatePostBinding

    private val viewModel: CreatePostViewModel by viewModels()

    private val authenticatedNestedGraphViewModel: AuthenticatedNestedGraphViewModel by navGraphViewModels(
        R.id.authenticated_nested_graph
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCreatePostBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupToolbar()

        binding.lifecycleOwner = this
        binding.viewmodel = viewModel

        authenticatedNestedGraphViewModel.user.observe(viewLifecycleOwner, Observer {
            binding.user = it
        })

        viewModel.publishButtonEnabled.observe(this, Observer {
            binding.toolbar.menu.getItem(0).isEnabled = it
        })
    }


    override fun onMenuItemClick(item: MenuItem?): Boolean {
        return if (item?.itemId == R.id.action_publish_new_post) {
            viewModel.addPost()
            true
        } else false
    }

    private fun setupToolbar() {
        val navController = findNavController()
        val appBarConfiguration = AppBarConfiguration(navController.graph)
        binding.toolbar.setupWithNavController(navController, appBarConfiguration)
        binding.toolbar.inflateMenu(R.menu.menu_create_new_post)
        binding.toolbar.setOnMenuItemClickListener(this)
    }
}

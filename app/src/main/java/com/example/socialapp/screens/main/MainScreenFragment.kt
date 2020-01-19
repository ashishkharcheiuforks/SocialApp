package com.example.socialapp.screens.main


import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import androidx.viewpager2.widget.ViewPager2
import com.example.socialapp.AuthenticatedNestedGraphViewModel
import com.example.socialapp.R
import com.example.socialapp.adapter.MainScreenPagerAdapter
import com.example.socialapp.databinding.FragmentMainScreenBinding
import com.example.socialapp.databinding.NavHeaderMainBinding
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.fragment_main_screen.view.*


class MainScreenFragment : Fragment() {

    private val nestedGraphViewModel: AuthenticatedNestedGraphViewModel by navGraphViewModels(R.id.authenticated_graph)

    private lateinit var binding: FragmentMainScreenBinding
    private lateinit var navHeaderMainBinding: NavHeaderMainBinding

    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager: ViewPager2
    private lateinit var pagerAdapter: MainScreenPagerAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate Fragment
        binding = FragmentMainScreenBinding.inflate(inflater, container, false)
        // Inflate Navigation Drawer Header Layout
        navHeaderMainBinding =
            DataBindingUtil.inflate(inflater, R.layout.nav_header_main, binding.navView, false)
        // Add header view to the navigation drawer
        binding.navView.addHeaderView(navHeaderMainBinding.root)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.lifecycleOwner = this@MainScreenFragment

        nestedGraphViewModel.user.observe(viewLifecycleOwner, Observer {
            navHeaderMainBinding.user = it
        })

        setupToolbar()

        setupViewPagerWithTabLayout()

        setTabsIcons()

        binding.drawerLayout.nav_view.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.action_friends -> navigateToFriendsScreen()
                R.id.action_chat -> navigateToChatRoomsScreen()
                R.id.action_log_out -> signOut()
            }
            // Close drawer after selecting an item
            binding.drawerLayout.closeDrawer(binding.navView)
            true
        }
    }

    private fun setTabsIcons() {
        tabLayout.getTabAt(0)!!.setIcon(R.drawable.ic_invites_black_24dp)
        tabLayout.getTabAt(1)!!.setIcon(R.drawable.ic_home_black_24dp)
        tabLayout.getTabAt(2)!!.setIcon(R.drawable.ic_groups_black_24dp)
    }


    private fun setupToolbar() {
        // Inflate menu with search icon
        binding.toolbar.inflateMenu(R.menu.menu_main_screen)
        // Set navigation icon color to white
        binding.toolbar.navigationIcon?.setTint(Color.WHITE)

        val navController = findNavController()
        val appBarConfiguration = AppBarConfiguration(navController.graph, binding.drawerLayout)
        binding.toolbar.setupWithNavController(navController, appBarConfiguration)
        binding.toolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.action_search -> navigateToSearchUserScreen()
            }
            true
        }

        navHeaderMainBinding.ivProfilePicture.setOnClickListener {
            navigateToProfileScreen()
        }

    }

    private fun setupViewPagerWithTabLayout() {
        tabLayout = binding.tabLayout
        viewPager = binding.pager

        pagerAdapter = MainScreenPagerAdapter(this@MainScreenFragment)
        viewPager.adapter = pagerAdapter

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            // Here you can modify the tabs
        }.attach()

        // Start from the middle tab
        viewPager.currentItem = 1
    }

    private fun signOut() {
        FirebaseAuth.getInstance().signOut()
    }

    private fun navigateToSearchUserScreen() {
        val action = MainScreenFragmentDirections.actionMainFragmentToSearchUserFragment()
        findNavController().navigate(action)
    }

    private fun navigateToFriendsScreen() {
        val action = MainScreenFragmentDirections.actionMainFragmentToFriendsFragment()
        findNavController().navigate(action)
    }

    private fun navigateToProfileScreen() {
        val userId = nestedGraphViewModel.user.value?.uid
        val action = MainScreenFragmentDirections.actionGlobalProfileFragment(userId)
        findNavController().navigate(action)
    }

    private fun navigateToChatRoomsScreen() {
        val action = MainScreenFragmentDirections.actionMainFragmentToChatRoomsFragment()
        findNavController().navigate(action)
    }

}

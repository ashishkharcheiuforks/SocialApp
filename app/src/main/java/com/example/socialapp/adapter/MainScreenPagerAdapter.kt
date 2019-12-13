package com.example.socialapp.screens.main

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.socialapp.screens.main.Adverts.AdvertsFragment
import com.example.socialapp.screens.main.home.HomeFragment
import com.example.socialapp.screens.main.invites.InvitesFragment
import com.example.socialapp.screens.main.menu.MenuFragment
import com.example.socialapp.screens.main.notifications.NotificationsFragment

class MainScreenPagerAdapter(f: Fragment) :
    FragmentStateAdapter(f) {

    private val tabFragments = listOf(
        HomeFragment(),
        InvitesFragment(),
        AdvertsFragment(),
        NotificationsFragment(),
        MenuFragment()
    )

    override fun getItemCount(): Int {
        return tabFragments.size
    }

    override fun createFragment(position: Int): Fragment {
        return if (position in 0..4) {
            tabFragments[position]
        } else {
            Fragment()
        }
    }

}
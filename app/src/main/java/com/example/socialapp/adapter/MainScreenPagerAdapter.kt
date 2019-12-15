package com.example.socialapp.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.socialapp.screens.adverts.AdvertsFragment
import com.example.socialapp.screens.home.HomeFragment
import com.example.socialapp.screens.invites.InvitesFragment

class MainScreenPagerAdapter(f: Fragment) :
    FragmentStateAdapter(f) {

    private val tabFragments = listOf(
        InvitesFragment(),
        HomeFragment(),
        AdvertsFragment()
    )

    override fun getItemCount(): Int {
        return tabFragments.size
    }

    override fun createFragment(position: Int): Fragment {
        return if (position in 0..3) {
            tabFragments[position]
        } else {
            Fragment()
        }
    }

}
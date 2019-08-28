package com.example.socialapp.screens.main

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.example.socialapp.screens.main.groups.GroupsFragment
import com.example.socialapp.screens.main.home.HomeFragment
import com.example.socialapp.screens.main.invites.InvitesFragment
import com.example.socialapp.screens.main.menu.MenuFragment
import com.example.socialapp.screens.main.notifications.NotificationsFragment

class MainScreenPagerAdapter(fm: FragmentManager, private val numOfTabs: Int) : FragmentPagerAdapter(fm) {

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> HomeFragment()
            1 -> InvitesFragment()
            2 -> GroupsFragment()
            3 -> NotificationsFragment()
            4 -> MenuFragment()
            else -> Fragment()
        }
    }

    override fun getCount(): Int {
        return numOfTabs
    }
}
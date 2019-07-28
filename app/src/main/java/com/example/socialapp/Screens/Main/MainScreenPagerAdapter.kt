package com.example.socialapp.Screens.Main

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.example.socialapp.Screens.Main.Groups.GroupsFragment
import com.example.socialapp.Screens.Main.Home.HomeFragment
import com.example.socialapp.Screens.Main.Invites.InvitesFragment
import com.example.socialapp.Screens.Main.Menu.MenuFragment
import com.example.socialapp.Screens.Main.Notifications.NotificationsFragment

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
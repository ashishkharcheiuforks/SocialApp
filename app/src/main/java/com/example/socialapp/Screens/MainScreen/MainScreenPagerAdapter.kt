package com.example.socialapp.Screens.MainScreen

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

class MainScreenPagerAdapter(fm: FragmentManager, private val numOfTabs: Int) : FragmentPagerAdapter(fm) {

    override fun getItem(position: Int): Fragment {
        return Fragment()
    }

    override fun getCount(): Int {
        return numOfTabs
    }
}
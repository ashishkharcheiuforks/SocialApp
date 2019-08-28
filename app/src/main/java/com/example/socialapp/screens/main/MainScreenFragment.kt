package com.example.socialapp.screens.main


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.example.socialapp.R
import com.example.socialapp.databinding.FragmentMainScreenBinding
import com.google.android.material.tabs.TabLayout


class MainFragment : Fragment() {

    private lateinit var binding: FragmentMainScreenBinding

    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager: ViewPager
    private lateinit var pagerAdapter: MainScreenPagerAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMainScreenBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tabLayout = binding.tabLayout
        viewPager = binding.pager

        tabLayout.setupWithViewPager(viewPager, false)

        pagerAdapter = MainScreenPagerAdapter(childFragmentManager, tabLayout.tabCount)
        viewPager.adapter = pagerAdapter

        setIcons()
    }

    private fun setIcons() {
        tabLayout.getTabAt(0)!!.setIcon(R.drawable.ic_home_black_24dp)
        tabLayout.getTabAt(1)!!.setIcon(R.drawable.ic_invites_black_24dp)
        tabLayout.getTabAt(2)!!.setIcon(R.drawable.ic_groups_black_24dp)
        tabLayout.getTabAt(3)!!.setIcon(R.drawable.ic_notifications_black_24dp)
        tabLayout.getTabAt(4)!!.setIcon(R.drawable.ic_menu_black_24dp)
    }

}

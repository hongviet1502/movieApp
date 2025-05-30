package com.example.muv.ui.navigation

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.example.muv.R
import com.example.muv.ui.download.DownloadsFragment
import com.example.muv.ui.home.HomeFragment
import com.example.muv.ui.more.MoreFragment
import com.example.muv.ui.search.SearchFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.core.view.get

class NetflixBottomNavigationHelper(
    private val activity: FragmentActivity,
    private val viewPager: ViewPager2,
    private val bottomNav: BottomNavigationView
) {

    private val fragments = listOf(
        HomeFragment(),
        SearchFragment(),
        DownloadsFragment(),
        MoreFragment()
    )

    init {
        setupViewPager()
        setupBottomNavigation()
    }

    private fun setupViewPager() {
        viewPager.adapter = object : FragmentStateAdapter(activity) {
            override fun getItemCount(): Int = fragments.size
            override fun createFragment(position: Int): Fragment = fragments[position]
        }

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                bottomNav.menu[position].isChecked = true
            }
        })
    }

    private fun setupBottomNavigation() {
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    viewPager.currentItem = 0
                    true
                }
                R.id.nav_search -> {
                    viewPager.currentItem = 1
                    true
                }
                R.id.nav_downloads -> {
                    viewPager.currentItem = 2
                    true
                }
                R.id.nav_more -> {
                    viewPager.currentItem = 3
                    true
                }
                else -> false
            }
        }
    }

    fun getCurrentFragment(): Fragment? {
        return fragments.getOrNull(viewPager.currentItem)
    }
}
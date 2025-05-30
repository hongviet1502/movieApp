package com.example.muv.ui.main

import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.muv.R
import com.example.muv.databinding.ActivityMainBinding
import com.example.muv.ui.base.BaseActivity
import com.example.muv.ui.navigation.NetflixBottomNavigationHelper
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding, MainViewModel>() {

    override val layoutId: Int = R.layout.activity_main
    override val viewModelClass: Class<MainViewModel> = MainViewModel::class.java

    private lateinit var navigationHelper: NetflixBottomNavigationHelper

    override fun shouldShowBackButton(): Boolean = false

    override fun initView() {
        setupBottomNavigation()
    }

    override fun initData() {
        // Main data will be loaded by individual fragments
    }

    override fun initListener() {
        // Handle back press for fragments - FIXED VERSION
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val currentFragment = navigationHelper.getCurrentFragment()
                if (currentFragment is OnBackPressedHandler) {
                    if (!currentFragment.onBackPressed()) {
                        finish()
                    }
                } else {
                    finish()
                }
            }
        }
        onBackPressedDispatcher.addCallback(this, callback)
    }

    private fun setupBottomNavigation() {
        navigationHelper = NetflixBottomNavigationHelper(
            activity = this,
            viewPager = binding.viewPager,
            bottomNav = binding.bottomNavigation
        )
    }

    // Interface for fragments to handle back press
    interface OnBackPressedHandler {
        fun onBackPressed(): Boolean
    }
}
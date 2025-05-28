package com.example.muv

import android.app.Application
import android.util.Log
import dagger.hilt.android.HiltAndroidApp

/**
 * Application class cho Movie App
 * @HiltAndroidApp annotation báo cho Hilt biết đây là entry point của ứng dụng
 * Hilt sẽ tự động generate code và inject dependencies từ đây
 */
@HiltAndroidApp
class MovieApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        // Khởi tạo các thư viện cần thiết
        initializeLibraries()
    }

    private fun initializeLibraries() {
        // Có thể thêm các khởi tạo khác nếu cần:
        // - Timber for logging
        // - Firebase
        // - Crash reporting tools
        // - etc.

        if (BuildConfig.DEBUG) {
            // Enable debug logging hoặc debug tools
            enableDebugMode()
        }
    }

    private fun enableDebugMode() {
        // Có thể thêm Timber hoặc logging khác
        // Timber.plant(Timber.DebugTree())
        // Debug configuration
        android.util.Log.d("MovieApp", "Debug mode enabled")
    }
}
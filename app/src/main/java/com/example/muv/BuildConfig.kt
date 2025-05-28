package com.example.muv

class BuildConfig {
    companion object {
        // API Configuration
        const val API_KEY = "your_tmdb_api_key_here"
        const val BASE_URL = "https://api.themoviedb.org/3/"
        const val IMAGE_BASE_URL = "https://image.tmdb.org/t/p/w500"

        // Build Configuration
        const val DEBUG = true // Set to false for release build
        const val APPLICATION_ID = "com.movieapp"
        const val VERSION_CODE = 1
        const val VERSION_NAME = "1.0"

        // SDK Versions
        const val MIN_SDK_VERSION = 24
        const val TARGET_SDK_VERSION = 34
        const val COMPILE_SDK_VERSION = 34
    }
}
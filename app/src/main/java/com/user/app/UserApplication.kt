package com.user.app

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

/**
 * Custom Application class for Hilt dependency injection and global initialization.
 *
 * Initializes Timber logging framework. Hilt handles dependency management.
 */
@HiltAndroidApp
class UserApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        // Initialize Timber logging - only for debug builds
        Timber.plant(Timber.DebugTree())
        Timber.d("UserApplication initialized - Debug logging enabled")
    }
}

package com.user.app

import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33], manifest = Config.NONE)
class UserApplicationTest {

    @Test
    fun testApplicationInitialization() {
        val app = ApplicationProvider.getApplicationContext<UserApplication>()

        // Verify that the application initializes correctly with Hilt
        assertNotNull("Application should be initialized", app)
    }
}

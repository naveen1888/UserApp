package com.user.app.ui

import androidx.lifecycle.ViewModel
import com.user.app.data.repository.AuthRepository
import com.user.app.data.repository.UserRepository
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations

class UserViewModelFactoryTest {

    @Mock
    private lateinit var userRepository: UserRepository

    @Mock
    private lateinit var authRepository: AuthRepository

    private lateinit var factory: UserViewModelFactory

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        factory = UserViewModelFactory(userRepository, authRepository)
    }

    @Test
    fun create_returnsUserViewModel() {
        val viewModel = factory.create(UserViewModel::class.java)
        assertNotNull(viewModel)
    }

    @Test(expected = IllegalArgumentException::class)
    fun create_throwsExceptionForUnknownViewModel() {
        @Suppress("UNCHECKED_CAST")
        factory.create(UnknownViewModel::class.java as Class<ViewModel>)
    }

    private class UnknownViewModel : ViewModel()
}

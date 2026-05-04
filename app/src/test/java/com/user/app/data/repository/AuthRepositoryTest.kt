package com.user.app.data.repository

import com.user.app.data.remote.api.AuthService
import com.user.app.data.remote.model.LoginRequest
import com.user.app.data.remote.model.LoginResponse
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import retrofit2.Response

class AuthRepositoryTest {

    @Mock
    private lateinit var authService: AuthService

    private lateinit var authRepository: AuthRepository

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        authRepository = AuthRepository(authService)
    }

    @Test
    fun login_callsAuthServiceAndReturnsResponse() = runTest {
        val request = LoginRequest("testuser", "password")
        val expectedResponse = Response.success(
            LoginResponse(
                id = 1,
                username = "testuser",
                email = "test@example.com",
                firstName = "Test",
                lastName = "User",
                gender = "male",
                image = "url",
                accessToken = "access",
                refreshToken = "refresh"
            )
        )

        Mockito.`when`(authService.login(request)).thenReturn(expectedResponse)

        val result = authRepository.login(request)

        Assert.assertEquals(expectedResponse, result)
        Mockito.verify(authService).login(request)
    }
}
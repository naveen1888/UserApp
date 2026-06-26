package com.user.app.features.auth.presentation.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.Snapshot
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.user.app.R
import com.user.app.features.auth.presentation.viewmodel.LoginViewModel
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map

/**
 * Composable that represents the Login screen of the application.
 *
 * This screen displays a login form with username and password fields, overlaid on a background image.
 * It integrates with [LoginViewModel] to perform remote authentication.
 *
 * @param viewModel The ViewModel handling login logic and state.
 * @param onLoginSuccess Callback triggered when the user successfully logs in.
 * It passes the user's email or username as an argument.
 */
@Composable
fun LoginScreen(
    viewModel: LoginViewModel = hiltViewModel(),
    onLoginSuccess: (String) -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    // Navigate on successful login
    LaunchedEffect(state.isLoggedIn) {
        if (state.isLoggedIn && state.loginResult != null) {
            onLoginSuccess(state.loginResult?.username ?: "")

            viewModel.onUsernameChanged(state.username)
        }
    }

    LoginContent(
        username = state.username,
        password = state.password,
        isLoggingIn = state.isLoading,
        error = state.error,
        usernameError = state.usernameError,
        onUsernameChanged = { viewModel.onUsernameChanged(it) },
        onPasswordChanged = { viewModel.onPasswordChanged(it) },
        onLogin = { viewModel.login() },
        onClearError = { viewModel.clearError() }
    )
}

/**
 * Stateless version of the Login screen for easier testing and previews.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginContent(
    username: String,
    password: String,
    isLoggingIn: Boolean,
    error: com.user.app.core.error.AppError?,
    usernameError: String? = null,
    onUsernameChanged: (String) -> Unit,
    onPasswordChanged: (String) -> Unit,
    onLogin: () -> Unit,
    onClearError: () -> Unit
) {

    Box(modifier = Modifier.fillMaxSize()) {
        // Background Image
        Image(
            painter = painterResource(id = R.drawable.ic_launcher_background),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.FillBounds
        )

        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                CenterAlignedTopAppBar(
                    title = { Text("Login") },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = Color.Transparent
                    )
                )
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth().padding(8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f)
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        error?.let {
                            Text(
                                text = it.getDisplayMessage(),
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                        }

                        OutlinedTextField(
                            value = username,
                            onValueChange = {
                                onUsernameChanged(it)
                                onClearError()
                            },
                            label = { Text("Username") },
                            isError = usernameError != null,
                            supportingText = { usernameError?.let { Text(it) } },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                            modifier = Modifier.fillMaxWidth().testTag("UsernameInput"),
                            enabled = !isLoggingIn
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = password,
                            onValueChange = { 
                                onPasswordChanged(it)
                                onClearError()
                            },
                            label = { Text("Password") },
                            visualTransformation = PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            modifier = Modifier.fillMaxWidth().testTag("PasswordInput"),
                            enabled = !isLoggingIn
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = onLogin,
                            modifier = Modifier.fillMaxWidth().testTag("LoginButton"),
                            enabled = !isLoggingIn
                        ) {
                            if (isLoggingIn) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(24.dp),
                                    strokeWidth = 2.dp,
                                    color = MaterialTheme.colorScheme.onPrimary
                                )
                            } else {
                                Text("Login")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    LoginContent(
        username = "testuser",
        password = "password",
        isLoggingIn = false,
        error = null,
        onUsernameChanged = {},
        onPasswordChanged = {},
        onLogin = {},
        onClearError = {}
    )
}

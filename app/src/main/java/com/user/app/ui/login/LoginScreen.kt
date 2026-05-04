package com.user.app.ui.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import com.user.app.Constants
import com.user.app.R
import com.user.app.data.remote.model.LoginRequest
import com.user.app.ui.UserError
import com.user.app.ui.UserViewModel

/**
 * Composable that represents the Login screen of the application.
 *
 * This screen displays a login form with email and password fields, overlaid on a background image.
 * It integrates with [UserViewModel] to perform remote authentication.
 *
 * @param viewModel The ViewModel handling login logic and state.
 * @param onLoginSuccess Callback triggered when the user successfully logs in.
 * It passes the user's email or username as an argument.
 */
@Composable
fun LoginScreen(
    viewModel: UserViewModel = hiltViewModel(),
    onLoginSuccess: (String) -> Unit
) {
    val isLoggingIn by viewModel.isLoggingIn.collectAsState()
    val error by viewModel.error.collectAsState()

    LoginContent(
        isLoggingIn = isLoggingIn,
        error = error,
        onLogin = { email, password ->
            viewModel.login(
                LoginRequest(userName = email, password = password),
                onComplete = onLoginSuccess
            )
        },
        onClearError = { viewModel.clearError() }
    )
}

/**
 * Stateless version of the Login screen for easier testing and previews.
 * Refactored from the original LoginScreen to resolve ViewModel instantiation issues in Previews.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginContent(
    isLoggingIn: Boolean,
    error: UserError?,
    onLogin: (String, String) -> Unit,
    onClearError: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var emailError by remember { mutableStateOf<String?>(null) }

    fun validateEmail(value: String): Boolean {
        return when {
            value.isBlank() -> {
                emailError = Constants.ERROR_USERNAME_EMPTY
                false
            }
            else -> {
                emailError = null
                true
            }
        }
    }

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
                            value = email,
                            onValueChange = { 
                                email = it
                                onClearError()
                                if (emailError != null) validateEmail(it)
                            },
                            label = { Text("Username") },
                            isError = emailError != null,
                            supportingText = { emailError?.let { Text(it) } },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                            modifier = Modifier.fillMaxWidth().testTag("UsernameInput"),
                            enabled = !isLoggingIn
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = password,
                            onValueChange = { 
                                password = it
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
                            onClick = {
                                if (validateEmail(email)) {
                                    onLogin(email, password)
                                }
                            },
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
    // Fixed: Using LoginContent directly to avoid ViewModel instantiation in Preview
    LoginContent(
        isLoggingIn = false,
        error = null,
        onLogin = { _, _ -> },
        onClearError = {}
    )
}

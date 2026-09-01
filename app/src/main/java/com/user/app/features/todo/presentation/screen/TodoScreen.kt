package com.user.app.features.todo.presentation.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.user.app.features.todo.presentation.intent.TodoIntent
import com.user.app.features.todo.presentation.state.TodoState
import com.user.app.features.todo.presentation.viewmodel.TodoViewModel

@Composable
fun TodoScreen(
    viewModel: TodoViewModel = hiltViewModel(),
    onLoginSuccess: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(state.isLoggedIn) {
        if (state.isLoggedIn) {
            onLoginSuccess()
        }
    }

    TodoContent(
        state = state,
        onIntent = { viewModel.processIntent(it) }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoContent(
    state: TodoState,
    onIntent: (TodoIntent) -> Unit
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Todo Login") }
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
                modifier = Modifier.fillMaxWidth().padding(8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    state.error?.let {
                        Text(
                            text = it.getDisplayMessage(),
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }

                    OutlinedTextField(
                        value = state.username,
                        onValueChange = {
                            onIntent(TodoIntent.OnUsernameChanged(it))
                            onIntent(TodoIntent.OnClearError)
                        },
                        label = { Text("Username") },
                        isError = state.usernameError != null,
                        supportingText = { state.usernameError?.let { Text(it) } },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        modifier = Modifier.fillMaxWidth().testTag("TodoUsernameInput"),
                        enabled = !state.isLoading
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = state.password,
                        onValueChange = {
                            onIntent(TodoIntent.OnPasswordChanged(it))
                            onIntent(TodoIntent.OnClearError)
                        },
                        label = { Text("Password") },
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        modifier = Modifier.fillMaxWidth().testTag("TodoPasswordInput"),
                        enabled = !state.isLoading
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { onIntent(TodoIntent.OnLoginClicked) },
                        modifier = Modifier.fillMaxWidth().testTag("TodoLoginButton"),
                        enabled = state.isActionEnabled
                    ) {
                        if (state.isLoading) {
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

@Preview(showBackground = true)
@Composable
fun TodoScreenPreview() {
    TodoContent(
        state = TodoState(username = "test@example.com"),
        onIntent = {}
    )
}

package com.user.app.features.user_management.presentation.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.user.app.features.user_management.presentation.viewmodel.UserManagementViewModel

@Composable
fun AddUserScreen(
    viewModel: UserManagementViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val state by viewModel.addUserState.collectAsState()

    // Navigate back when user is successfully added
    LaunchedEffect(state.isSuccess) {
        if (state.isSuccess) {
            onNavigateBack()
            viewModel.resetAddUserForm()
        }
    }

    AddUserContent(
        name = state.name,
        email = state.email,
        age = state.age,
        isLoading = state.isLoading,
        error = state.error,
        nameError = state.nameError,
        emailError = state.emailError,
        ageError = state.ageError,
        onNameChanged = { viewModel.onNameChanged(it) },
        onEmailChanged = { viewModel.onEmailChanged(it) },
        onAgeChanged = { viewModel.onAgeChanged(it) },
        onAddUser = { viewModel.addUser() },
        onClearError = { viewModel.clearAddUserError() }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddUserContent(
    name: String,
    email: String,
    age: String,
    isLoading: Boolean,
    error: com.user.app.core.error.AppError? = null,
    nameError: String? = null,
    emailError: String? = null,
    ageError: String? = null,
    onNameChanged: (String) -> Unit,
    onEmailChanged: (String) -> Unit,
    onAgeChanged: (String) -> Unit,
    onAddUser: () -> Unit,
    onClearError: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Add User") })
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            error?.let {
                Text(
                    text = it.getDisplayMessage(),
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(bottom = 8.dp).testTag("GeneralError")
                )
            }

            OutlinedTextField(
                value = name,
                onValueChange = {
                    onClearError()
                    onNameChanged(it)
                },
                label = { Text("Name") },
                isError = nameError != null,
                supportingText = { nameError?.let { Text(it) } },
                modifier = Modifier.fillMaxWidth().testTag("NameInput"),
                enabled = !isLoading
            )
            OutlinedTextField(
                value = email,
                onValueChange = { 
                    onClearError()
                    onEmailChanged(it)
                },
                label = { Text("Email") },
                isError = emailError != null,
                supportingText = { emailError?.let { Text(it) } },
                modifier = Modifier.fillMaxWidth().testTag("EmailInput"),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                enabled = !isLoading
            )
            OutlinedTextField(
                value = age,
                onValueChange = { 
                    onClearError()
                    onAgeChanged(it)
                },
                label = { Text("Age") },
                isError = ageError != null,
                supportingText = { ageError?.let { Text(it) } },
                modifier = Modifier.fillMaxWidth().testTag("AgeInput"),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                enabled = !isLoading
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = onAddUser,
                modifier = Modifier.fillMaxWidth().testTag("SaveButton"),
                enabled = !isLoading && nameError == null && emailError == null && ageError == null &&
                         name.isNotEmpty() && email.isNotEmpty() && age.isNotEmpty()
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp).testTag("ProgressIndicator"),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("Save")
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AddUserScreenPreview() {
    AddUserContent(
        name = "",
        email = "",
        age = "",
        isLoading = false,
        onNameChanged = {},
        onEmailChanged = {},
        onAgeChanged = {},
        onAddUser = {},
        onClearError = {}
    )
}

package com.user.app.ui.adduser

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
import com.user.app.Constants
import com.user.app.ui.UserError
import com.user.app.ui.UserViewModel
import com.user.app.util.InputSanitizer

@Composable
fun AddUserScreen(
    viewModel: UserViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val isSaving by viewModel.isSaving.collectAsState()
    val error by viewModel.error.collectAsState()

    AddUserContent(
        isSaving = isSaving,
        errorMessage = error?.getDisplayMessage(),
        onAddUser = { name, email, age ->
            viewModel.addUser(name.trim(), email, age) {
                onNavigateBack()
            }
        },
        onInputChange = { viewModel.clearError() },
        onDispose = { viewModel.stopAllOperations() }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddUserContent(
    isSaving: Boolean,
    errorMessage: String? = null,
    onAddUser: (String, String, Int) -> Unit,
    onInputChange: () -> Unit = {},
    onDispose: () -> Unit = {}
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }

    var nameError by remember { mutableStateOf<String?>(null) }
    var emailError by remember { mutableStateOf<String?>(null) }
    var ageError by remember { mutableStateOf<String?>(null) }

    fun validateName(value: String): Boolean {
        return when {
            value.isBlank() -> {
                nameError = Constants.ERROR_NAME_EMPTY
                false
            }
            value.length > Constants.MAX_NAME_LENGTH -> {
                nameError = Constants.ERROR_NAME_TOO_LONG
                false
            }
            !value.all { it.isLetter() || it.isWhitespace() } -> {
                nameError = Constants.ERROR_NAME_INVALID
                false
            }
            else -> {
                nameError = null
                true
            }
        }
    }

    fun validateEmail(value: String): Boolean {
        return when {
            value.isBlank() -> {
                emailError = Constants.ERROR_EMAIL_EMPTY
                false
            }
            value.length > Constants.MAX_EMAIL_LENGTH -> {
                emailError = Constants.ERROR_EMAIL_TOO_LONG
                false
            }
            !Constants.EMAIL_REGEX.matches(value) -> {
                emailError = Constants.ERROR_EMAIL_INVALID
                false
            }
            else -> {
                emailError = null
                true
            }
        }
    }

    fun validateAge(value: String): Boolean {
        val ageInt = value.toIntOrNull()
        return when {
            value.isBlank() -> {
                ageError = Constants.ERROR_AGE_EMPTY
                false
            }
            !value.all { it.isDigit() } -> {
                ageError = Constants.ERROR_AGE_INVALID
                false
            }
            ageInt == null || ageInt < Constants.MIN_AGE || ageInt > Constants.MAX_AGE -> {
                ageError = Constants.ERROR_AGE_OUT_OF_RANGE
                false
            }
            else -> {
                ageError = null
                true
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            onDispose()
        }
    }

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
            errorMessage?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(bottom = 8.dp).testTag("GeneralError")
                )
            }

            OutlinedTextField(
                value = name,
                onValueChange = { input ->
                    onInputChange()
                    val sanitized = InputSanitizer.sanitizeName(input)
                    if (sanitized.length <= Constants.MAX_NAME_LENGTH) {
                        name = sanitized
                        if (nameError != null) validateName(sanitized)
                    }
                },
                label = { Text("Name") },
                isError = nameError != null,
                supportingText = { nameError?.let { Text(it) } },
                modifier = Modifier.fillMaxWidth().testTag("NameInput"),
                enabled = !isSaving
            )
            OutlinedTextField(
                value = email,
                onValueChange = { 
                    onInputChange()
                    val sanitized = InputSanitizer.sanitizeEmail(it)
                    if (sanitized.length <= Constants.MAX_EMAIL_LENGTH) {
                        email = sanitized
                        if (emailError != null) validateEmail(sanitized)
                    }
                },
                label = { Text("Email") },
                isError = emailError != null || (errorMessage != null && errorMessage.contains("Email", ignoreCase = true)),
                supportingText = { emailError?.let { Text(it) } },
                modifier = Modifier.fillMaxWidth().testTag("EmailInput"),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                enabled = !isSaving
            )
            OutlinedTextField(
                value = age,
                onValueChange = { 
                    onInputChange()
                    val sanitized = InputSanitizer.sanitizeAge(it)
                    age = sanitized
                    if (ageError != null) validateAge(sanitized)
                },
                label = { Text("Age") },
                isError = ageError != null,
                supportingText = { ageError?.let { Text(it) } },
                modifier = Modifier.fillMaxWidth().testTag("AgeInput"),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                enabled = !isSaving
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    val isNameValid = validateName(name)
                    val isEmailValid = validateEmail(email)
                    val isAgeValid = validateAge(age)

                    if (isNameValid && isEmailValid && isAgeValid) {
                        onAddUser(name, email, age.toInt())
                    }
                },
                modifier = Modifier.fillMaxWidth().testTag("SaveButton"),
                enabled = !isSaving
            ) {
                if (isSaving) {
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
        isSaving = false,
        onAddUser = { _, _, _ -> }
    )
}

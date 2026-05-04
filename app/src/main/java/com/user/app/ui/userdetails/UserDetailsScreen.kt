package com.user.app.ui.userdetails

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.user.app.data.local.entity.User
import com.user.app.ui.UserViewModel

@Composable
fun UserDetailsScreen(
    userId: Int,
    viewModel: UserViewModel,
    onNavigateBack: () -> Unit
) {
    val user by viewModel.getUserById(userId).collectAsState(initial = null)
    UserDetailsContent(user = user, onNavigateBack = onNavigateBack)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserDetailsContent(
    user: User?,
    onNavigateBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("User Details") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        user?.let {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(text = "Name: ${it.name}", style = MaterialTheme.typography.headlineMedium)
                Text(text = "Email: ${it.email}", style = MaterialTheme.typography.bodyLarge)
                Text(text = "Age: ${it.age}", style = MaterialTheme.typography.bodyLarge)
            }
        } ?: Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentAlignment = androidx.compose.ui.Alignment.Center
        ) {
            CircularProgressIndicator(modifier = Modifier.testTag("LoadingIndicator"))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun UserDetailsScreenPreview() {
    UserDetailsContent(
        user = User(id = 1, name = "Jane Doe", email = "jane.doe@example.com", age = 28),
        onNavigateBack = {}
    )
}

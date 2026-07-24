package com.user.app.features.user_management.presentation.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.user.app.core.util.Constants
import com.user.app.features.user_management.domain.model.User
import com.user.app.core.error.AppError
import com.user.app.features.user_management.presentation.components.UserItem
import com.user.app.features.user_management.presentation.viewmodel.UserManagementViewModel

@Composable
fun UserListScreen(
    viewModel: UserManagementViewModel = hiltViewModel(),
    onUserClick: (Int) -> Unit,
    onAddUserClick: () -> Unit
) {
    val state by viewModel.userListState.collectAsState()
    UserListContent(
        users = state.users,
        isLoading = state.isLoading,
        error = state.error,
        onUserClick = onUserClick,
        onAddUserClick = onAddUserClick
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserListContent(
    users: List<User>,
    isLoading: Boolean = false,
    error: AppError? = null,
    onUserClick: (Int) -> Unit,
    onAddUserClick: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("User List") })
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddUserClick) {
                Icon(Icons.Default.Add, contentDescription = "Add User")
            }
        }
    ) { padding ->
        if (isLoading && users.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (error != null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text(text = error.getDisplayMessage(), color = MaterialTheme.colorScheme.error)
            }
        } else if (users.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text(text = Constants.EMPTY_USER_LIST_MESSAGE, style = MaterialTheme.typography.bodyLarge)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                items(users) { user ->
                    // Map feature User to domain User for UserItem
                    val domainUser = User(
                        id = user.id,
                        name = user.name,
                        email = user.email,
                        age = user.age
                    )
                    UserItem(user = domainUser, onClick = { onUserClick(user.id) })
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun UserListPreview() {
    UserListContent(
        users = listOf(
            User(1, "John Doe", "john@example.com", 30),
            User(2, "Jane Smith", "jane@example.com", 25)
        ),
        onUserClick = {},
        onAddUserClick = {}
    )
}

@Preview(showBackground = true)
@Composable
fun UserListEmptyPreview() {
    UserListContent(
        users = emptyList(),
        onUserClick = {},
        onAddUserClick = {}
    )
}

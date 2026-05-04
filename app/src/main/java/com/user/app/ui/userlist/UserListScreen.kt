package com.user.app.ui.userlist

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
import com.user.app.Constants
import com.user.app.data.local.entity.User
import com.user.app.ui.UserViewModel
import com.user.app.ui.components.UserItem

@Composable
fun UserListScreen(
    viewModel: UserViewModel = hiltViewModel(),
    onUserClick: (Int) -> Unit,
    onAddUserClick: () -> Unit
) {
    val users by viewModel.allUsers.collectAsState()
    UserListContent(
        users = users,
        onUserClick = onUserClick,
        onAddUserClick = onAddUserClick
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserListContent(
    users: List<User>,
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
        if (users.isEmpty()) {
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
                    UserItem(user = user, onClick = { onUserClick(user.id) })
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

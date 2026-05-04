package com.user.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.user.app.data.local.pref.UserPreferencesRepository
import com.user.app.ui.adduser.AddUserScreen
import com.user.app.ui.login.LoginScreen
import com.user.app.ui.userdetails.UserDetailsScreen
import com.user.app.ui.userlist.UserListScreen
import com.user.app.ui.UserViewModel
import com.user.app.ui.theme.MyApplicationTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * The main entry point of the User Management application.
 *
 * This activity sets up the Compose-based UI and delegates navigation
 * to the [UserApp] composable.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var userPreferencesRepository: UserPreferencesRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                UserApp(userPreferencesRepository)
            }
        }
    }
}

/**
 * Root composable that manages the application's navigation stack.
 *
 * It uses [NavHost] to switch between different screens:
 * - Login: Initial destination.
 * - User List: Displaying all registered users.
 * - Add User: Form to create a new user.
 * - User Details: Detailed view of a single user.
 */
@Composable
fun UserApp(
    userPreferencesRepository: UserPreferencesRepository,
    viewModel: UserViewModel = hiltViewModel()
) {
    val navController = rememberNavController()
    val isLoggedIn = userPreferencesRepository.isLoggedIn.collectAsState(initial = false).value
    val scope = rememberCoroutineScope()
    NavHost(
        navController = navController,
        startDestination = if (isLoggedIn) Constants.NAV_ROUTE_USER_LIST else Constants.NAV_ROUTE_LOGIN
    ) {
        composable(Constants.NAV_ROUTE_LOGIN) {
            LoginScreen(
                viewModel = viewModel,
                onLoginSuccess = { email ->
                    scope.launch {
                        userPreferencesRepository.saveLoginStatus(true, email)
                        navController.navigate(Constants.NAV_ROUTE_USER_LIST) {
                            popUpTo(Constants.NAV_ROUTE_LOGIN) { inclusive = true }
                        }
                    }
                }
            )
        }
        composable(Constants.NAV_ROUTE_USER_LIST) {
            UserListScreen(
                viewModel = viewModel,
                onUserClick = { userId ->
                    navController.navigate("${Constants.NAV_ROUTE_USER_DETAILS}/$userId")
                },
                onAddUserClick = {
                    navController.navigate(Constants.NAV_ROUTE_ADD_USER)
                }
            )
        }
        composable(Constants.NAV_ROUTE_ADD_USER) {
            AddUserScreen(
                viewModel = viewModel,
                onNavigateBack = {
                    // Navigate to user list after adding or when going back
                    navController.navigate(Constants.NAV_ROUTE_USER_LIST) {
                        popUpTo(Constants.NAV_ROUTE_ADD_USER) { inclusive = true }
                    }
                }
            )
        }
        composable(
            route = "${Constants.NAV_ROUTE_USER_DETAILS}/{${Constants.NAV_ARG_USER_ID}}",
            arguments = listOf(navArgument(Constants.NAV_ARG_USER_ID) { type = NavType.IntType })
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getInt(Constants.NAV_ARG_USER_ID) ?: return@composable
            UserDetailsScreen(
                userId = userId,
                viewModel = viewModel,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}

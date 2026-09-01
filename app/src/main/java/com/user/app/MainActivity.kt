package com.user.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.user.app.core.domain.usecase.GetLoginStatusUseCase
import com.user.app.core.navigation.NavigationConstants
import com.user.app.core.theme.MyApplicationTheme
import com.user.app.features.auth.presentation.screen.LoginScreen
import com.user.app.features.auth.presentation.viewmodel.LoginViewModel
import com.user.app.features.todo.presentation.screen.TodoScreen
import com.user.app.features.user_management.presentation.screen.AddUserScreen
import com.user.app.features.user_management.presentation.screen.UserDetailsScreen
import com.user.app.features.user_management.presentation.screen.UserListScreen
import com.user.app.features.user_management.presentation.viewmodel.UserManagementViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var getLoginStatusUseCase: GetLoginStatusUseCase
    private val viewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                UserApp()
            }
        }
    }
}

@Composable
fun UserApp(
    loginViewModel: LoginViewModel = hiltViewModel(),
    userViewModel: UserManagementViewModel = hiltViewModel()
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = NavigationConstants.ROUTE_TODO
    ) {
        composable(NavigationConstants.ROUTE_LOGIN) {
            LoginScreen(
                viewModel = loginViewModel,
                onLoginSuccess = {
                    navController.navigate(NavigationConstants.ROUTE_USER_LIST) {
                        popUpTo(NavigationConstants.ROUTE_LOGIN) { inclusive = true }
                    }
                }
            )
        }

        composable(NavigationConstants.ROUTE_TODO) {
            TodoScreen(
                onLoginSuccess = {
                    navController.navigate(NavigationConstants.ROUTE_USER_LIST) {
                        popUpTo(NavigationConstants.ROUTE_TODO) { inclusive = true }
                    }
                }
            )
        }

        composable(NavigationConstants.ROUTE_USER_LIST) {
            UserListScreen(
                viewModel = userViewModel,
                onUserClick = { userId ->
                    navController.navigate("${NavigationConstants.ROUTE_USER_DETAILS}/$userId")
                },
                onAddUserClick = {
                    navController.navigate(NavigationConstants.ROUTE_ADD_USER)
                }
            )
        }

        composable(NavigationConstants.ROUTE_ADD_USER) {
            AddUserScreen(
                viewModel = userViewModel,
                onNavigateBack = {
                    navController.navigate(NavigationConstants.ROUTE_USER_LIST) {
                        popUpTo(NavigationConstants.ROUTE_ADD_USER) { inclusive = true }
                    }
                }
            )
        }

        composable(
            route = "${NavigationConstants.ROUTE_USER_DETAILS}/{${NavigationConstants.ARG_USER_ID}}",
            arguments = listOf(navArgument(NavigationConstants.ARG_USER_ID) { type = NavType.IntType })
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getInt(NavigationConstants.ARG_USER_ID) ?: return@composable
            UserDetailsScreen(
                userId = userId,
                viewModel = userViewModel,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}

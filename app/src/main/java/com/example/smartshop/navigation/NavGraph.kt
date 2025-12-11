package com.example.smartshop.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.smartshop.ui.home.HomeScreen
import com.example.smartshop.ui.signin.SignInScreen
import com.example.smartshop.ui.signup.SignUpScreen
import com.example.smartshop.viewmodel.AuthViewModel

@Composable
fun NavGraph(navController: NavHostController, authViewModel: AuthViewModel) {
    NavHost(navController = navController, startDestination = "signin") {
        composable("signin") {
            SignInScreen(viewModel = authViewModel, onSignedIn = { navController.navigate("home") { popUpTo("signin") { inclusive = true } } },
                onGoToSignUp = { navController.navigate("signup") })
        }
        composable("signup") {
            SignUpScreen(viewModel = authViewModel, onSignedUp = { navController.navigate("home") { popUpTo("signup") { inclusive = true } } })
        }
        composable("home") {
            HomeScreen(onSignOut = { authViewModel.signOut(); navController.navigate("signin") { popUpTo("home") { inclusive = true } } })
        }
    }
}

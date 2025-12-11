package com.example.smartshop

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.smartshop.ui.home.HomeScreen
import com.example.smartshop.ui.signin.SignInScreen
import com.example.smartshop.ui.signup.SignUpScreen
import com.example.smartshop.viewmodel.AuthViewModel
import com.google.firebase.FirebaseApp

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize Firebase
        FirebaseApp.initializeApp(this)

        setContent {
            MaterialTheme {
                val navController = rememberNavController()
                val authViewModel: AuthViewModel = viewModel()

                NavHost(navController = navController, startDestination = "signin") {

                    // SignIn Screen
                    composable("signin") {
                        val authSuccess = authViewModel.authSuccess.collectAsState().value
                        if (authSuccess) {
                            navController.navigate("home") {
                                popUpTo("signin") { inclusive = true }
                            }
                        }
                        SignInScreen(
                            viewModel = authViewModel,
                            onSignedIn = {
                                navController.navigate("home") {
                                    popUpTo("signin") { inclusive = true }
                                }
                            },
                            onGoToSignUp = {
                                navController.navigate("signup")
                            }
                        )
                    }

                    // SignUp Screen
                    composable("signup") {
                        val authSuccess = authViewModel.authSuccess.collectAsState().value
                        if (authSuccess) {
                            navController.navigate("home") {
                                popUpTo("signup") { inclusive = true }
                            }
                        }
                        SignUpScreen(
                            viewModel = authViewModel,
                            onSignedUp = {
                                navController.navigate("home") {
                                    popUpTo("signup") { inclusive = true }
                                }
                            }
                        )
                    }

                    // Home Screen
                    composable("home") {
                        HomeScreen(
                            onSignOut = {
                                authViewModel.signOut()
                                navController.navigate("signin") {
                                    popUpTo("home") { inclusive = true }
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

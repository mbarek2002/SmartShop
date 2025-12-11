package com.example.smartshop

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.navigation.compose.rememberNavController
import com.example.smartshop.navigation.NavGraph
import com.example.smartshop.viewmodel.AuthViewModel
import com.google.firebase.FirebaseApp

class MainActivity : ComponentActivity() {
    @SuppressLint("ViewModelConstructorInComposable")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize Firebase
        FirebaseApp.initializeApp(this)

        setContent {
            MaterialTheme {
                val navController = rememberNavController()
                val authViewModel = AuthViewModel()
                NavGraph(navController = navController, authViewModel = authViewModel)
            }
        }
    }
}

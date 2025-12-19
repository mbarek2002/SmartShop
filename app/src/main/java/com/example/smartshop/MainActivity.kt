package com.example.smartshop

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.rememberNavController
import com.example.smartshop.data.PreferencesManager
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
                SmartShopApp()
            }
        }
    }
}

@Composable
fun SmartShopApp() {
    val context = LocalContext.current
    val preferencesManager = remember { PreferencesManager(context) }
    val navController = rememberNavController()
    val authViewModel = remember { AuthViewModel(preferencesManager = preferencesManager) }
    
    NavGraph(navController = navController, authViewModel = authViewModel)
}

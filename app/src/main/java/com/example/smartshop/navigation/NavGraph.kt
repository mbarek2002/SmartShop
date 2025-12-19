package com.example.smartshop.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.room.Room
import com.example.smartshop.data.local.AppDatabase
import com.example.smartshop.data.cloud.OrderFirestore
import com.example.smartshop.data.cloud.ProductFirestore
import com.example.smartshop.data.repository.OrderRepository
import com.example.smartshop.data.repository.ProductRepository
import com.example.smartshop.ui.main.MainScreen
import com.example.smartshop.ui.order.OrderScreen
import com.example.smartshop.ui.product.ProductFormScreen
import com.example.smartshop.ui.signin.SignInScreen
import com.example.smartshop.ui.signup.SignUpScreen
import com.example.smartshop.viewmodel.AuthViewModel
import com.example.smartshop.viewmodel.OrderViewModel
import com.example.smartshop.viewmodel.ProductViewModel

@Composable
fun NavGraph(
    navController: NavHostController,
    authViewModel: AuthViewModel
) {
    // Check if user is already logged in to determine start destination
    val isLoggedIn = authViewModel.isUserLoggedIn()
    val startDestination = if (isLoggedIn) "home" else "signin"

    NavHost(navController = navController, startDestination = startDestination) {

        // Sign In
        composable("signin") {
            SignInScreen(
                viewModel = authViewModel,
                onSignedIn = {
                    navController.navigate("home") {
                        popUpTo("signin") { inclusive = true }
                    }
                },
                onGoToSignUp = { navController.navigate("signup") }
            )
        }

        // Sign Up
        composable("signup") {
            SignUpScreen(
                viewModel = authViewModel,
                onSignedUp = {
                    navController.navigate("home") {
                        popUpTo("signup") { inclusive = true }
                    }
                }
            )
        }

        // Main Screen with Top Navigation
        composable("home") {
            val context = LocalContext.current
            val db = Room.databaseBuilder(context, AppDatabase::class.java, "db")
                .fallbackToDestructiveMigration() // Permet la migration automatique
                .build()
            val productRepo = ProductRepository(db.productDao(), ProductFirestore())
            val orderRepo = OrderRepository(OrderFirestore())

            val productViewModel: ProductViewModel = viewModel(
                factory = ProductViewModel.Factory(productRepo)
            )
            val orderViewModel: OrderViewModel = viewModel(
                factory = OrderViewModel.Factory(orderRepo)
            )

            MainScreen(
                productViewModel = productViewModel,
                orderViewModel = orderViewModel,
                onAddProduct = { navController.navigate("productForm") },
                onEditProduct = { product -> navController.navigate("productForm/${product.id}") },
                onOrderProduct = { product -> navController.navigate("order/${product.id}") },
                onSignOut = {
                    authViewModel.signOut()
                    navController.navigate("signin") {
                        popUpTo("home") { inclusive = true }
                    }
                }
            )
        }

        // Product Form – Add
        composable("productForm") {
            val context = LocalContext.current
            val db = Room.databaseBuilder(context, AppDatabase::class.java, "db")
                .fallbackToDestructiveMigration() // Permet la migration automatique
                .build()
            val productRepo = ProductRepository(db.productDao(), ProductFirestore())

            val productViewModel: ProductViewModel = viewModel(
                factory = ProductViewModel.Factory(productRepo)
            )

            ProductFormScreen(
                viewModel = productViewModel,
                initial = null, // Add
                onDone = { navController.popBackStack() }
            )
        }

        // Product Form – Edit
        composable("productForm/{productId}") { backStackEntry ->
            val productId = backStackEntry.arguments?.getString("productId")

            val context = LocalContext.current
            val db = Room.databaseBuilder(context, AppDatabase::class.java, "db")
                .fallbackToDestructiveMigration() // Permet la migration automatique
                .build()
            val productRepo = ProductRepository(db.productDao(), ProductFirestore())

            val productViewModel: ProductViewModel = viewModel(
                factory = ProductViewModel.Factory(productRepo)
            )

            // Observe products and find the one to edit
            val products by productViewModel.products.collectAsState(initial = emptyList())
            val product = products.find { it.id == productId }

            ProductFormScreen(
                viewModel = productViewModel,
                initial = product,
                onDone = { navController.popBackStack() }
            )
        }

        // Order Screen
        composable("order/{productId}") { backStackEntry ->
            val productId = backStackEntry.arguments?.getString("productId")

            val context = LocalContext.current
            val db = Room.databaseBuilder(context, AppDatabase::class.java, "db")
                .fallbackToDestructiveMigration()
                .build()
            val productRepo = ProductRepository(db.productDao(), ProductFirestore())
            val orderRepo = OrderRepository(OrderFirestore())

            val productViewModel: ProductViewModel = viewModel(
                factory = ProductViewModel.Factory(productRepo)
            )
            val orderViewModel: OrderViewModel = viewModel(
                factory = OrderViewModel.Factory(orderRepo)
            )

            // Observe products and find the one to order
            val products by productViewModel.products.collectAsState(initial = emptyList())
            val product = products.find { it.id == productId }

            if (product != null) {
                OrderScreen(
                    product = product,
                    orderViewModel = orderViewModel,
                    onBack = { navController.popBackStack() }
                )
            }
        }

    }
}

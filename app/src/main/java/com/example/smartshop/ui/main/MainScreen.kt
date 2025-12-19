package com.example.smartshop.ui.main

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.smartshop.ui.order.OrderHistoryScreen
import com.example.smartshop.ui.product.ProductListScreen
import com.example.smartshop.ui.stats.UserStatsScreen
import com.example.smartshop.viewmodel.OrderViewModel
import com.example.smartshop.viewmodel.ProductViewModel
import com.google.firebase.auth.FirebaseAuth

enum class MainTab {
    PRODUCTS,
    ORDERS,
    HISTORY,
    STATS
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    productViewModel: ProductViewModel,
    orderViewModel: OrderViewModel,
    onAddProduct: () -> Unit,
    onEditProduct: (com.example.smartshop.model.Product) -> Unit,
    onOrderProduct: (com.example.smartshop.model.Product) -> Unit,
    onSignOut: () -> Unit
) {
    var selectedTab by remember { mutableStateOf(MainTab.PRODUCTS) }
    val user = FirebaseAuth.getInstance().currentUser
    val userEmail = user?.email ?: "Utilisateur"

    Column(modifier = Modifier
        .fillMaxSize()
        .background(Color(0xFFF5F5F5))) {
        // Top App Bar with Navigation - Beautiful Gradient
        TopAppBar(
            title = { 
                Text(
                    text = when (selectedTab) {
                        MainTab.PRODUCTS -> "Produits"
                        MainTab.ORDERS -> "Commandes"
                        MainTab.HISTORY -> "Historique"
                        MainTab.STATS -> "Statistiques"
                    },
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
            },
            navigationIcon = {
                IconButton(onClick = { /* Menu drawer if needed */ }) {
                    Icon(
                        imageVector = Icons.Default.Menu,
                        contentDescription = "Menu",
                        tint = Color.White
                    )
                }
            },
            actions = {
                // User email display with badge
                Card(
                    modifier = Modifier
                        .padding(end = 8.dp)
                        .clip(RoundedCornerShape(12.dp)),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White.copy(alpha = 0.2f)
                    )
                ) {
                    Text(
                        text = userEmail.split("@").firstOrNull() ?: userEmail,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        fontWeight = FontWeight.Medium
                    )
                }
                // Logout button
                IconButton(onClick = onSignOut) {
                    Icon(
                        imageVector = Icons.Default.ExitToApp,
                        contentDescription = "Déconnexion",
                        tint = Color.White
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color.Transparent
            ),
            modifier = Modifier.background(
                Color(0xFF2196F3)
            )
        )

        // Tab Row for Navigation
        TabRow(
            selectedTabIndex = selectedTab.ordinal,
            containerColor = Color.White,
            contentColor = Color(0xFF2196F3),
            indicator = { tabPositions ->
                TabRowDefaults.Indicator(
                    modifier = Modifier
                        .tabIndicatorOffset(tabPositions[selectedTab.ordinal])
                        .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp)),
                    color = Color(0xFF2196F3),
                    height = 4.dp
                )
            },
            divider = {
                Divider(color = Color.LightGray, thickness = 0.5.dp)
            }
        ) {
            Tab(
                selected = selectedTab == MainTab.PRODUCTS,
                onClick = { selectedTab = MainTab.PRODUCTS },
                text = { 
                    Text(
                        "Produits", 
                        color = if (selectedTab == MainTab.PRODUCTS) Color(0xFF2196F3) else Color.Gray,
                        fontWeight = if (selectedTab == MainTab.PRODUCTS) FontWeight.Bold else FontWeight.Normal,
                        fontSize = 12.sp
                    ) 
                },
                icon = {
                    Icon(
                        imageVector = Icons.Default.ShoppingCart,
                        contentDescription = "Produits",
                        tint = if (selectedTab == MainTab.PRODUCTS) Color(0xFF2196F3) else Color.Gray
                    )
                }
            )
            Tab(
                selected = selectedTab == MainTab.ORDERS,
                onClick = { selectedTab = MainTab.ORDERS },
                text = { 
                    Text(
                        "Commandes",
                        color = if (selectedTab == MainTab.ORDERS) Color(0xFF2196F3) else Color.Gray,
                        fontWeight = if (selectedTab == MainTab.ORDERS) FontWeight.Bold else FontWeight.Normal,
                        fontSize = 12.sp
                    )
                },
                icon = {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Commandes",
                        tint = if (selectedTab == MainTab.ORDERS) Color(0xFF2196F3) else Color.Gray
                    )
                }
            )
            Tab(
                selected = selectedTab == MainTab.HISTORY,
                onClick = { selectedTab = MainTab.HISTORY },
                text = { 
                    Text(
                        "Historique",
                        color = if (selectedTab == MainTab.HISTORY) Color(0xFF2196F3) else Color.Gray,
                        fontWeight = if (selectedTab == MainTab.HISTORY) FontWeight.Bold else FontWeight.Normal,
                        fontSize = 12.sp
                    )
                },
                icon = {
                    Icon(
                        imageVector = Icons.Default.List,
                        contentDescription = "Historique",
                        tint = if (selectedTab == MainTab.HISTORY) Color(0xFF2196F3) else Color.Gray
                    )
                }
            )
            Tab(
                selected = selectedTab == MainTab.STATS,
                onClick = { selectedTab = MainTab.STATS },
                text = { 
                    Text(
                        "Stats",
                        color = if (selectedTab == MainTab.STATS) Color(0xFF2196F3) else Color.Gray,
                        fontWeight = if (selectedTab == MainTab.STATS) FontWeight.Bold else FontWeight.Normal,
                        fontSize = 12.sp
                    )
                },
                icon = {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Statistiques",
                        tint = if (selectedTab == MainTab.STATS) Color(0xFF2196F3) else Color.Gray
                    )
                }
            )
        }

        // Content based on selected tab
        Box(modifier = Modifier.fillMaxSize()) {
            when (selectedTab) {
                MainTab.PRODUCTS -> {
                    ProductListScreen(
                        viewModel = productViewModel,
                        onAdd = onAddProduct,
                        onEdit = onEditProduct,
                        onOrder = onOrderProduct
                    )
                }
                MainTab.ORDERS -> {
                    // Pour l'instant, on affiche l'historique aussi ici
                    // Vous pouvez créer un écran séparé pour les commandes en cours
                    OrderHistoryScreen(orderViewModel = orderViewModel)
                }
                MainTab.HISTORY -> {
                    OrderHistoryScreen(orderViewModel = orderViewModel)
                }
                MainTab.STATS -> {
                    UserStatsScreen(orderViewModel = orderViewModel)
                }
            }
        }
    }
}


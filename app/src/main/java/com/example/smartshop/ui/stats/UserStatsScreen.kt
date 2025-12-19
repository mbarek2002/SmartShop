package com.example.smartshop.ui.stats

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.smartshop.viewmodel.OrderViewModel

@Composable
fun UserStatsScreen(orderViewModel: OrderViewModel) {
    val orders by orderViewModel.orders.collectAsState(initial = emptyList())

    val totalOrders = orders.size
    val totalSpent = orders.sumOf { it.totalPrice }
    val averageOrder = if (orders.isNotEmpty()) totalSpent / orders.size else 0.0
    val pendingOrders = orders.count { it.status == "pending" }
    val confirmedOrders = orders.count { it.status == "confirmed" }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(4.dp, RoundedCornerShape(16.dp)),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF2196F3))
                    .padding(24.dp)
            ) {
                Column {
                    Text(
                        text = "Mes Statistiques",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Vue d'ensemble de vos commandes",
                        fontSize = 14.sp,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                }
            }
        }

        // Total des commandes
        StatCard(
            title = "Total des commandes",
            value = "$totalOrders",
            icon = Icons.Default.ShoppingCart,
            gradientColors = listOf(Color(0xFF2196F3), Color(0xFF03A9F4))
        )

        // Total dépensé
        StatCard(
            title = "Total dépensé",
            value = "${String.format("%.2f", totalSpent)} €",
            icon = Icons.Default.Star,
            gradientColors = listOf(Color(0xFF2196F3), Color(0xFF03A9F4))
        )

        // Panier moyen
        StatCard(
            title = "Panier moyen",
            value = "${String.format("%.2f", averageOrder)} €",
            icon = Icons.Default.Info,
            gradientColors = listOf(Color(0xFF2196F3), Color(0xFF03A9F4))
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Commandes en attente
            StatCard(
                title = "En attente",
                value = "$pendingOrders",
                icon = Icons.Default.Info,
                gradientColors = listOf(Color(0xFF03A9F4), Color(0xFF00BCD4)),
                modifier = Modifier.weight(1f)
            )

            // Commandes confirmées
            StatCard(
                title = "Confirmées",
                value = "$confirmedOrders",
                icon = Icons.Default.CheckCircle,
                gradientColors = listOf(Color(0xFF2196F3), Color(0xFF03A9F4)),
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun StatCard(
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    gradientColors: List<Color>,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.horizontalGradient(colors = gradientColors)
                )
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = title,
                        fontSize = 14.sp,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = value,
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Color.White.copy(alpha = 0.8f),
                    modifier = Modifier.size(48.dp)
                )
            }
        }
    }
}


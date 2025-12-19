package com.example.smartshop.model

/**
 * Modèle de données pour une commande.
 * Représente une commande passée par un utilisateur sur un produit.
 */
data class Order(
    val id: String = "",
    val productId: String = "", // ID du produit commandé
    val productName: String = "", // Nom du produit (pour éviter les jointures)
    val userId: String = "", // ID de l'utilisateur qui passe la commande
    val quantity: Int = 0, // Quantité commandée
    val unitPrice: Double = 0.0, // Prix unitaire au moment de la commande
    val totalPrice: Double = 0.0, // Prix total (quantity * unitPrice)
    val status: String = "pending", // pending, confirmed, cancelled
    val createdAt: Long = System.currentTimeMillis() // Timestamp de création
)


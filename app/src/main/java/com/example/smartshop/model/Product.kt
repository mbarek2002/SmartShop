package com.example.smartshop.model

/**
 * Modèle de données pour un produit.
 * Utilisé comme représentation universelle des produits dans l'application.
 *
 * Les champs par défaut sont nécessaires pour la désérialisation automatique depuis Firebase Firestore.
 */
data class Product(
    // Clé unique. Réutilisera l'ID généré par Firestore/Room.
    val id: String = "",
    val name: String = "",
    val quantity: Int = 0,
    val price: Double = 0.0,
    // Lien vers l'utilisateur propriétaire du produit pour la sécurité (filtrage Firestore).
    val userId: String = ""
)
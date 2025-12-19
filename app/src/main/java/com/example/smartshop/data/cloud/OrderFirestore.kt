package com.example.smartshop.data.cloud

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.tasks.await

data class OrderRemote(
    val id: String = "",
    val productId: String = "",
    val productName: String = "",
    val userId: String = "",
    val quantity: Int = 0,
    val unitPrice: Double = 0.0,
    val totalPrice: Double = 0.0,
    val status: String = "pending",
    val createdAt: Long = System.currentTimeMillis()
)

class OrderFirestore {
    private val db = FirebaseFirestore.getInstance()
    private val collection = db.collection("orders")
    private var listener: ListenerRegistration? = null
    
    private val _orders = MutableStateFlow<List<OrderRemote>>(emptyList())
    val orders: StateFlow<List<OrderRemote>> = _orders.asStateFlow()

    init {
        startListening()
    }

    private fun startListening() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId == null) {
            _orders.value = emptyList()
            return
        }

        // Écouter uniquement les commandes de l'utilisateur connecté
        // Note: Si vous obtenez une erreur d'index, créez un index composite dans Firebase Console
        // pour userId (Ascending) et createdAt (Descending)
        listener = collection
            .whereEqualTo("userId", userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    android.util.Log.e("OrderFirestore", "Erreur listener: ${error.message}")
                    _orders.value = emptyList()
                    return@addSnapshotListener
                }
                val ordersList = snapshot?.documents?.mapNotNull {
                    it.toObject(OrderRemote::class.java)
                } ?: emptyList()
                // Trier manuellement par createdAt décroissant (évite le besoin d'un index composite)
                _orders.value = ordersList.sortedByDescending { it.createdAt }
            }
    }

    fun observeAll(): Flow<List<OrderRemote>> = orders

    suspend fun create(order: OrderRemote): Result<String> {
        return try {
            val currentUser = FirebaseAuth.getInstance().currentUser
            if (currentUser == null) {
                return Result.failure(Exception("Utilisateur non connecté"))
            }
            
            if (order.userId != currentUser.uid) {
                return Result.failure(Exception("userId ne correspond pas"))
            }
            
            collection.document(order.id).set(order).await()
            Result.success("Commande créée avec succès")
        } catch (e: Exception) {
            android.util.Log.e("OrderFirestore", "Erreur création commande: ${e.message}", e)
            Result.failure(e)
        }
    }

    fun cleanup() {
        listener?.remove()
        listener = null
    }
}


package com.example.smartshop.data.cloud

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.tasks.await

data class ProductRemote(
    val id: String = "",
    val name: String = "",
    val quantity: Int = 0,
    val price: Double = 0.0,
    val userId: String = ""
)


class ProductFirestore {

    private val db = FirebaseFirestore.getInstance()
    private val collection = db.collection("products")
    private var listener: ListenerRegistration? = null
    
    private val _products = MutableStateFlow<List<ProductRemote>>(emptyList())
    val products: StateFlow<List<ProductRemote>> = _products.asStateFlow()

    init {
        startListening()
    }

    private fun startListening() {
        listener = collection
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    _products.value = emptyList()
                    return@addSnapshotListener
                }
                val productsList = snapshot?.documents?.mapNotNull {
                    it.toObject(ProductRemote::class.java)
                } ?: emptyList()
                _products.value = productsList
            }
    }

    fun observeAll(): Flow<List<ProductRemote>> = products

    suspend fun upload(product: ProductRemote): Result<String> {
        return try {
            // Vérifier que l'utilisateur est connecté
            val currentUser = FirebaseAuth.getInstance().currentUser
            if (currentUser == null) {
                return Result.failure(Exception("Utilisateur non connecté. Veuillez vous reconnecter."))
            }
            
            val currentUserId = currentUser.uid
            
            // Vérifier que le userId correspond
            if (product.userId.isEmpty()) {
                return Result.failure(Exception("userId est vide. L'utilisateur n'est peut-être pas correctement authentifié."))
            }
            
            if (product.userId != currentUserId) {
                return Result.failure(Exception("userId ne correspond pas. Attendu: $currentUserId, Reçu: ${product.userId}"))
            }
            
            // Log pour débogage
            android.util.Log.d("ProductFirestore", "Upload produit: id=${product.id}, userId=${product.userId}, currentUserId=$currentUserId")
            
            collection.document(product.id).set(product).await()
            Result.success("Produit sauvegardé avec succès")
        } catch (e: Exception) {
            android.util.Log.e("ProductFirestore", "Erreur upload: ${e.message}", e)
            Result.failure(e)
        }
    }

    suspend fun delete(id: String): Result<String> {
        return try {
            // Vérifier que l'utilisateur est connecté
            val currentUser = FirebaseAuth.getInstance().currentUser
            if (currentUser == null) {
                return Result.failure(Exception("Utilisateur non connecté"))
            }
            
            collection.document(id).delete().await()
            Result.success("Produit supprimé avec succès")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun cleanup() {
        listener?.remove()
        listener = null
    }
}

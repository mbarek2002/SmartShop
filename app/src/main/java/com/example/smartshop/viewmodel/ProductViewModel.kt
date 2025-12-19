package com.example.smartshop.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.smartshop.data.repository.ProductRepository
import com.example.smartshop.model.Product
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.util.*

class ProductViewModel(private val repository: ProductRepository) : ViewModel() {

    val products = repository.getAll()

    private val _stats = MutableStateFlow(ProductStats())
    val stats: StateFlow<ProductStats> = _stats

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    private val _operationSuccess = MutableStateFlow(false)
    val operationSuccess: StateFlow<Boolean> = _operationSuccess

    init {
        calculateStats()
    }

    fun add(name: String, quantity: Int, price: Double, imageUri: String) = viewModelScope.launch {
        _operationSuccess.value = false
        if(price <= 0 || quantity < 0) {
            _errorMessage.value = "Prix et quantité doivent être positifs"
            return@launch
        }

        try {
            val id = UUID.randomUUID().toString()
            val product = Product(id = id, name = name, quantity = quantity, price = price)
            val result = repository.add(product, imageUri)
            result.getOrElse { throwable ->
                _errorMessage.value = getErrorMessage(throwable)
                return@launch
            }
            _errorMessage.value = null
            _operationSuccess.value = true
            calculateStats()
        } catch (e: Exception) {
            _errorMessage.value = "Erreur lors de l'ajout: ${e.message}"
        }
    }

    fun update(product: Product, imageUri: String) = viewModelScope.launch {
        _operationSuccess.value = false
        try {
            val result = repository.update(product, imageUri)
            result.getOrElse { throwable ->
                _errorMessage.value = getErrorMessage(throwable)
                return@launch
            }
            _errorMessage.value = null
            _operationSuccess.value = true
            calculateStats()
        } catch (e: Exception) {
            _errorMessage.value = "Erreur lors de la mise à jour: ${e.message}"
        }
    }

    fun delete(product: Product) = viewModelScope.launch {
        _operationSuccess.value = false
        try {
            val result = repository.delete(product)
            result.getOrElse { throwable ->
                _errorMessage.value = getErrorMessage(throwable)
                return@launch
            }
            _errorMessage.value = null
            _operationSuccess.value = true
            calculateStats()
        } catch (e: Exception) {
            _errorMessage.value = "Erreur lors de la suppression: ${e.message}"
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }

    fun resetSuccess() {
        _operationSuccess.value = false
    }

    private fun getErrorMessage(throwable: Throwable): String {
        val currentUser = FirebaseAuth.getInstance().currentUser
        val userId = currentUser?.uid ?: "NON CONNECTÉ"
        
        return when {
            throwable.message?.contains("PERMISSION_DENIED") == true -> {
                "❌ Erreur de permissions Firestore\n\n" +
                "Vérifications:\n" +
                "1. Règles Firestore publiées dans Firebase Console\n" +
                "2. Vous êtes connecté (UID: $userId)\n" +
                "3. Collection 'products' existe\n\n" +
                "Solution rapide:\n" +
                "→ Utilisez firestore.rules.simple temporairement\n" +
                "→ Voir DEBUG_FIRESTORE.md\n\n" +
                "Erreur: ${throwable.message}"
            }
            throwable.message?.contains("non connecté") == true -> {
                "❌ Vous n'êtes pas connecté.\n\nVeuillez vous reconnecter."
            }
            throwable.message?.contains("userId est vide") == true -> {
                "❌ Problème d'authentification.\n\nLe userId n'est pas défini. Reconnectez-vous."
            }
            else -> {
                "❌ Erreur: ${throwable.message ?: throwable.toString()}\n\nVoir Logcat pour détails."
            }
        }
    }

    suspend fun getImageUri(productId: String): String? {
        return repository.getImageUri(productId)
    }

    private fun calculateStats() {
        products.onEach { list: List<Product> ->
            val totalStock = list.size
            val totalValue = list.sumOf { product: Product ->
                product.price * product.quantity
            }
            _stats.value = ProductStats(totalStock, totalValue)
        }.launchIn(viewModelScope)
    }

    class Factory(private val repo: ProductRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return ProductViewModel(repo) as T
        }
    }
}

data class ProductStats(val count: Int = 0, val totalValue: Double = 0.0)

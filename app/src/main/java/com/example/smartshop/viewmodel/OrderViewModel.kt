package com.example.smartshop.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.smartshop.data.repository.OrderRepository
import com.example.smartshop.model.Order
import com.example.smartshop.model.Product
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.*

class OrderViewModel(private val repository: OrderRepository) : ViewModel() {

    val orders = repository.getAll()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    private val _operationSuccess = MutableStateFlow(false)
    val operationSuccess: StateFlow<Boolean> = _operationSuccess

    fun createOrder(product: Product, quantity: Int) {
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser == null) {
            _errorMessage.value = "Vous devez être connecté pour passer une commande"
            return
        }

        if (quantity <= 0) {
            _errorMessage.value = "La quantité doit être supérieure à 0"
            return
        }

        if (quantity > product.quantity) {
            _errorMessage.value = "Quantité insuffisante. Disponible: ${product.quantity}"
            return
        }

        viewModelScope.launch {
            try {
                val orderId = UUID.randomUUID().toString()
                val totalPrice = product.price * quantity
                
                val order = Order(
                    id = orderId,
                    productId = product.id,
                    productName = product.name,
                    userId = currentUser.uid, // L'utilisateur qui passe la commande
                    quantity = quantity,
                    unitPrice = product.price,
                    totalPrice = totalPrice,
                    status = "pending"
                )

                val result = repository.create(order)
                result.getOrElse { throwable ->
                    _errorMessage.value = "Erreur lors de la création de la commande: ${throwable.message}"
                    return@launch
                }
                _errorMessage.value = null
                _operationSuccess.value = true
            } catch (e: Exception) {
                _errorMessage.value = "Erreur: ${e.message}"
            }
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }

    fun resetSuccess() {
        _operationSuccess.value = false
    }

    class Factory(private val repo: OrderRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return OrderViewModel(repo) as T
        }
    }
}


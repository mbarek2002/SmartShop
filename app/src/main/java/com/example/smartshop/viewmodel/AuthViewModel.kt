package com.example.smartshop.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartshop.data.AuthRepository
import com.example.smartshop.data.PreferencesManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    object Success : AuthState()
    data class Error(val message: String) : AuthState()
}

class AuthViewModel(
    private val repository: AuthRepository = AuthRepository(),
    private val preferencesManager: PreferencesManager? = null
) : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState

    init {
        // Check if user is already logged in
        checkAuthState()
    }

    private fun checkAuthState() {
        val currentUser = repository.getCurrentUser()
        if (currentUser != null) {
            _authState.value = AuthState.Success
        } else {
            _authState.value = AuthState.Idle
        }
    }

    fun signUp(email: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val result = repository.signUp(email, password)
            if (result) {
                // Save email after successful signup
                preferencesManager?.saveEmail(email)
                _authState.value = AuthState.Success
            } else {
                _authState.value = AuthState.Error("Inscription échouée")
            }
        }
    }

    fun signIn(email: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val result = repository.signIn(email, password)
            if (result) {
                // Save email after successful signin
                preferencesManager?.saveEmail(email)
                _authState.value = AuthState.Success
            } else {
                _authState.value = AuthState.Error("Connexion échouée")
            }
        }
    }

    fun signOut() {
        repository.signOut()
        _authState.value = AuthState.Idle
    }

    fun getSavedEmail(): String? {
        return preferencesManager?.getSavedEmail()
    }

    fun isUserLoggedIn(): Boolean {
        return repository.getCurrentUser() != null
    }
}

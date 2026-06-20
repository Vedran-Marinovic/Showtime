package com.example.myapplication.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.http_api.AuthRepository
import com.example.myapplication.token.TokenManager
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable

class AuthViewModel(
    private val repository: AuthRepository,
    private val tokenManager: TokenManager
) : ViewModel() {
    private val _state = MutableStateFlow(AuthUIState())
    val state = _state.asStateFlow()

    private val events = MutableSharedFlow<AuthUIEvent>()
    fun setEvent(event: AuthUIEvent) = viewModelScope.launch { events.emit(event) }

    init {
        observeEvents()
    }

    private fun observeEvents() {
        viewModelScope.launch {
            events.collect { event ->
                when (event) {
                    is AuthUIEvent.Login -> handleLogin(event.user, event.pass)
                    is AuthUIEvent.Signup -> handleSignup(event.fullName, event.user, event.pass)
                    AuthUIEvent.ToggleMode -> _state.update { it.copy(isLoginMode = !it.isLoginMode, error = null) }
                    AuthUIEvent.ClearError -> _state.update { it.copy(error = null) }
                }
            }
        }
    }

    private fun handleLogin(user: String, pass: String) {
        if (!validateBasic(user, pass)) return

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            try {
                val response = repository.login(user, pass)
                tokenManager.saveToken(response.access_token)
                // App.kt will see the new token and navigate away automatically
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, error = "Login failed: ${e.message}") }
            }
        }
    }

    private fun handleSignup(name: String, user: String, pass: String) {
        if (name.isBlank()) {
            _state.update { it.copy(error = "Full name is required") }
            return
        }
        if (!validateBasic(user, pass)) return

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            try {
                val response = repository.signup(name, user, pass)
                tokenManager.saveToken(response.access_token)
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, error = "Signup failed: ${e.message}") }
            }
        }
    }

    private fun validateBasic(user: String, pass: String): Boolean {
        val usernameRegex = "^[a-zA-Z0-9_]*$".toRegex()

        return when {
            user.length < 3 -> {
                _state.update { it.copy(error = "Username must be at least 3 characters") }
                false
            }
            !user.matches(usernameRegex) -> {
                _state.update { it.copy(error = "Username only allows letters, numbers and _") }
                false
            }
            pass.length < 8 -> {
                _state.update { it.copy(error = "Password must be at least 8 characters") }
                false
            }
            else -> true
        }
    }
}

data class AuthUIState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val isLoginMode: Boolean = true // Toggle between Login and Signup
)

sealed class AuthUIEvent {
    data class Login(val user: String, val pass: String) : AuthUIEvent()
    data class Signup(val fullName: String, val user: String, val pass: String) : AuthUIEvent()
    object ToggleMode : AuthUIEvent()
    object ClearError : AuthUIEvent()

}
@Serializable
data class User(
    val id: Int,
    val username: String,
    val full_name: String
)

@Serializable
data class AuthResponse(
    val access_token: String,
    val expires_in: Long,
    val user: User
)

@Serializable
data class LoginRequest(val username: String, val password: String)

@Serializable
data class SignupRequest(val full_name: String, val username: String, val password: String)
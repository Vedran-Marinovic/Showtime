package com.example.myapplication.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.http_api.AuthRepository
import com.example.myapplication.token.TokenManager
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val tokenManager: TokenManager,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _userState = MutableStateFlow<User?>(null)
    val userState = _userState.asStateFlow()
    sealed class UIEvent {
        object Logout : UIEvent()
    }

    private val events = MutableSharedFlow<UIEvent>()
    fun setEvent(event: UIEvent) = viewModelScope.launch { events.emit(event) }

    init {
        viewModelScope.launch {
            events.collect { event ->
                when (event) {
                    is UIEvent.Logout -> {
                        tokenManager.clearToken()
                    }
                }
            }
        }
        loadProfile()
    }

    private fun loadProfile() {
        viewModelScope.launch {
            try {
                val user = authRepository.getMe(token = tokenManager.token.first() ?: throw Exception("Not logged in"))
                _userState.value = user
            } catch (e: Exception) {
                if (e.message?.contains("401") == true) {
                    setEvent(ProfileViewModel.UIEvent.Logout)
                }
            }
        }
    }
}


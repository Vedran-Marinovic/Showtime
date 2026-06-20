package com.example.myapplication.http_api

import com.example.myapplication.token.TokenManager
import com.example.myapplication.viewmodels.AuthResponse
import com.example.myapplication.viewmodels.LoginRequest
import com.example.myapplication.viewmodels.SignupRequest
import com.example.myapplication.viewmodels.User
import kotlinx.coroutines.flow.first

class AuthRepository(private val AuthApiService: AuthApiService) {


    suspend fun signup(fullName: String, user: String, pass: String): AuthResponse {
        val response = AuthApiService.signup(SignupRequest(fullName, user, pass))
        return response
    }

    suspend fun login(username: String, pass: String): AuthResponse {
        val response = AuthApiService.login(LoginRequest(username, pass))
        return response
    }

    suspend fun getMe(token: String): User {
        return AuthApiService.getMe("Bearer $token")
    }
}
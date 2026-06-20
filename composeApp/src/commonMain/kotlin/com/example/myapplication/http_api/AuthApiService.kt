package com.example.myapplication.http_api

import com.example.myapplication.viewmodels.AuthResponse
import com.example.myapplication.viewmodels.LoginRequest
import com.example.myapplication.viewmodels.SignupRequest
import com.example.myapplication.viewmodels.User
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Header
import de.jensklingenberg.ktorfit.http.Headers
import de.jensklingenberg.ktorfit.http.POST

interface AuthApiService {
    @Headers("Content-Type: application/json")
    @POST("auth/signup")
    suspend fun signup(@Body request: SignupRequest): AuthResponse
    @Headers("Content-Type: application/json")
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): AuthResponse

    @GET("me")
    suspend fun getMe(@Header("Authorization") token: String): User

}
package com.example.myapplication.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.viewmodels.AuthUIEvent
import com.example.myapplication.viewmodels.AuthViewModel
import org.koin.compose.viewmodel.koinViewModel


@Composable
fun AuthLandingScreen(viewModel: AuthViewModel = koinViewModel(),) {
    val state by viewModel.state.collectAsState()

    var fullName by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp)
                .safeContentPadding(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = "🎬",
                fontSize = 80.sp,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Text(
                text = if (state.isLoginMode) "Welcome Back" else "Create Account",
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.primary
            )

            if (!state.isLoginMode) {
                TextField(
                    value = fullName,
                    onValueChange = { fullName = it },
                    label = { Text("Full Name") })
            }

            TextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Username") })
            TextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") })

            if (state.error != null) {
                Text(state.error!!, color = Color.Red)
            }

            Button(onClick = {
                if (state.isLoginMode) {
                    viewModel.setEvent(AuthUIEvent.Login(username, password))
                } else {
                    viewModel.setEvent(AuthUIEvent.Signup(fullName, username, password))
                }
            }) {
                if (state.isLoading) CircularProgressIndicator() else Text(if (state.isLoginMode) "Login" else "Sign Up")
            }

            TextButton(onClick = { viewModel.setEvent(AuthUIEvent.ToggleMode) }) {
                Text(if (state.isLoginMode) "Need an account? Sign Up" else "Have an account? Login")
            }
        }
    }
}
package com.example.myapplication.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.*
import com.example.myapplication.viewmodels.MovieDetailViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun DetailScreen (viewModel: MovieDetailViewModel = koinViewModel()){
    val uiState by viewModel.state.collectAsState()
    MaterialTheme (
        colorScheme = darkColorScheme()
    ) {
        Column {  }
    }
}
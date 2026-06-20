package com.example.myapplication.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import com.example.myapplication.viewmodels.MovieDetailViewModel
import org.koin.compose.viewmodel.koinViewModel
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import coil3.compose.LocalPlatformContext
import coil3.request.ImageRequest
import com.example.myapplication.viewmodels.Movie
import com.example.myapplication.viewmodels.MovieUIState
import com.example.myapplication.viewmodels.MovieViewModel

@Composable
@ExperimentalMaterial3Api
fun DetailScreen (viewModel: MovieDetailViewModel = koinViewModel(), onBack: () -> Unit){
    val uiState by viewModel.state.collectAsState()
    MaterialTheme (
        colorScheme = darkColorScheme()
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Details") },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Text(
                                text = "←",
                                fontSize = 28.sp,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(bottom = 4.dp)
                            )
                        }
                    }
                )
            }
        ) { padding ->
            if (uiState.isLoading) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
            } else uiState.movie?.let { movie ->
                LazyColumn(Modifier.padding(padding).padding(16.dp)) {

                    item {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            AsyncImage(
                                model = "https://image.tmdb.org/t/p/w200${movie.posterPath}",
                                contentDescription = null,
                                modifier = Modifier.size(60.dp).clip(RoundedCornerShape(8.dp))
                            )
                            Spacer(Modifier.width(12.dp))
                            Text(movie.title, style = MaterialTheme.typography.headlineMedium)
                        }
                    }


                    item {
                        Text("Overview", style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(top = 16.dp))
                        Text(movie.overview ?: "No overview available.", style = MaterialTheme.typography.bodyMedium)
                    }


                    item {
                        Text("Info", style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(top = 16.dp))
                        FlowRow(
                            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                            maxItemsInEachRow = 2,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            InfoBox("Budget", "$${(movie.budget ?: 0) / 1_000_000}M", Modifier.weight(1f))
                            InfoBox("Revenue", "$${(movie.revenue ?: 0) / 1_000_000}M", Modifier.weight(1f))
                            InfoBox("Language", movie.languageCode?.uppercase() ?: "N/A", Modifier.weight(1f))
                            InfoBox("Popularity", movie.popularity?.toString() ?: "N/A", Modifier.weight(1f))
                        }
                    }


                    item {
                        Text("Images", style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(top = 16.dp))
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(top = 8.dp)) {
                            items(uiState.images) { img ->
                                AsyncImage(
                                    model = "https://image.tmdb.org/t/p/w500${img.filePath}",
                                    contentDescription = null,
                                    modifier = Modifier.height(150.dp).clip(RoundedCornerShape(8.dp)),
                                    contentScale = ContentScale.FillHeight
                                )
                            }
                        }
                    }

                    item { Text("Actors", style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(top = 16.dp)) }
                    items(uiState.cast.take(10)) { person ->
                        Row(Modifier.padding(vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                            AsyncImage(
                                model = "https://image.tmdb.org/t/p/w200${person.profilePath}",
                                contentDescription = null,
                                modifier = Modifier.size(40.dp).clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )
                            Spacer(Modifier.width(12.dp))
                            Column {
                                Text(person.name, style = MaterialTheme.typography.bodyLarge)
                                //Text(person.professions ?: "", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun InfoBox(label: String, value: String, modifier: Modifier) {
    Card(modifier = modifier, colors = CardDefaults.cardColors(containerColor = Color.DarkGray)) {
        Column(Modifier.padding(12.dp)) {
            Text(label, style = MaterialTheme.typography.labelSmall, color = Color.LightGray)
            Text(value, style = MaterialTheme.typography.titleMedium, color = Color.White)
        }
    }
}
@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.myapplication.screens
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
import androidx.compose.foundation.lazy.items
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
import coil3.compose.AsyncImage
import coil3.compose.LocalPlatformContext
import coil3.request.ImageRequest
import com.example.myapplication.viewmodels.Movie
import com.example.myapplication.viewmodels.MovieUIState
import com.example.myapplication.viewmodels.MovieViewModel

@Composable
@Preview
fun App(viewModel: MovieViewModel = koinViewModel() ) {
    val uiState by viewModel.state.collectAsState()
    MaterialTheme (
        colorScheme = darkColorScheme()
    ) {
        var isInFilterMode by remember { mutableStateOf(false)}
        Scaffold(
            topBar = {
                TopMoviesAppBar(
                    isInFilterMode = isInFilterMode,
                    onButtonClick = {
                        isInFilterMode = !isInFilterMode
                    }
                )
            },
            content = { paddingValues ->
                if (isInFilterMode) {
                    FilterOverlay(
                        onButtonClick = {
                            isInFilterMode = !isInFilterMode
                        },
                        paddingValues = paddingValues,
                        viewModel = viewModel
                    )
                } else {
                    MovieResults(uiState, paddingValues,viewModel)
                }
            }
        )
    }
}

@Composable
fun MovieResults(
    uiState: MovieUIState,
    paddingValues: PaddingValues,
    viewModel: MovieViewModel
) {
    Column(modifier = Modifier
        .fillMaxSize()
        .padding(paddingValues)) {
        ResultsHeader(
            uiState.totalCount,
            viewModel.filter.sortBy,
            onSortSelected = { newSort ->
                viewModel.updateSort(newSort)
            }
        )

        if (uiState.loading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.3f)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (uiState.error != null) {
            Text(
                text = "Error: ${uiState.error?.toString()}",
                color = MaterialTheme.colorScheme.error,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(16.dp)
            )
        } else if (uiState.result.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.3f)),
                contentAlignment = Alignment.Center
            ) {
                Text("No movies found. Try changing filters.")
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(uiState.result) { movie ->
                    MovieItem(movie)
                }
            }
        }
    }
}

@Composable
fun MovieItem(movie: Movie) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val imageUrl = "https://image.tmdb.org/t/p/w500${movie.posterPath}"
            AsyncImage(
                model = ImageRequest.Builder(LocalPlatformContext.current)
                    .data(imageUrl)
                    .build(),
                contentDescription = "Poster for ${movie.title}",
                modifier = Modifier
                    .size(width = 80.dp, height = 120.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.DarkGray),
                contentScale = ContentScale.Crop
            )

            Spacer(Modifier.width(16.dp))

            Column {
                Text(
                    text = movie.title,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = "${movie.year ?: "N/A"}  •  ⭐ ${movie.imdbRating ?: "N/A"}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(Modifier.height(8.dp))

                Text(
                    text = movie.genres.joinToString { it.name },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
fun ResultsHeader(
    totalCount: Int,
    currentSortLabel: String,
    onSortSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val options = listOf("Rating", "Year", "Title", "Popularity")

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box {
            TextButton(onClick = { expanded = true }) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Sort by: $currentSortLabel",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(" ▼", style = MaterialTheme.typography.labelLarge)
                }
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                options.forEach { selection ->
                    DropdownMenuItem(
                        text = { Text(selection) },
                        onClick = {
                            onSortSelected(selection)
                            expanded = false
                        }
                    )
                }
            }
        }

        // RIGHT: Count Text
        Text(
            text = "$totalCount movies fit criteria",
            style = MaterialTheme.typography.bodySmall,
            color = Color.Gray
        )
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun FilterOverlay(
    onButtonClick: () -> Unit,
    paddingValues: PaddingValues,
    viewModel: MovieViewModel
) {
    var editingFilters by remember(viewModel.filter) {
        mutableStateOf(viewModel.filter)
    }
    Column(
        modifier = Modifier
            .padding(paddingValues)
            .safeContentPadding()
    ) {
        Text("Search")

        Spacer(Modifier.height(8.dp))

        TextField(
            value = editingFilters.query,
            onValueChange = {editingFilters = editingFilters.copy(query = it)},
            placeholder = { Text("Search by title...")},
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            keyboardOptions = KeyboardOptions(
                autoCorrectEnabled = false,
                keyboardType = KeyboardType.Text
            )
        )

        Spacer(Modifier.height(16.dp))

        Text("Genre")
        val genres = viewModel.genres

        FlowRow (
            horizontalArrangement = Arrangement.spacedBy(3.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            genres.forEach { genre ->
                FilterChip(
                    selected = genre == editingFilters.selectedgenre,
                    onClick = {
                        editingFilters = editingFilters.copy(selectedgenre = genre)
                    },
                    label = { Text(genre)},
                    modifier = Modifier
                        .height(20.dp)
                        .padding(horizontal = 4.dp, vertical = 0.dp)
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        Text("Year Range")
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextField(
                value = editingFilters.yearFrom?.toString() ?: "",
                onValueChange = {
                    editingFilters = editingFilters.copy(
                        yearFrom = it.toIntOrNull()
                    )
                },
                modifier = Modifier.weight(1f),
                singleLine = true,
                placeholder = { Text("From") }
            )

            Text(
                text = "—",
                modifier = Modifier.padding(horizontal = 8.dp),
                color = Color.Gray
            )

            TextField(
                value = editingFilters.yearTo?.toString() ?: "",
                onValueChange = {
                    editingFilters = editingFilters.copy(
                        yearTo = it.toIntOrNull()
                    )
                },
                modifier = Modifier.weight(1f),
                singleLine = true,
                placeholder = { Text("To") }
            )
        }

        Spacer(Modifier.height(16.dp))

        Text("Minimum Rating")
        Slider(
            value = editingFilters.minRating,
            onValueChange = {
                val rounded = (it * 2).toInt() / 2f
                editingFilters = editingFilters.copy(minRating = rounded)
            },
            valueRange = 0f..10f,
            steps = 19
        )

        Text("⭐ ${editingFilters.minRating}")

        Spacer(Modifier.height(16.dp))

        Button(
            onClick = {
                onButtonClick()
                viewModel.applyFilters(editingFilters)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Apply Filters")
        }

        Button(
            onClick = {
                editingFilters = editingFilters.copy(
                    query = "",
                    selectedgenre = "",
                    yearFrom = null,
                    yearTo = null,
                    minRating = 0f,
                    sortBy = "Rating"
                )
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Clear All")
        }
    }
}



@ExperimentalMaterial3Api
@Composable
fun TopMoviesAppBar(
    isInFilterMode: Boolean,
    onButtonClick: () -> Unit,
) {
    TopAppBar(
        title = {
            Text(
                text = "Movies",
            )
        },
        actions = {
            Button(
                onClick = onButtonClick,
                content = {
                    Text(text = if (!isInFilterMode) "Filter" else "Back")
                }
            )
        }
    )
}

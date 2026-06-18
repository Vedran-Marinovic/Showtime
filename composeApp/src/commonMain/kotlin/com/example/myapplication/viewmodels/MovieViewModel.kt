package com.example.myapplication.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.http_api.MovieRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlin.time.Duration.Companion.seconds

class MovieViewModel(private val repository: MovieRepository) : ViewModel() {

    private val _state = MutableStateFlow(value = MovieUIState())

    val state: StateFlow<MovieUIState> = _state.asStateFlow()

    var filter by mutableStateOf(FilterState())

    init {
        viewModelScope.launch {
            _state.update { it.copy(loading = true, error = null) }
            delay(1.seconds)
            applyFilters(FilterState("","",null,null,0f, "Rating"))
        }
    }
    fun applyFilters(newFilters: FilterState) {
        filter = newFilters
        loadMovies()
    }

    fun loadMovies() {
        viewModelScope.launch {
            _state.update { it.copy(loading = true, error = null) }
            try {
                val selectedGenreId = genreNameToId[filter.selectedgenre]

                val apiSortBy = when (filter.sortBy) {
                    "Rating" -> "imdb_rating"
                    "Year" -> "year"
                    "Title" -> "title"
                    "Popularity" -> "popularity"
                    else -> "imdb_rating"
                }

                val response = repository.getMovies(
                    query = filter.query.ifBlank { null },
                    genreId = selectedGenreId,
                    minYear = filter.yearFrom,
                    maxYear = filter.yearTo,
                    minRating = if (filter.minRating > 0) filter.minRating else null,
                    sortBy = apiSortBy
                )

                _state.update { it.copy(
                    loading = false,
                    result = response.items,
                    totalCount = response.totalItems
                )}
            } catch (e: Exception) {
                _state.update { it.copy(
                    loading = false,
                    error = e
                )}
            }
        }
    }
    val genres = listOf(
        "Action", "Adventure", "Animation", "Comedy", "Crime",
        "Drama", "Family", "Fantasy", "History", "Horror",
        "Music", "Mystery", "Romance", "Science Fiction",
        "Thriller", "War", "Western"
    )

    val genreNameToId = mapOf<String, Int>(
        "Action" to 28,
        "Adventure" to 12,
        "Animation" to 16,
        "Comedy" to 35,
        "Crime" to 80,
        "Drama" to 18,
        "Family" to 10751,
        "Fantasy" to 14,
        "History" to 36,
        "Horror" to 27,
        "Music" to 10402,
        "Mystery" to 9648,
        "Romance" to 10749,
        "Science Fiction" to 878,
        "Thriller" to 53,
        "War" to 10752,
        "Western" to 37
    )
    fun updateSort(newSort: String) {
        filter = filter.copy(sortBy = newSort)
        loadMovies()
    }
}


data class FilterState(
    var query: String = "",
    var selectedgenre: String = "",
    val yearFrom: Int? = 0,
    val yearTo: Int? = 2030,
    val minRating: Float = 0f,
    val sortBy: String = "Rating"
)

@Serializable
data class Movie(
    val imdbId: String,
    val title: String,
    val year: Int?,
    val imdbRating: Double?,
    val imdbVotes: Int?,
    val posterPath: String?,
    val genres: List<Genre>
)

data class MovieUIState(
    val loading: Boolean = false,
    val result: List<Movie> = emptyList(),
    val error: Throwable? = null,
    val totalCount: Int = 0
)

@Serializable
data class PaginatedResponse<T>(
    val items: List<T>,
    val page: Int,
    val pageSize: Int,
    val totalItems: Int,
    val totalPages: Int
)
@Serializable
data class Genre(val id: Int, val name: String)

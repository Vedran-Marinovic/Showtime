package com.example.myapplication.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.http_api.MovieRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable

class MovieDetailViewModel(
    private val repository: MovieRepository,
    private val movieId: String
) : ViewModel() {

    private val _state = MutableStateFlow(DetailUIState())
    val state = _state.asStateFlow()

    init {
        loadDetails()
    }

    private fun loadDetails() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
//            val detailsdeferred = async { repository.getMovieDetails(movieId)}
//            val imagesdeferred = async { repository.getMovieImages(movieId)}
//            val castdeferred = async { repository.getMovieCast(movieId)}
//
//            val details = detailsdeferred.await()
//            val images = imagesdeferred.await()
//            val cast = castdeferred.await()

            val details = repository.getMovieDetails(movieId)
            val images = repository.getMovieImages(movieId)
            val cast = repository.getMovieCast(movieId)

            _state.update {it.copy(
                isLoading = false,
                movie = details,
                cast = cast.items,
                images = images.backdrops.take(10),
                error = null,
            )}
        }
    }
}

data class DetailUIState(
    val isLoading: Boolean = false,
    val movie: MovieDetails? = null,
    val cast: List<PersonSummary> = emptyList(),
    val images: List<MovieImage> = emptyList(),
    val error: String? = null
)

@Serializable
data class MovieDetails(
    val imdbId: String,
    val title: String,
    val overview: String?,
    val budget: Long?,
    val revenue: Long?,
    val languageCode: String?,
    val popularity: Float?,
    val posterPath: String?,
    val backdropPath: String?,
    val year: Int?,
    val genres: List<Genre> = emptyList()
)
@Serializable
data class PersonSummary(
    val imdbId: String,
    val name: String,
    val professions: String? = null,
    val department: String? = null,
    val profilePath: String? = null
)

@Serializable
data class MovieActors(
    val actors: List<PersonSummary> = emptyList()
)

@Serializable
data class MovieImages(
    val backdrops: List<MovieImage> = emptyList(),
    val posters: List<MovieImage> = emptyList()
)

@Serializable
data class MovieImage(
    val filePath: String
)
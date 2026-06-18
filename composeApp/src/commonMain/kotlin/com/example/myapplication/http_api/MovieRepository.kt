package com.example.myapplication.http_api

import com.example.myapplication.viewmodels.Movie
import com.example.myapplication.viewmodels.MovieDetails
import com.example.myapplication.viewmodels.MovieImages
import com.example.myapplication.viewmodels.PaginatedResponse
import com.example.myapplication.viewmodels.PersonSummary

class MovieRepository(private val apiService: MovieApiService) {

    suspend fun getMovies(
        query: String? = null,
        genreId: Int? = null,
        minYear: Int? = null,
        maxYear: Int? = null,
        minRating: Float? = null,
        sortBy: String? = null
    ): PaginatedResponse<Movie> {
        return apiService.getMovies(
            query = query,
            genreId = genreId,
            minYear = minYear,
            maxYear = maxYear,
            minRating = minRating,
            sortBy = sortBy
        )
    }
    suspend fun getMovieDetails(
        id: String
    ): MovieDetails {
        return apiService.getMovieDetails(
            id = id
        )
    }
    suspend fun getMovieCast(
        id: String
    ): PaginatedResponse<PersonSummary> {
        return apiService.getMovieCast(
            id = id
        )
    }
    suspend fun getMovieImages(
        id: String
    ): MovieImages {
        return apiService.getMovieImages(
            id = id
        )
    }
}
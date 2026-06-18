package com.example.myapplication.http_api

import com.example.myapplication.viewmodels.Movie
import com.example.myapplication.viewmodels.MovieDetails
import com.example.myapplication.viewmodels.MovieImages
import com.example.myapplication.viewmodels.PaginatedResponse
import com.example.myapplication.viewmodels.PersonSummary
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Path
import de.jensklingenberg.ktorfit.http.Query

interface MovieApiService {
    @GET("movies")
    suspend fun getMovies(
        @Query("page") page: Int? = null,
        @Query("page_size") pageSize: Int? = null,
        @Query("query") query: String? = null,
        @Query("genre_id") genreId: Int? = null,
        @Query("min_year") minYear: Int? = null,
        @Query("max_year") maxYear: Int? = null,
        @Query("min_rating") minRating: Float? = null,
        @Query("sort_by") sortBy: String? = null,
        @Query("sort_order") sortOrder: String? = null
    ): PaginatedResponse<Movie>

    @GET("movies/{id}")
    suspend fun getMovieDetails(@Path("id") id: String): MovieDetails

    @GET("movies/{id}/cast?page_size=10")
    suspend fun getMovieCast(@Path("id") id: String): PaginatedResponse<PersonSummary>

    @GET("movies/{id}/images?type=backdrop")
    suspend fun getMovieImages(@Path("id") id: String): MovieImages

}
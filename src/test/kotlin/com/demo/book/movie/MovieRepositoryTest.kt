package com.demo.book.movie

import com.demo.book.BaseIntegrationSpec
import com.demo.book.movie.entity.Movie
import com.demo.book.movie.repository.MovieRepository
import com.demo.book.movie.request.MovieRequest
import io.kotest.matchers.shouldBe

class MovieRepositoryTest : BaseIntegrationSpec() {

    init {
        "Should add movie to a theatre" {
            // Given
            val theatreId = 1

            // When
            val result = addMovie("Sheershah", 100, theatreId)

            // Then
            result.id shouldBe 1
            result.title shouldBe "Sheershah"
        }

        "Should return list of movies in a theatre" {
            // Given
            val movieRepository = MovieRepository(dataSource)
            val theatreId = 1
            // When
            addMovie("Sheershah", 100, theatreId)
            addMovie("DDLJ", 200, theatreId)
            val result = movieRepository.getMoviesInATheatre(theatreId)

            // Then
            result shouldBe listOf(Movie(1, "Sheershah", 100, theatreId), Movie(2, "DDLJ", 200, theatreId))
        }
    }

    private fun addMovie(movieName: String, duration: Int, theatreId: Int): Movie {
        val movieRepository = MovieRepository(dataSource)
        return movieRepository.addMovieToATheatre(MovieRequest(movieName, duration), theatreId)
    }
}

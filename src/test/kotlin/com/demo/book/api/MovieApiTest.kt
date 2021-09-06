package com.demo.book.api

import com.demo.book.BaseIntegrationSpec
import com.demo.book.movie.entity.Movie
import com.demo.book.movie.request.MovieRequest
import com.demo.book.utils.get
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.micronaut.http.HttpStatus
import io.micronaut.http.client.exceptions.HttpClientResponseException

class MovieApiTest : BaseIntegrationSpec() {

    init {
        "Should add movie to a theatre" {
            // Given
            val theatreId = 1

            // When
            val response = addMovieToATheatre(MovieRequest("War", 120), theatreId)
            val movieAdded = response.body.get()

            // Then
            response.status shouldBe HttpStatus.OK
            jsonString(movieAdded) shouldBe jsonString(Movie(1, "War", 120, 1))
        }

        "Should get all added movies in a theatre" {
            // Given
            val theatreId = 1
            addMovieToATheatre(MovieRequest("War", 120), theatreId)

            // When
            val response = httpClient.get<List<Movie>>("/theatres/$theatreId/movies")
            val addedMovies = response.body.get()

            // Then
            response.status shouldBe HttpStatus.OK
            addedMovies.size shouldBe 1
            jsonString(addedMovies[0]) shouldBe jsonString(Movie(1, "War", 120, 1))
        }

        "Should throw error when we get all added movies in a theatre if theatreId is invalid" {
            // Given
            val theatreId = 10

            // When
            val exception =
                shouldThrow<HttpClientResponseException> { httpClient.get<List<Movie>>("/theatres/$theatreId/movies") }

            // Then
            exception.status shouldBe HttpStatus.BAD_REQUEST
            exception.message shouldBe "Theatre with this id does not exist"
        }

        "Should throw error if the duration of movie is less than 5 minutes when we add a movie to a theatre" {
            // Given
            val theatreId = 1
            val movieToBeAdded = MovieRequest("War", 3)

            // When
            val exception = shouldThrow<HttpClientResponseException> { addMovieToATheatre(movieToBeAdded, theatreId) }

            // Then
            exception.message shouldBe "Movie duration cannot be less than 5 minutes"
            exception.status shouldBe HttpStatus.BAD_REQUEST
        }

        "Should throw error if the duration of movie is more than 6 hours when we add a movie to a theatre" {
            // Given
            val theatreId = 1
            val movieToBeAdded = MovieRequest("War", 400)

            // When
            val exception = shouldThrow<HttpClientResponseException> { addMovieToATheatre(movieToBeAdded, theatreId) }

            // Then
            exception.message shouldBe "Movie duration cannot be more than 6 hours"
            exception.status shouldBe HttpStatus.BAD_REQUEST
        }

        "Should throw error when we add a movie and movie title is empty" {
            // Given
            val theatreId = 1
            val movieToBeAdded = MovieRequest("", 120)

            // When
            val exception = shouldThrow<HttpClientResponseException> { addMovieToATheatre(movieToBeAdded, theatreId) }

            // Then
            exception.status shouldBe HttpStatus.BAD_REQUEST
            exception.message shouldBe "Movie Name cannot be empty"
        }

        "Should throw error when we try to add the same movie again to a theatre" {
            // Given
            val theatreId = 1
            val movieToBeAdded = MovieRequest("War", 120)
            addMovieToATheatre(movieToBeAdded, theatreId)

            // When
            val exception = shouldThrow<HttpClientResponseException> { addMovieToATheatre(movieToBeAdded, theatreId) }

            // Then
            exception.status shouldBe HttpStatus.BAD_REQUEST
            exception.message shouldBe "Movie with this name already exists in this theatre"
        }

        "Should throw error when we try to add a movie to a theatre and the theatreId is invalid" {
            // Given
            val theatreId = 10
            val movieToBeAdded = MovieRequest("War", 120)

            // When
            val exception = shouldThrow<HttpClientResponseException> { addMovieToATheatre(movieToBeAdded, theatreId) }

            // Then
            exception.status shouldBe HttpStatus.BAD_REQUEST
            exception.message shouldBe "Theatre with this id does not exist"
        }
        "should throw exception when name in different case but is same " {
            val theatreId = 1
            val movieToBeAdded = MovieRequest("WAR", 120)
            addMovieToATheatre(movieToBeAdded, theatreId)

            // When
            val exception = shouldThrow<HttpClientResponseException> { addMovieToATheatre(movieToBeAdded, theatreId) }

            // Then
            exception.status shouldBe HttpStatus.BAD_REQUEST
            exception.message shouldBe "Movie with this name already exists in this theatre"
        }
    }
}

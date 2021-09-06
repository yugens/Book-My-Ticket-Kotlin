package com.demo.book.movie

import com.demo.book.movie.entity.Movie
import com.demo.book.theatre.entity.Theatre
import com.demo.book.movie.exception.InvalidMovieDataException
import com.demo.book.theatre.exception.InvalidTheatreDataException
import com.demo.book.movie.repository.MovieRepository
import com.demo.book.movie.request.MovieRequest
import com.demo.book.movie.service.MovieService
import com.demo.book.theatre.service.TheatreService
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.core.test.TestCase
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify

class MovieServiceTest : StringSpec() {

    private var mockMovieRepository = mockk<MovieRepository>(relaxed = true)
    private val mockTheatreService = mockk<TheatreService>(relaxed = true)
    private var movieService = MovieService(mockMovieRepository, mockTheatreService)

    override fun beforeEach(testCase: TestCase) {
        super.beforeEach(testCase)
        mockMovieRepository = mockk(relaxed = true)
        movieService = MovieService(mockMovieRepository, mockTheatreService)
    }

    init {
        "Should ensure we can save movie in a theatre if data is valid" {
            val theatreId = 1
            every {
                mockMovieRepository.addMovieToATheatre(MovieRequest("Movie1", 20), theatreId)
            } returns Movie(1, "Movie1", 20, theatreId)
            every {
                mockTheatreService.getAllTheatres()
            } returns listOf(Theatre(1, "Medly"))

            val result = movieService.addMovieToATheatre(MovieRequest("Movie1", 20), theatreId)
            result shouldBe Movie(1, "Movie1", 20, theatreId)
            verify(exactly = 1) { mockMovieRepository.addMovieToATheatre(any(), theatreId) }
        }

        "Should throw exception if duration is less that 5 min when we add a movie to a theatre" {
            val theatreId = 1
            val exception =
                shouldThrow<InvalidMovieDataException> {
                    movieService.addMovieToATheatre(
                        MovieRequest("Movie1", 2),
                        theatreId
                    )
                }

            exception.message shouldBe "Movie duration cannot be less than 5 minutes"
        }

        "Should throw exception if duration is more than 6 hours when we add a movie to a theatre" {
            val theatreId = 1
            val exception =
                shouldThrow<InvalidMovieDataException> {
                    movieService.addMovieToATheatre(
                        MovieRequest("Movie1", 400),
                        theatreId
                    )
                }

            exception.message shouldBe "Movie duration cannot be more than 6 hours"
        }

        "Should throw error when we add a movie and movie title is empty" {
            val theatreId = 1

            val exception =
                shouldThrow<InvalidMovieDataException> {
                    movieService.addMovieToATheatre(MovieRequest("", 100), theatreId)
                }

            exception.message shouldBe "Movie Name cannot be empty"
        }

        "Should throw exception if theatreId is invalid when we add a movie to a theatre" {

            val theatreId = 1
            every { movieService.validateTheatreId(6) } throws
                    InvalidTheatreDataException("This theatre does not exist")
            val exception =
                shouldThrow<InvalidTheatreDataException> {
                    movieService.addMovieToATheatre(
                        MovieRequest("Movie1", 200),
                        theatreId + 5
                    )
                }
            exception.message shouldBe "This theatre does not exist"
        }

        "Should throw exception when we add the same movie again to a theatre" {
            val theatreId = 1
            every {
                mockMovieRepository.getMoviesInATheatre(theatreId)
            } returns listOf(Movie(1, "Movie1", 200, theatreId))
            every {
                mockTheatreService.getAllTheatres()
            } returns listOf(Theatre(1, "Medly"))

            val exception =
                shouldThrow<InvalidMovieDataException> {
                    movieService.addMovieToATheatre(
                        MovieRequest("Movie1", 200),
                        theatreId
                    )
                }
            exception.message shouldBe "Movie with this name already exists in this theatre"
        }

        "Should return all movies in a theatre" {
            val theatreId = 1
            every {
                mockMovieRepository.getMoviesInATheatre(theatreId)
            } returns listOf(
                Movie(
                    1,
                    "Movie1",
                    7,
                    theatreId
                ),
                Movie(
                    2,
                    "Movie2",
                    100,
                    theatreId
                )
            )
            every {
                mockTheatreService.getAllTheatres()
            } returns listOf(Theatre(1, "Medly"))

            val result = movieService.getMoviesInATheatre(theatreId)
            result shouldBe listOf(
                Movie(1, "Movie1", 7, theatreId),
                Movie(2, "Movie2", 100, theatreId)
            )
            verify(exactly = 1) { mockMovieRepository.getMoviesInATheatre(theatreId) }
        }

        "Should throw exception when we get movies in a theatre if theatreId is invalid" {
            val theatreId = 6
            every { movieService.validateTheatreId(6) } throws
                    InvalidTheatreDataException("This theatre does not exist")
            val exception =
                shouldThrow<InvalidTheatreDataException> {
                    movieService.getMoviesInATheatre(theatreId)
                }
            exception.message shouldBe "This theatre does not exist"
        }
        "should throw exception if name is in different case but already present" {
            val theatreId = 1
            every {
                mockMovieRepository.getMoviesInATheatre(theatreId)
            } returns listOf(Movie(1, "Movie1", 200, theatreId))
            every {
                mockTheatreService.getAllTheatres()
            } returns listOf(Theatre(1, "Medly"))

            val exception =
                shouldThrow<InvalidMovieDataException> {
                    movieService.addMovieToATheatre(
                        MovieRequest("MOVIE1", 200),
                        theatreId
                    )
                }
            exception.message shouldBe "Movie with this name already exists in this theatre"
        }
    }
}

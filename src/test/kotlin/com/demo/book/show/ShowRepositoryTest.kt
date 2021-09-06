package com.demo.book.show

import com.demo.book.BaseIntegrationSpec
import com.demo.book.movie.entity.Movie
import com.demo.book.user.entity.User
import com.demo.book.movie.repository.MovieRepository
import com.demo.book.ticket.repository.TicketRepository
import com.demo.book.user.repository.UserRepository
import com.demo.book.movie.request.MovieRequest
import com.demo.book.show.request.ShowRequest
import com.demo.book.ticket.request.TicketRequest
import com.demo.book.user.request.UserRequest
import com.demo.book.show.repository.ShowRepository
import com.demo.book.show.entity.Show
import io.kotest.matchers.shouldBe
import java.sql.Timestamp

class ShowRepositoryTest() : BaseIntegrationSpec() {
    init {
        "Should save show to a theatre if show doesn't overlap" {
            val movieRepository = MovieRepository(dataSource)
            val theatreId = 1
            val totalSeats = 100
            movieRepository.addMovieToATheatre(MovieRequest("DDLJ", 100), theatreId)
            val currentTime = System.currentTimeMillis() + 1 * 24 * 3600 * 1000
            val result = addShow(currentTime, 1, theatreId, totalSeats)
            result.id shouldBe 1
            result.startTime shouldBe Timestamp(currentTime)
        }

        "Should return list of shows in a theatre" {
            val movieRepository = MovieRepository(dataSource)
            val theatreId = 1
            val totalSeats = 100
            val movieId1 = movieRepository.addMovieToATheatre(MovieRequest("DDLJ", 100), theatreId).id
            val movieId2 = movieRepository.addMovieToATheatre(MovieRequest("ABC", 120), theatreId).id
            val currentTime = System.currentTimeMillis() + 1 * 24 * 3600 * 1000
            val currentTimeNew = System.currentTimeMillis() + 2 * 24 * 3600 * 1000
            val showRepository = ShowRepository(dataSource)
            addShow(currentTime, movieId1, theatreId, totalSeats)
            addShow(currentTimeNew, movieId2, theatreId, totalSeats)
            val result = showRepository.getAllShowsInATheatre(theatreId)
            result shouldBe listOf(
                Show(1, Timestamp(currentTime), 1, theatreId, 100, 100),
                Show(2, Timestamp(currentTimeNew), 2, theatreId, 100, 100)
            )
        }

        "Should get the count of tickets of a show in a theatre" {
            // Given
            val movieRepository = MovieRepository(dataSource)
            val showRepository = ShowRepository(dataSource)
            val theatreId = 1
            val totalSeats = 100
            movieRepository.addMovieToATheatre(MovieRequest("DDLJ", 100), theatreId)
            val currentTime = System.currentTimeMillis() + 1 * 24 * 3600 * 1000
            addShow(currentTime, 1, theatreId, totalSeats)

            // When
            val count = showRepository.getCountOfTicketsOfAShow(1)

            // Then
            count shouldBe 0
        }

        "Should get tickets for a show of a theatre" {
            // Given
            val currentTime = System.currentTimeMillis() + 1 * 24 * 3600 * 1000
            val totalSeats = 100
            val movieId = addMovie(MovieRequest("DDLJ", 100)).id
            val showId = addShow(currentTime, movieId, 1, totalSeats).id
            val userId = addUser1(UserRequest("Shivam", "shivam@gmail.com")).id

            // When
            val ticketRepository = TicketRepository(dataSource)
            ticketRepository.book(TicketRequest(showId, userId, 1))
            val showRepository = ShowRepository(dataSource)
            val result = showRepository.getTicketsForAShow(showId, 1)

            // Then
            result.size shouldBe 1
            result[0] shouldBe 1
        }
    }

    private fun addShow(startTime: Long, movieId: Int, theatreId: Int, totalSeats: Int): Show {
        val showRepository = ShowRepository(dataSource)
        return showRepository.addShowToATheatre(ShowRequest(startTime, movieId, totalSeats), theatreId)
    }

    private fun addMovie(movieRequest: MovieRequest): Movie {
        val movieRepository = MovieRepository(dataSource)
        return movieRepository.addMovieToATheatre(movieRequest, 1)
    }

    private fun addUser1(userRequest: UserRequest): User {
        val userRepository = UserRepository(dataSource)
        return userRepository.add(userRequest)
    }
}

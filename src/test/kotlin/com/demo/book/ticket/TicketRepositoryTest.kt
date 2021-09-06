package com.demo.book.ticket

import com.demo.book.BaseIntegrationSpec
import com.demo.book.movie.entity.Movie
import com.demo.book.ticket.entity.Ticket
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

class TicketRepositoryTest : BaseIntegrationSpec() {

    init {
        "should book tickets if data is valid" {
            // Given
            val currentTime = System.currentTimeMillis() + 1 * 24 * 3600 * 1000
            val totalSeats = 100
            val movieId = addMovie(MovieRequest("DDLJ", 100)).id
            val showId = addShow(ShowRequest(currentTime, movieId, totalSeats), 1).id
            val userId = addUser(UserRequest("Shivam", "shivam@gmail.com")).id
            val ticketRepository = TicketRepository(dataSource)

            // When
            val result = ticketRepository.book(TicketRequest(showId, userId, 1))

            // Then
            result shouldBe 1
        }

        "should be able to get all booked tickets" {
            // Given
            val currentTime = System.currentTimeMillis() + 1 * 24 * 3600 * 1000
            val totalSeats = 100
            val movieId = addMovie(MovieRequest("DDLJ", 100)).id
            val showId = addShow(ShowRequest(currentTime, movieId, totalSeats), 1).id
            val userId = addUser(UserRequest("Shivam", "shivam@gmail.com")).id
            val ticketRepository = TicketRepository(dataSource)
            val ticketNumber = ticketRepository.book(TicketRequest(showId, userId, 1))

            // When
            val ticket = ticketRepository.getTicketDetailsFromTicketNumber(ticketNumber)

            // Then
            jsonString(ticket) shouldBe jsonString(
                Ticket(
                    ticketNumber,
                    "Shivam",
                    "Medly",
                    "DDLJ",
                    Timestamp(currentTime),
                    100
                )
            )
        }
    }

    private fun addMovie(movieRequest: MovieRequest): Movie {
        val movieRepository = MovieRepository(dataSource)
        return movieRepository.addMovieToATheatre(movieRequest, 1)
    }

    private fun addShow(showRequest: ShowRequest, theatreId: Int): Show {
        val showRepository = ShowRepository(dataSource)
        return showRepository.addShowToATheatre(showRequest, theatreId)
    }

    private fun addUser(userRequest: UserRequest): User {
        val userRepository = UserRepository(dataSource)
        return userRepository.add(userRequest)
    }
}

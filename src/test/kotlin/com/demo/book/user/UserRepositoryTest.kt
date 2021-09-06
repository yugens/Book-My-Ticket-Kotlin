package com.demo.book.user

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

class UserRepositoryTest : BaseIntegrationSpec() {

    init {
        "should add user if data is valid" {
            // When
            val result = addUser(UserRequest("Shivam", "shivam@gmail.com")).id

            // Then
            result shouldBe 1
        }

        "should return list of all users" {
            // Given
            val userRepository = UserRepository(dataSource)
            userRepository.add(UserRequest("Shivam", "shivam@gmail.com"))

            // When
            val usersList = userRepository.getAll()

            // Then
            usersList.size shouldBe 1
            usersList[0].id shouldBe 1
            usersList[0].name shouldBe "Shivam"
            usersList[0].email shouldBe "shivam@gmail.com"
        }

        "should get tickets for a user" {
            // Given
            val currentTime = System.currentTimeMillis() + 1 * 24 * 3600 * 1000
            val movieId = addMovie(MovieRequest("DDLJ", 100)).id
            val showId = addShow(ShowRequest(currentTime, movieId, 100), 1).id
            val userId = addUser(UserRequest("Shivam", "shivam@gmail.com")).id

            // When
            val ticketRepository = TicketRepository(dataSource)
            ticketRepository.book(TicketRequest(showId, userId, 1))
            val userRepository = UserRepository(dataSource)
            val result = userRepository.getTicketsForAUser(userId)

            // Then
            result.size shouldBe 1
            result[0] shouldBe 1
        }
    }

    private fun addUser(userRequest: UserRequest): User {
        val userRepository = UserRepository(dataSource)
        return userRepository.add(userRequest)
    }

    private fun addMovie(movieRequest: MovieRequest): Movie {
        val movieRepository = MovieRepository(dataSource)
        return movieRepository.addMovieToATheatre(movieRequest, 1)
    }

    private fun addShow(showRequest: ShowRequest, theatreId: Int): Show {
        val showRepository = ShowRepository(dataSource)
        return showRepository.addShowToATheatre(showRequest, theatreId)
    }
}

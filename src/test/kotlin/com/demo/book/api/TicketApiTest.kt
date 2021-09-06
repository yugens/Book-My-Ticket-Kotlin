package com.demo.book.api

import com.demo.book.BaseIntegrationSpec
import com.demo.book.movie.entity.Movie
import com.demo.book.ticket.entity.Ticket
import com.demo.book.movie.request.MovieRequest
import com.demo.book.show.request.ShowRequest
import com.demo.book.ticket.request.TicketRequest
import com.demo.book.user.request.UserRequest
import com.demo.book.show.entity.Show
import com.demo.book.user.entity.User
import com.demo.book.utils.get
import com.demo.book.utils.post
import com.fasterxml.jackson.module.kotlin.readValue
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus
import io.micronaut.http.client.exceptions.HttpClientResponseException
import java.sql.Timestamp

class TicketApiTest : BaseIntegrationSpec() {

    init {
        "should be able to book tickets if show is within seven days" {
            // Given
            val currentTime = System.currentTimeMillis() + 1 * 24 * 3600 * 1000
            val theatreId = 1
            val totalSeats = 100
            val responseMovie = addMovieToATheatre(MovieRequest("War", 120), theatreId).body.get()
            val movie = jsonMapper.readValue<Movie>(jsonString(responseMovie))
            val userResponse = addUserToSystem(UserRequest("Shivam", "shivam@gmail.com")).body.get()
            val user = jsonMapper.readValue<User>(jsonString(userResponse))
            val responseShow = addShowToATheatre(ShowRequest(currentTime, movie.id, totalSeats), theatreId).body.get()
            val show = jsonMapper.readValue<Show>(jsonString(responseShow))

            // When
            val ticketResponse = bookTicketForShow(TicketRequest(show.id, user.id, theatreId)).body.get()
            val ticket = jsonMapper.readValue<Ticket>(jsonString(ticketResponse))

            // Then
            ticket.ticketNumber shouldBe 1
        }

        "should be able to get all booked tickets" {
            // Given
            val theatreId = 1
            val totalSeats = 100
            val currentTime = System.currentTimeMillis() + 1 * 24 * 3600 * 1000
            val responseMovie = addMovieToATheatre(MovieRequest("War", 120), theatreId).body.get()
            val movie = jsonMapper.readValue<Movie>(jsonString(responseMovie))
            val userResponse = addUserToSystem(UserRequest("Shivam", "shivam@gmail.com")).body.get()
            val user = jsonMapper.readValue<User>(jsonString(userResponse))
            val responseShow = addShowToATheatre(ShowRequest(currentTime, movie.id, totalSeats), theatreId).body.get()
            val show = jsonMapper.readValue<Show>(jsonString(responseShow))
            val ticketResponse = bookTicketForShow(TicketRequest(show.id, user.id, theatreId)).body.get()
            val ticket = jsonMapper.readValue<Ticket>(jsonString(ticketResponse))

            // When
            val ticketList = httpClient.get<List<Ticket>>("/tickets/1").body.get()

            // Then
            ticketList.size shouldBe 1
            jsonString(ticketList[0]) shouldBe jsonString(
                Ticket(
                    ticket.ticketNumber,
                    "Shivam",
                    "Medly",
                    "War",
                    Timestamp(currentTime),
                    120
                )
            )
        }

        "should throw exception when user does not exist" {
            // Given
            val theatreId = 1
            val totalSeats = 100
            val currentTime = System.currentTimeMillis() + 1 * 24 * 3600 * 1000
            val responseMovie = addMovieToATheatre(MovieRequest("War", 120), theatreId).body.get()
            val movie = jsonMapper.readValue<Movie>(jsonString(responseMovie))
            val responseShow = addShowToATheatre(ShowRequest(currentTime, movie.id, totalSeats), theatreId).body.get()
            val show = jsonMapper.readValue<Show>(jsonString(responseShow))

            // When
            val response =
                shouldThrow<HttpClientResponseException> { bookTicketForShow(TicketRequest(show.id, 1, theatreId)) }

            // Then
            response.status shouldBe HttpStatus.BAD_REQUEST
            response.message shouldBe "This user does not exist"
        }

        "should throw exception when show Id is invalid" {
            // Given
            addMovie(MovieRequest("War", 100))
            val userResponse = addUserToSystem(UserRequest("Shivam", "shivam@gmail.com")).body.get()
            val user = jsonMapper.readValue<User>(jsonString(userResponse))

            // When
            val response = shouldThrow<HttpClientResponseException> { bookTicketForShow(TicketRequest(2, user.id, 1)) }

            // Then
            response.status shouldBe HttpStatus.BAD_REQUEST
            response.message shouldBe "This show does not exist"
        }

        "should throw an exception when the booked show is after 7 days" {
            // Given
            val theatreId = 1
            val totalSeats = 100
            val currentTime = System.currentTimeMillis() + 10 * 24 * 3600 * 1000
            val responseMovie = addMovieToATheatre(MovieRequest("War", 120), theatreId).body.get()
            val movie = jsonMapper.readValue<Movie>(jsonString(responseMovie))
            val userResponse = addUserToSystem(UserRequest("Shivam", "shivam@gmail.com")).body.get()
            val user = jsonMapper.readValue<User>(jsonString(userResponse))
            val responseShow = addShowToATheatre(ShowRequest(currentTime, movie.id, totalSeats), theatreId).body.get()
            val show = jsonMapper.readValue<Show>(jsonString(responseShow))

            // When
            val response = shouldThrow<HttpClientResponseException> {
                bookTicketForShow(TicketRequest(show.id, user.id, theatreId)).body.get()
                    .toString()
                    .toInt()
            }

            // Then
            response.status shouldBe HttpStatus.BAD_REQUEST
            response.message shouldBe "This show is after 7 days"
        }
    }

    private fun addMovie(movieRequest: MovieRequest): HttpResponse<Any> {
        return httpClient.post(
            url = "/theatres/1/movies",
            body = jsonMapper.writeValueAsString(movieRequest)
        )
    }
}

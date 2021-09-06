package com.demo.book.api

import com.demo.book.BaseIntegrationSpec
import com.demo.book.movie.entity.Movie
import com.demo.book.ticket.entity.Ticket
import com.demo.book.user.entity.User
import com.demo.book.movie.request.MovieRequest
import com.demo.book.show.request.ShowRequest
import com.demo.book.ticket.request.TicketRequest
import com.demo.book.user.request.UserRequest
import com.demo.book.show.entity.Show
import com.demo.book.utils.get
import com.fasterxml.jackson.module.kotlin.readValue
import io.kotest.assertions.throwables.shouldThrowExactly
import io.kotest.matchers.shouldBe
import io.micronaut.http.HttpStatus
import io.micronaut.http.client.exceptions.HttpClientResponseException
import java.sql.Timestamp

class UserApiTest : BaseIntegrationSpec() {

    init {
        "should ensure we can add a user if details are valid" {
            // Given
            val userRequest = UserRequest("Shivam", "shivam@gmail.com")

            // When
            val response = addUserToSystem(userRequest)
            val user = jsonMapper.readValue<User>(jsonString(response.body.get()))

            // Then
            response.status shouldBe HttpStatus.OK
            user.id shouldBe 1
            user.name shouldBe "Shivam"
            user.email shouldBe "shivam@gmail.com"
        }

        "should throw an exception if the same user is added twice" {
            // Given
            val userRequest = UserRequest("Shivam", "shivam@gmail.com")
            addUserToSystem(userRequest)

            // When
            val exception = shouldThrowExactly<HttpClientResponseException> { addUserToSystem(userRequest) }

            // Then
            exception.message shouldBe "User already exists"
            exception.status shouldBe HttpStatus.BAD_REQUEST
        }

        "should return all added users" {
            // Given
            val userRequest = UserRequest("Shivam", "shivam@gmail.com")
            addUserToSystem(userRequest)

            // When
            val response = httpClient.get<List<User>>("/users")
            val users = response.body.get()

            // Then
            response.status shouldBe HttpStatus.OK
            users.size shouldBe 1
            val expected = jsonString(User(1, "Shivam", "shivam@gmail.com"))
            jsonString(users[0]) shouldBe expected
        }

        "should be able to get all tickets for a user" {
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
            val ticketList = httpClient.get<List<Ticket>>("/users/${user.id}/tickets").body.get()

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
        "should throw exception when try to add user with empty detail" {
            // Given
            val userRequest = UserRequest("", "")

            // When
            val exception = shouldThrowExactly<HttpClientResponseException> { addUserToSystem(userRequest) }

            // Then
            exception.message shouldBe "User Details can't be empty"
            exception.status shouldBe HttpStatus.BAD_REQUEST
        }
        "should throw exception when try to add user with invalid email" {
            // Given
            val userRequest = UserRequest("Shivam", "shiv.com")

            // When
            val exception = shouldThrowExactly<HttpClientResponseException> { addUserToSystem(userRequest) }

            // Then
            exception.message shouldBe "Email not valid"
            exception.status shouldBe HttpStatus.BAD_REQUEST
        }
    }
}

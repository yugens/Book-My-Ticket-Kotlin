package com.demo.book.api

import com.demo.book.BaseIntegrationSpec
import com.demo.book.movie.entity.Movie
import com.demo.book.ticket.entity.Ticket
import com.demo.book.movie.request.MovieRequest
import com.demo.book.show.request.ShowRequest
import com.demo.book.ticket.request.TicketRequest
import com.demo.book.user.request.UserRequest
import com.demo.book.show.entity.Show
import com.demo.book.utils.get
import com.fasterxml.jackson.module.kotlin.readValue
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.micronaut.http.HttpStatus
import io.micronaut.http.client.exceptions.HttpClientResponseException
import java.sql.Timestamp
import com.demo.book.show.entity.AllShows
import com.demo.book.user.entity.User
import kotlinx.coroutines.delay

class ShowApiTest : BaseIntegrationSpec() {

    init {
        "Should get all added shows in a theatre in reverse chronological order with their remaining seats" {
            // Given
            val theatreId = 1
            val totalSeats = 100
            val currentTime = System.currentTimeMillis() + 1 * 24 * 3600 * 1000 // Current Time + 1 Day
            val currentTimeNew = System.currentTimeMillis() + 2 * 24 * 3600 * 1000 // Current Time + 2 Days
            val responseMovie = addMovieToATheatre(MovieRequest("War", 120), theatreId).body.get()
            val movie = jsonMapper.readValue<Movie>(jsonString(responseMovie))
            addShowToATheatre(ShowRequest(currentTime, movie.id, totalSeats), theatreId)
            addShowToATheatre(ShowRequest(currentTimeNew, movie.id, totalSeats), theatreId)

            // When
            val response = httpClient.get<List<Show>>("/theatres/$theatreId/shows")
            val addedShows = response.body.get()

            // Then
            response.status shouldBe HttpStatus.OK
            addedShows.size shouldBe 2
            jsonString(addedShows[1]) shouldBe jsonString(Show(1, Timestamp(currentTime), 1, 1, 100, 100))
            jsonString(addedShows[0]) shouldBe jsonString(Show(2, Timestamp(currentTimeNew), 1, 1, 100, 100))
        }

        "Should throw exception when we get all added shows in a theatre and theatreId is invalid" {
            // Given
            val theatreId = 10

            // When
            val response =
                shouldThrow<HttpClientResponseException> { httpClient.get<List<Show>>("/theatres/$theatreId/shows") }

            // Then
            response.status shouldBe HttpStatus.BAD_REQUEST
            response.message shouldBe "Theatre with this id does not exist"
        }

        "Should return empty list when a theatre has no shows added to it" {
            // When
            val theatreId = 1
            val response = httpClient.get<List<Show>>("/theatres/$theatreId/shows")
            val addedShows = response.body.get()

            // Then
            response.status shouldBe HttpStatus.OK
            addedShows.size shouldBe 0
        }

        "Should throw exception when we add a show that overlaps with another show in a theatre" {
            // Given
            val theatreId = 1
            val totalSeats = 100
            val currentTime = System.currentTimeMillis() + 1 * 24 * 3600 * 1000 // Current Time + 1 Day
            val responseMovie = addMovieToATheatre(MovieRequest("War", 100), theatreId).body.get()
            val movie = jsonMapper.readValue<Movie>(jsonString(responseMovie))
            addShowToATheatre(ShowRequest(currentTime, movie.id, totalSeats), theatreId)

            // When
            val response = shouldThrow<HttpClientResponseException> {
                addShowToATheatre(
                    ShowRequest(currentTime, movie.id, totalSeats),
                    theatreId
                )
            }

            // Then
            response.status shouldBe HttpStatus.BAD_REQUEST
            response.message shouldBe "Already have a show scheduled during that time"
        }

        "Should throw exception when we add a show which has the start time in the past in a theatre" {
            // Given
            val theatreId = 1
            val totalSeats = 100
            val currentTime = System.currentTimeMillis() - 1 * 24 * 3600 * 1000 // Current Time + 1 Day
            val responseMovie = addMovieToATheatre(MovieRequest("War", 100), theatreId).body.get()
            val movie = jsonMapper.readValue<Movie>(jsonString(responseMovie))

            // When
            val response = shouldThrow<HttpClientResponseException> {
                addShowToATheatre(
                    ShowRequest(currentTime, movie.id, totalSeats), theatreId
                )
            }

            // Then
            response.status shouldBe HttpStatus.BAD_REQUEST
            response.message shouldBe "Cannot create a show for past date"
        }

        "Should add shows to a theatre when it does not overlap with other shows" {
            // Given
            val theatreId = 1
            val totalSeats = 100
            val currentTime = System.currentTimeMillis() + 1 * 24 * 3600 * 1000 // Current Time + 1 Day
            val responseMovie = addMovieToATheatre(MovieRequest("War", 150), theatreId).body.get()
            val movie = jsonMapper.readValue<Movie>(jsonString(responseMovie))
            addShowToATheatre(ShowRequest(currentTime, movie.id, totalSeats), theatreId)
            val newCurrentTime = System.currentTimeMillis() + 2 * 24 * 3600 * 1000 // Current Time + 2 Days
            addShowToATheatre(ShowRequest(newCurrentTime, movie.id, totalSeats), theatreId)

            // When
            val response = httpClient.get<List<Show>>("/theatres/$theatreId/shows")
            val savedShows = response.body.get()

            // Then
            response.status shouldBe HttpStatus.OK
            savedShows.size shouldBe 2
            jsonString(savedShows[1]) shouldBe jsonString(Show(1, Timestamp(currentTime), 1, 1, 100, 100))
            jsonString(savedShows[0]) shouldBe jsonString(Show(2, Timestamp(newCurrentTime), 1, 1, 100, 100))
        }

        "Should throw exception when we add show in a theatre and theatreId is invalid" {
            // Given
            val theatreId = 1
            val totalSeats = 100
            val currentTime = System.currentTimeMillis() + 1 * 24 * 3600 * 1000 // Current Time + 1 Day
            val responseMovie = addMovieToATheatre(MovieRequest("War", 150), theatreId).body.get()
            val movie = jsonMapper.readValue<Movie>(jsonString(responseMovie))

            // When
            val response = shouldThrow<HttpClientResponseException> {
                addShowToATheatre(ShowRequest(currentTime, movie.id, totalSeats), theatreId + 10)
            }

            // Then
            response.status shouldBe HttpStatus.BAD_REQUEST
            response.message shouldBe "Theatre with this id does not exist"
        }

        "Should throw exception when we add show in a theatre and movieId is invalid" {
            // Given
            val theatreId = 1
            val totalSeats = 100
            val currentTime = System.currentTimeMillis() + 1 * 24 * 3600 * 1000 // Current Time + 1 Day
            val responseMovie = addMovieToATheatre(MovieRequest("War", 150), theatreId).body.get()
            val movie = jsonMapper.readValue<Movie>(jsonString(responseMovie))

            // When
            val response = shouldThrow<HttpClientResponseException> {
                addShowToATheatre(ShowRequest(currentTime, movie.id + 10, totalSeats), theatreId)
            }

            // Then
            response.status shouldBe HttpStatus.BAD_REQUEST
            response.message shouldBe "Movie with this id does not exist in this theatre"
        }

        "Should be able to get all tickets for a show in a theatre" {
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
            bookTicketForShow(TicketRequest(show.id, user.id, theatreId))

            // When
            val ticketList = httpClient.get<Ticket>("/theatres/$theatreId/shows/${show.id}/tickets").body.get()

            // Then
            jsonString(ticketList) shouldBe jsonString(Ticket(1, "Shivam", "Medly", "War", Timestamp(currentTime), 120))
        }

        "should return list of all shows categorised by past, ongoing and upcoming" {
            val theatreId = 1
            val currentTime = System.currentTimeMillis() + 1000
            val futureTime = System.currentTimeMillis() + 1 * 24 * 3600 * 1000
            val responseMovie = addMovieToATheatre(MovieRequest("War", 120), theatreId).body.get()
            val movie = jsonMapper.readValue<Movie>(jsonString(responseMovie))
            addShowToATheatre(ShowRequest(currentTime, movie.id, 100), theatreId)
            addShowToATheatre(ShowRequest(futureTime, movie.id, 100), theatreId)
            delay(1000)
            val response = httpClient.get<Map<String, List<AllShows>>>("/theatres/$theatreId/shows-by-time")
            val listOfShows = response.body.get()
            response.status shouldBe HttpStatus.OK
            listOfShows.size shouldBe 2
            jsonString(listOfShows.values) shouldBe jsonString(
                listOf(
                    listOf(AllShows(1, Timestamp(currentTime), 1, "War")),
                    listOf(
                        AllShows(2, Timestamp(futureTime), 1, "War")
                    )
                )
            )
        }
    }
}

package com.demo.book

import com.demo.book.movie.request.MovieRequest
import com.demo.book.show.request.ShowRequest
import com.demo.book.theatre.request.TheatreRequest
import com.demo.book.ticket.request.TicketRequest
import com.demo.book.user.request.UserRequest
import com.demo.book.utils.get
import com.demo.book.utils.post
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.kotest.core.spec.style.StringSpec
import io.kotest.core.test.TestCase
import io.micronaut.http.HttpResponse
import io.micronaut.http.client.HttpClient
import io.micronaut.http.client.annotation.Client
import io.micronaut.test.extensions.kotest.annotation.MicronautTest
import norm.executeCommand
import javax.inject.Inject
import javax.sql.DataSource

@MicronautTest
abstract class BaseIntegrationSpec : StringSpec() {

    @Inject
    @field:Client("/api")
    protected lateinit var httpClient: HttpClient

    @Inject
    protected lateinit var dataSource: DataSource

    protected val jsonMapper: ObjectMapper = jacksonObjectMapper().also {
        it.enable(SerializationFeature.INDENT_OUTPUT)
        it.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
    }

    override fun beforeEach(testCase: TestCase) {
        super.beforeEach(testCase)
        clearData()
        addATheatreToDatabase()
    }

    protected fun addATheatreToDatabase() {
        dataSource.connection.use { connection ->
            connection.executeCommand("INSERT INTO theatres (name) VALUES ('Medly');")
        }
    }

    protected fun clearData() {
        dataSource.connection.use { connection ->
            connection.executeCommand("TRUNCATE theatres RESTART IDENTITY CASCADE;").also {
                connection.executeCommand("TRUNCATE users RESTART IDENTITY CASCADE;")
            }
        }
    }

    protected fun jsonString(obj: Any?) = jsonMapper.writeValueAsString(obj)

    fun addTheatre(theatreRequest: TheatreRequest): HttpResponse<Any> {
        return httpClient.post(
            url = "/theatres",
            body = jsonMapper.writeValueAsString(theatreRequest)
        )
    }

    fun addMovieToATheatre(movieRequest: MovieRequest, theatreId: Int): HttpResponse<Any> {
        return httpClient.post(
            url = "/theatres/$theatreId/movies",
            body = jsonMapper.writeValueAsString(movieRequest)
        )
    }

    fun addShowToATheatre(showRequest: ShowRequest, theatreId: Int): HttpResponse<Any> {
        return httpClient.post(
            url = "/theatres/$theatreId/shows",
            body = jsonMapper.writeValueAsString(showRequest)
        )
    }

    fun addUserToSystem(userRequest: UserRequest): HttpResponse<Any> {
        return httpClient.post(
            url = "/users",
            body = jsonMapper.writeValueAsString(userRequest)
        )
    }

    fun bookTicketForShow(ticketRequest: TicketRequest): HttpResponse<Any> {
        return httpClient.post(
            url = "/tickets",
            body = jsonMapper.writeValueAsString(ticketRequest)
        )
    }
}

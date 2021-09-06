package com.demo.book.api

import com.demo.book.BaseIntegrationSpec
import com.demo.book.theatre.entity.Theatre
import com.demo.book.theatre.request.TheatreRequest
import com.demo.book.utils.get
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus
import io.micronaut.http.client.exceptions.HttpClientResponseException

class TheatreApiTest : BaseIntegrationSpec() {

    init {
        "Should save a theatre if name is unique" {
            // When
            val result = addTheatre(TheatreRequest("Theatre"))

            // Then
            result.status shouldBe HttpStatus.OK
            jsonString(result.body.get()) shouldBe jsonString(Theatre(2, "Theatre"))
        }

        "Should throw exception when we add a theatre and if theatre name is empty" {
            // When
            val result = shouldThrow<HttpClientResponseException> { addTheatre(TheatreRequest("")) }

            // Then
            result.status shouldBe HttpStatus.BAD_REQUEST
            result.message shouldBe "Theatre name cannot be empty"
        }

        "Should return all added theatres" {
            // When
            val result = getAllTheatre()

            // Then
            result.status shouldBe HttpStatus.OK
            jsonString(result.body.get()) shouldBe jsonString(listOf(Theatre(1, "Medly")))
        }

        "Should return theatre details using name " {
            // When
            val result = getAllTheatreByName()

            // Then
            result.status shouldBe HttpStatus.OK
            jsonString(result.body.get()) shouldBe jsonString(Theatre(1, "Medly"))
        }

        "Should throw exception if theatre does not exist" {
            // When
            val result = shouldThrow<HttpClientResponseException> { getTheatreByName() }

            // Then
            result.status shouldBe HttpStatus.BAD_REQUEST
        }
    }

    private fun getAllTheatre(): HttpResponse<Any> {
        return httpClient.get(
            url = "/theatres"
        )
    }

    private fun getTheatreByName(): HttpResponse<Any> {
        return httpClient.get(
            url = "/theatres/ABC"
        )
    }

    private fun getAllTheatreByName(): HttpResponse<Any> {
        return httpClient.get(
            url = "/theatres/Medly"
        )
    }
}

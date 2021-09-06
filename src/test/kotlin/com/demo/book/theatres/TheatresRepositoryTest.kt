package com.demo.book.theatres

import com.demo.book.BaseIntegrationSpec
import com.demo.book.theatre.entity.Theatre
import com.demo.book.theatre.repository.TheatreRepository
import com.demo.book.theatre.request.TheatreRequest
import io.kotest.matchers.shouldBe

class TheatresRepositoryTest : BaseIntegrationSpec() {

    init {
        "Should add new theatre" {
            // Given
            val theatreRepository = TheatreRepository(dataSource)

            // When
            val result = theatreRepository.addTheatre(TheatreRequest("Theatre"))

            // Then
            result shouldBe Theatre(2, "Theatre")
        }

        "Should return the list of theatres" {
            // Given
            val theatreRepository = TheatreRepository(dataSource)

            // When
            theatreRepository.addTheatre(TheatreRequest("Theatre"))
            val result = theatreRepository.getAllTheatres()

            // Then
            result shouldBe listOf(Theatre(1, "Medly"), Theatre(2, "Theatre"))
        }

        "Should get theatre by its name" {
            // Given
            val theatreRepository = TheatreRepository(dataSource)

            // When
            val result = theatreRepository.getTheatreFromName("Medly")

            // Then
            result shouldBe Theatre(1, "Medly")
        }

        "Should get theatre by its id" {
            // Given
            val theatreRepository = TheatreRepository(dataSource)

            // When
            val result = theatreRepository.getTheatreFromId(1)

            // Then
            result shouldBe Theatre(1, "Medly")
        }
    }
}

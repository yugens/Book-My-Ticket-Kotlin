package com.demo.book.theatres

import com.demo.book.theatre.entity.Theatre
import com.demo.book.theatre.exception.InvalidTheatreDataException
import com.demo.book.theatre.repository.TheatreRepository
import com.demo.book.theatre.request.TheatreRequest
import com.demo.book.theatre.service.TheatreService
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.core.test.TestCase
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify

class TheatreServiceTest : StringSpec() {
    private var mockTheatresRepository = mockk<TheatreRepository>()
    private var theatreService = TheatreService(mockTheatresRepository)
    override fun beforeEach(testCase: TestCase) {
        super.beforeEach(testCase)
        mockTheatresRepository = mockk(relaxed = true)
        theatreService = TheatreService(mockTheatresRepository)
    }

    init {
        "should throw exception if movie theatre name is empty" {
            every { mockTheatresRepository.getAllTheatres() } returns listOf()
            val exception = shouldThrow<InvalidTheatreDataException> { theatreService.addTheatre(TheatreRequest("")) }
            exception.message shouldBe "Theatre name cannot be empty"
        }

        "should add theatre if valid name" {
            every { mockTheatresRepository.addTheatre(TheatreRequest("ABC")) } returns Theatre(1, "ABC")
            val result = theatreService.addTheatre(TheatreRequest("ABC"))
            result shouldBe Theatre(1, "ABC")
            verify(exactly = 1) { mockTheatresRepository.addTheatre(TheatreRequest("ABC")) }
        }

        "should return  all theatres" {
            every {
                mockTheatresRepository.getAllTheatres()
            } returns listOf(Theatre(1, "Medly"), Theatre(2, "ABC"))

            val result = theatreService.getAllTheatres()
            result shouldBe listOf(Theatre(1, "Medly"), Theatre(2, "ABC"))
            verify(exactly = 1) { mockTheatresRepository.getAllTheatres() }
        }

        "should return theatre details if name is given" {
            every { mockTheatresRepository.getTheatreFromName("Medly") } returns Theatre(1, "Medly")
            every { mockTheatresRepository.getAllTheatres() } returns listOf(Theatre(1, "Medly"))
            val result = theatreService.getTheatreFromName("Medly")
            result shouldBe Theatre(1, "Medly")
            verify(exactly = 1) { mockTheatresRepository.getTheatreFromName("Medly") }
        }

        "should return theatre details if  id is given" {
            every { mockTheatresRepository.getTheatreFromId(1) } returns Theatre(1, "Medly")
            every { mockTheatresRepository.getAllTheatres() } returns listOf(Theatre(1, "Medly"))
            val result = theatreService.getTheatreFromId(1)
            result shouldBe Theatre(1, "Medly")
            verify(exactly = 1) { mockTheatresRepository.getTheatreFromId(1) }
        }
    }
}

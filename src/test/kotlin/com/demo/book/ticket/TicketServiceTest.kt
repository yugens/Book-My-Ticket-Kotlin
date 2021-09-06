package com.demo.book.ticket

import com.demo.book.movie.service.MovieService
import com.demo.book.ticket.entity.Ticket
import com.demo.book.user.entity.User
import com.demo.book.ticket.exception.InvalidTicketDataException
import com.demo.book.ticket.repository.TicketRepository
import com.demo.book.ticket.request.TicketRequest
import com.demo.book.show.service.ShowService
import com.demo.book.ticket.service.TicketService
import com.demo.book.show.entity.Show
import com.demo.book.user.repository.UserRepository
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.sql.Timestamp

class TicketServiceTest : StringSpec({
    var mockTicketRepository = mockk<TicketRepository>(relaxed = true)
    val mockShowService = mockk<ShowService>(relaxed = true)
    val mockUserRepository = mockk<UserRepository>(relaxed = true)
    val mockMovieService = mockk<MovieService>(relaxed = true)
    var ticketService = TicketService(mockTicketRepository, mockShowService, mockUserRepository, mockMovieService)

    beforeEach {
        mockTicketRepository = mockk(relaxed = true)
        ticketService = TicketService(mockTicketRepository, mockShowService, mockUserRepository, mockMovieService)
    }

    "should be able to book tickets if show is within seven days" {
        // Given
        val currentTime = System.currentTimeMillis() + 1 * 24 * 3600 * 1000
        every {
            mockTicketRepository.book(TicketRequest(2, 2, 1))
        } returns 1
        every {
            mockTicketRepository.getTicketDetailsFromTicketNumber(1)
        } returns Ticket(1, "Shivam", "Medly", "War", Timestamp(currentTime), 120)
        every {
            mockShowService.getAllShowsInATheatre(1)
        } returns listOf(Show(2, Timestamp(currentTime), 1, 1, 100, 100))
        every {
            mockUserRepository.getAll()
        } returns listOf(User(2, "shivam", "sh@gmail.com"))
        every { mockShowService.getShowFromId(2) } returns Show(2, Timestamp(currentTime), 1, 1, 100, 100)
        // When
        val ticket = ticketService.bookTicket(TicketRequest(2, 2, 1))

        // Then
        ticket shouldBe Ticket(1, "Shivam", "Medly", "War", Timestamp(currentTime), 120)
        verify(exactly = 1) { mockTicketRepository.book(TicketRequest(2, 2, 1)) }
    }

    "should be able to get all booked tickets" {
        // Given
        every {
            mockTicketRepository.getTicketDetailsFromTicketNumber(1)
        } returns Ticket(1, "Shivam", "Medly", "War", Timestamp(11), 120)

        // When
        val ticket = ticketService.getTicketDetailsFromTicketNumber(1)

        // Then
        ticket shouldBe Ticket(1, "Shivam", "Medly", "War", Timestamp(11), 120)
        verify(exactly = 1) { mockTicketRepository.getTicketDetailsFromTicketNumber(1) }
    }

    "should throw exception when user does not exist" {
        // Given
        val currentTime = System.currentTimeMillis() + 1 * 24 * 3600 * 1000
        every {
            mockShowService.getAllShowsInATheatre(1)
        } returns listOf(Show(1, Timestamp(currentTime), 1, 1, 100, 100))

        // When
        val exception = shouldThrow<InvalidTicketDataException> { ticketService.bookTicket(TicketRequest(1, 1, 1)) }

        // Then
        exception.message shouldBe "This user does not exist"
    }

    "should throw exception when show Id is invalid" {
        // Given
        every {
            mockUserRepository.getAll()
        } returns listOf(User(1, "Shivam", "shivam@gmail.com"))

        // When
        val exception = shouldThrow<InvalidTicketDataException> { ticketService.bookTicket(TicketRequest(7, 1, 1)) }

        // Then
        exception.message shouldBe "This show does not exist"
    }

    "should throw an exception when the booked show is after 7 days" {
        // Given
        val timeInEpoch: Long = System.currentTimeMillis() + 10 * 24 * 3600 * 1000
        every {
            mockShowService.getAllShowsInATheatre(1)
        } returns listOf(Show(2, Timestamp(timeInEpoch), 1, 1, 100, 100))
        every {
            mockUserRepository.getAll()
        } returns listOf(User(2, "shivam", "sh@gmail.com"))

        // When
        val exception = shouldThrow<InvalidTicketDataException> { ticketService.bookTicket(TicketRequest(2, 2, 1)) }

        // Then
        exception.message shouldBe "This show is after 7 days"
    }

    "should throw an exception when the booked show is a past show" {
        // Given
        val timeInEpoch: Long = System.currentTimeMillis() - 10 * 24 * 3600 * 1000
        every {
            mockShowService.getAllShowsInATheatre(1)
        } returns listOf(Show(2, Timestamp(timeInEpoch), 1, 1, 100, 100))
        every {
            mockUserRepository.getAll()
        } returns listOf(User(2, "shivam", "sh@gmail.com"))

        // When
        val exception = shouldThrow<InvalidTicketDataException> { ticketService.bookTicket(TicketRequest(2, 2, 1)) }

        // Then
        exception.message shouldBe "Cannot book ticket for a past show"
    }
})

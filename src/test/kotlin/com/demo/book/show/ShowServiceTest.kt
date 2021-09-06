package com.demo.book.show

import com.demo.book.movie.entity.Movie
import com.demo.book.show.exception.InvalidShowDataException
import com.demo.book.theatre.exception.InvalidTheatreDataException
import com.demo.book.show.exception.OverlapException
import com.demo.book.show.request.ShowRequest
import com.demo.book.movie.service.MovieService
import com.demo.book.show.service.ShowService
import com.demo.book.show.entity.AllShows
import com.demo.book.show.repository.ShowRepository
import com.demo.book.show.entity.Show
import com.demo.book.ticket.entity.Ticket
import com.demo.book.ticket.repository.TicketRepository
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.assertions.throwables.shouldThrowExactly
import io.kotest.core.spec.style.StringSpec
import io.kotest.core.test.TestCase
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.sql.Timestamp

class ShowServiceTest : StringSpec() {

    private var mockShowRepository = mockk<ShowRepository>(relaxed = true)
    private var mockMovieService = mockk<MovieService>(relaxed = true)
    private var mockTicketRepository = mockk<TicketRepository>(relaxed = true)
    private var showService = ShowService(mockShowRepository, mockMovieService, mockTicketRepository)

    override fun beforeEach(testCase: TestCase) {
        super.beforeEach(testCase)
        mockShowRepository = mockk(relaxed = true)
        mockMovieService = mockk(relaxed = true)
        mockTicketRepository = mockk(relaxed = true)
        showService = ShowService(mockShowRepository, mockMovieService, mockTicketRepository)
    }

    init {
        "Should add shows to a theatre when it does not overlap with other shows" {
            val currentTime = System.currentTimeMillis() + 1 * 24 * 3600 * 1000
            val totalSeats = 100
            every { mockMovieService.validateTheatreId(1) } returns Unit
            every { mockShowRepository.addShowToATheatre(ShowRequest(currentTime, 1, totalSeats), 1) } returns Show(
                1,
                Timestamp(currentTime),
                1,
                1,
                totalSeats, totalSeats
            )
            every { mockMovieService.getMoviesInATheatre(1) } returns listOf(Movie(1, "ABCD", 100, 1))

            val currentTimeNew = System.currentTimeMillis() + 2 * 24 * 3600 * 1000
            every { mockShowRepository.addShowToATheatre(ShowRequest(currentTimeNew, 1, totalSeats), 1) } returns Show(
                2,
                Timestamp(currentTimeNew),
                1, 1, totalSeats, totalSeats
            )

            showService.addShowToATheatre(ShowRequest(currentTime, 1, totalSeats), 1)
            showService.addShowToATheatre(ShowRequest(currentTimeNew, 1, totalSeats), 1) shouldBe Show(
                2,
                Timestamp(currentTimeNew),
                1,
                1,
                100, 100
            )

            verify(exactly = 1) { mockShowRepository.addShowToATheatre(ShowRequest(currentTime, 1, 100), 1) }
            verify(exactly = 1) { mockShowRepository.addShowToATheatre(ShowRequest(currentTimeNew, 1, 100), 1) }
        }

        "Should throw exception when we add a show that overlaps with another show in a theatre" {
            val currentTime = System.currentTimeMillis() + 1 * 24 * 3600 * 1000
            val totalSeats = 100
            every { mockMovieService.validateTheatreId(1) } returns Unit
            every { mockMovieService.getMoviesInATheatre(1) } returns listOf(Movie(1, "ABCD", 100, 1))

            every {
                mockShowRepository.addShowToATheatre(
                    ShowRequest(
                        currentTime,
                        1, totalSeats
                    ), 1
                )
            } throws OverlapException("Already have a show scheduled during that time")
            val exception = shouldThrowExactly<OverlapException> {
                showService.addShowToATheatre(ShowRequest(currentTime, 1, totalSeats), 1)
            }
            exception.message shouldBe "Already have a show scheduled during that time"
        }

        "Should get all added shows in a theatre in reverse chronological order with their remaining seats" {
            val currentTime = System.currentTimeMillis() + 1 * 24 * 3600 * 1000
            val currentTimeNew = System.currentTimeMillis() + 2 * 24 * 3600 * 1000
            val totalSeats = 100

            every {
                mockMovieService.getMoviesInATheatre(1)
            } returns listOf(Movie(1, "ABCD", 100, 1))

            every {
                mockMovieService.validateTheatreId(1)
            } returns Unit

            every {
                mockShowRepository.getAllShowsInATheatre(1)
            } returns listOf(
                Show(1, Timestamp(currentTime), 7, 1, totalSeats, 100),
                Show(2, Timestamp(currentTimeNew), 100, 1, totalSeats, 100)
            )

            val result = showService.getAllShowsInATheatre(1)
            result shouldBe listOf(
                Show(2, Timestamp(currentTimeNew), 100, 1, 100, 100),
                Show(1, Timestamp(currentTime), 7, 1, 100, 100)
            )
            verify(exactly = 1) { mockShowRepository.getAllShowsInATheatre(1) }
        }

        "Should return empty list when movie theatre system has no shows added to the system " {
            every {
                mockShowRepository.getAllShowsInATheatre(1)
            } returns listOf()
            every { mockMovieService.validateTheatreId(1) } returns Unit
            every { mockMovieService.getMoviesInATheatre(1) } returns listOf(Movie(1, "ABCD", 100, 1))
            val result = showService.getAllShowsInATheatre(1)
            result shouldBe emptyList()
            verify(exactly = 1) { mockShowRepository.getAllShowsInATheatre(1) }
        }

        "Should be able to get all tickets for a show in a theatre" {
            // Given
            every {
                mockShowRepository.getTicketsForAShow(2, 1)
            } returns listOf(1)
            every { mockMovieService.getMoviesInATheatre(1) } returns listOf(Movie(1, "ABCD", 100, 1))

            every {
                mockTicketRepository.getTicketDetailsFromTicketNumber(1)
            } returns Ticket(1, "Shivam", "Medly", "War", Timestamp(11), 120)

            // When
            val listTicket = showService.getTicketsForAShow(2, 1)

            // Then
            listTicket shouldBe listOf(Ticket(1, "Shivam", "Medly", "War", Timestamp(11), 120))
            verify(exactly = 1) { mockShowRepository.getTicketsForAShow(2, 1) }
        }

        "Should throw exception when we add show in a theatre and theatreId is invalid" {
            // Given
            val currentTime = System.currentTimeMillis() + 1 * 24 * 3600 * 1000
            every { mockMovieService.validateTheatreId(3) } throws
                    InvalidTheatreDataException("This theatre does not exist")

            // When
            val exception =
                shouldThrow<InvalidTheatreDataException> {
                    showService.addShowToATheatre(
                        ShowRequest(currentTime, 1, 100), 3
                    )
                }

            // Then
            exception.message shouldBe "This theatre does not exist"
        }

        "Should throw exception when we add show in a theatre and movieId is invalid" {
            // Given
            val currentTime = System.currentTimeMillis() + 1 * 24 * 3600 * 1000
            every { mockMovieService.validateTheatreId(1) } returns Unit

            // When
            val exception =
                shouldThrow<InvalidShowDataException> {
                    showService.addShowToATheatre(
                        ShowRequest(currentTime, 10, 100), 1
                    )
                }

            // Then
            exception.message shouldBe "Movie with this id does not exist in this theatre"
        }

        "should give shows in past, ongoing , upcoming order" {
            val currentTime = System.currentTimeMillis()
            val futureTime = System.currentTimeMillis() + 1 * 24 * 3600 * 1000
            val pastTime = System.currentTimeMillis() - 1 * 24 * 3600 * 1000
            every {
                mockMovieService.getMoviesInATheatre(1)
            } returns listOf(Movie(1, "ABCD", 100, 1))

            every {
                mockMovieService.validateTheatreId(1)
            } returns Unit
            every { mockMovieService.getMovieByMovieId(1, 1) } returns Movie(1, "ABCD", 100, 1)

            every {
                mockShowRepository.getAllShowsInATheatre(1)
            } returns listOf(
                Show(1, Timestamp(futureTime), 1, 1, 100, 100),
                Show(2, Timestamp(currentTime), 1, 1, 100, 100),
                Show(3, Timestamp(pastTime), 1, 1, 100, 100)

            )
            val result = showService.allShowsByOrder(1)
            result shouldBe mapOf(
                Pair("Past:", listOf(AllShows(3, Timestamp(pastTime), 1, "ABCD"))),
                Pair("Ongoing:", listOf(AllShows(2, Timestamp(currentTime), 1, "ABCD"))),
                Pair("Upcoming:", listOf(AllShows(1, Timestamp(futureTime), 1, "ABCD")))
            )
        }

        "should throw exception if theaterId is invalid" {
            every { mockMovieService.validateTheatreId(3) } throws
                    InvalidTheatreDataException("Theatre with this id does not exist")
            val exception = shouldThrow<InvalidTheatreDataException> { showService.allShowsByOrder(3) }
            exception.message shouldBe "Theatre with this id does not exist"
        }
    }
}

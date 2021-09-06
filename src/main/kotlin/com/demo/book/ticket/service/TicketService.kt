package com.demo.book.ticket.service

import com.demo.book.movie.service.MovieService
import com.demo.book.show.service.ShowService
import com.demo.book.ticket.entity.Ticket
import com.demo.book.ticket.exception.InvalidTicketDataException
import com.demo.book.ticket.repository.TicketRepository
import com.demo.book.ticket.request.TicketRequest
import com.demo.book.user.repository.UserRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TicketService(
    @Inject val ticketRepository: TicketRepository,
    @Inject val showService: ShowService,
    @Inject val userRepository: UserRepository,
    @Inject val movieService: MovieService
) {

    fun bookTicket(ticketRequest: TicketRequest): Ticket {
        validateTicketDetails(ticketRequest)
        val show = showService.getShowFromId(ticketRequest.showId)
        validateRemainingSeats(show.remainingSeats)
        val ticketNumber = ticketRepository.book(ticketRequest)
        return ticketRepository.getTicketDetailsFromTicketNumber(ticketNumber)
    }

    fun getTicketDetailsFromTicketNumber(ticketNumber: Int): Ticket {
        try {
            return ticketRepository.getTicketDetailsFromTicketNumber(ticketNumber)
        } catch (ex: Exception) {
            throw InvalidTicketDataException("This ticket number is invalid")
        }
    }

    private fun validateTicketDetails(ticketRequest: TicketRequest) {
        movieService.validateTheatreId(ticketRequest.theatreId)
        validateUser(ticketRequest)
        validateShow(ticketRequest)
    }

    private fun validateUser(ticketRequest: TicketRequest) {
        val allUsers = userRepository.getAll()
        var isUserAvailable = false
        for (user in allUsers) {
            if (user.id == ticketRequest.userId)
                isUserAvailable = true
        }
        if (!isUserAvailable) {
            throw InvalidTicketDataException("This user does not exist")
        }
    }

    private fun validateRemainingSeats(seats: Int) {
        if (seats <= 0) {
            throw InvalidTicketDataException("All seats for this show are booked")
        }
    }

    private fun validateShow(ticketRequest: TicketRequest) {
        val allShows = showService.getAllShowsInATheatre(ticketRequest.theatreId)
        var isShowAvailable = false
        for (show in allShows) {
            if (show.id == ticketRequest.showId) {
                isShowAvailable = true

                val showStartTime = show.startTime.time
                val currentTime = System.currentTimeMillis()
                val timeOf7DaysInMillis = 7 * 24 * 3600 * 1000

                if (showStartTime - currentTime > timeOf7DaysInMillis) {
                    throw InvalidTicketDataException("This show is after 7 days")
                } else if (currentTime > showStartTime) {
                    throw InvalidTicketDataException("Cannot book ticket for a past show")
                }
            }
        }
        if (!isShowAvailable) {
            throw InvalidTicketDataException("This show does not exist")
        }
    }
}

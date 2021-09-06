package com.demo.book.ticket.repository

import com.demo.book.ticket.request.TicketRequest
import com.demo.book.ticket.entity.Ticket
import norm.query
import ticket.*
import javax.inject.Inject
import javax.inject.Singleton
import javax.sql.DataSource

@Singleton
class TicketRepository(@Inject private val datasource: DataSource) {

    fun book(ticketToBook: TicketRequest): Int = datasource.connection.use { connection ->
        AddTicketQuery().query(
            connection,
            AddTicketParams(
                ticketToBook.showId,
                ticketToBook.userId,
                ticketToBook.theatreId
            )
        )
    }.map {
        it.ticketNumber
    }.first()

    fun getTicketDetailsFromTicketNumber(ticketNumber: Int): Ticket = datasource.connection.use { connection ->
        GetTicketForAUserQuery().query(
            connection,
            GetTicketForAUserParams(ticketNumber)
        )
    }.map {
        Ticket(
            it.ticketNumber,
            it.userName,
            it.theatreName,
            it.movieName,
            it.startTime,
            it.durationInMinutes
        )
    }.first()
}

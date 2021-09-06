package com.demo.book.api

import com.demo.book.ticket.entity.Ticket
import com.demo.book.ticket.request.TicketRequest
import com.demo.book.ticket.service.TicketService
import io.micronaut.http.HttpResponse
import io.micronaut.http.MutableHttpResponse
import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.Post
import javax.inject.Inject

@Controller
class TicketApi(@Inject val ticketService: TicketService) {

    @Get("/tickets/{ticketNumber}")
    fun getTicketDetails(ticketNumber: Int): HttpResponse<Ticket> {
        return HttpResponse.ok(ticketService.getTicketDetailsFromTicketNumber(ticketNumber))
    }

    @Post("/tickets")
    fun bookTicket(@Body ticketRequest: TicketRequest): MutableHttpResponse<Ticket> {
        return HttpResponse.ok(ticketService.bookTicket(ticketRequest))
    }
}

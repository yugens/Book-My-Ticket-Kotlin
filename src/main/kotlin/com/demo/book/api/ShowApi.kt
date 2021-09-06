package com.demo.book.api

import com.demo.book.ticket.entity.Ticket
import com.demo.book.show.request.ShowRequest
import com.demo.book.show.service.ShowService
import com.demo.book.show.entity.Show
import com.demo.book.show.entity.AllShows
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.*
import javax.inject.Inject

@Controller("/theatres")
class ShowApi(@Inject val showService: ShowService) {

    @Get("/{theatreId}/shows")
    fun allShows(@QueryValue theatreId: Int): HttpResponse<List<Show>> {
        return HttpResponse.ok(showService.getAllShowsInATheatre(theatreId))
    }

    @Post("/{theatreId}/shows")
    fun saveShows(@Body showRequest: ShowRequest, @QueryValue theatreId: Int): HttpResponse<Show> {
        return HttpResponse.ok(showService.addShowToATheatre(showRequest, theatreId))
    }

    @Get("/{theatreId}/shows/{showId}/tickets")
    fun ticketsForAShow(showId: Int, @QueryValue theatreId: Int): HttpResponse<List<Ticket>> {
        return HttpResponse.ok(showService.getTicketsForAShow(showId, theatreId))
    }

    @Get("/{theatreId}/shows-by-time")
    fun getShowsByTime(@PathVariable theatreId: Int): HttpResponse<Map<String, List<AllShows>>> {
        return HttpResponse.ok(showService.allShowsByOrder(theatreId))
    }
}

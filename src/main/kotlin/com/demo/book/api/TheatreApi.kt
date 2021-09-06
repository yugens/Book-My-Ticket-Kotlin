package com.demo.book.api

import com.demo.book.theatre.entity.Theatre
import com.demo.book.theatre.request.TheatreRequest
import com.demo.book.theatre.service.TheatreService
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.Post
import javax.inject.Inject

@Controller
class TheatreApi(@Inject val theatreService: TheatreService) {

    @Get("/theatres")
    fun getAllTheatres(): HttpResponse<List<Theatre>> {
        return HttpResponse.ok(theatreService.getAllTheatres())
    }

    @Post("/theatres")
    fun addTheatre(@Body theatreRequest: TheatreRequest): HttpResponse<Theatre> {
        return HttpResponse.ok(theatreService.addTheatre(theatreRequest))
    }

    @Get("/theatres/{name}")
    fun getTheatreFromName(@Body name: String): HttpResponse<Theatre> {
        return HttpResponse.ok(theatreService.getTheatreFromName(name))
    }
}

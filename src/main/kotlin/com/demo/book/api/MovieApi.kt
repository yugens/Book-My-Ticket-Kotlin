package com.demo.book.api

import com.demo.book.movie.entity.Movie
import com.demo.book.movie.request.MovieRequest
import com.demo.book.movie.service.MovieService
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.*
import javax.inject.Inject

@Controller("/theatres")
class MovieApi(@Inject val movieService: MovieService) {

    @Get("/{theatreId}/movies")
    fun moviesForATheatre(@QueryValue theatreId: Int): HttpResponse<List<Movie>> {
        return HttpResponse.ok(movieService.getMoviesInATheatre(theatreId))
    }

    @Post("/{theatreId}/movies")
    fun saveMovieToATheatre(@Body movieRequest: MovieRequest, @QueryValue theatreId: Int): HttpResponse<Movie> {
        return HttpResponse.ok(movieService.addMovieToATheatre(movieRequest, theatreId))
    }
}

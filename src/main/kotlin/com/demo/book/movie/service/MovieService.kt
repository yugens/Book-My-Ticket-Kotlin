package com.demo.book.movie.service

import com.demo.book.movie.entity.Movie
import com.demo.book.movie.exception.InvalidMovieDataException
import com.demo.book.movie.repository.MovieRepository
import com.demo.book.movie.request.MovieRequest
import com.demo.book.theatre.service.TheatreService
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MovieService(@Inject val movieRepository: MovieRepository, @Inject val theatreService: TheatreService) {

    fun addMovieToATheatre(movieRequest: MovieRequest, theatreId: Int): Movie {
        validateTheatreId(theatreId)

        if (movieRequest.title == "")
            throw InvalidMovieDataException("Movie Name cannot be empty")

        validateMovieName(movieRequest.title, theatreId)

        if (movieRequest.durationInMinutes < 5)
            throw InvalidMovieDataException("Movie duration cannot be less than 5 minutes")
        else if (movieRequest.durationInMinutes > 360)
            throw InvalidMovieDataException("Movie duration cannot be more than 6 hours")

        return movieRepository.addMovieToATheatre(movieRequest, theatreId)
    }

    fun getMoviesInATheatre(theatreId: Int): List<Movie> {
        validateTheatreId(theatreId)

        return movieRepository.getMoviesInATheatre(theatreId)
    }

    fun validateTheatreId(theatreId: Int) {
        theatreService.validateTheatreId(theatreId)
    }

    private fun validateMovieName(name: String, theatreId: Int) {
        val movieNameList = getMoviesInATheatre(theatreId).map { it.title.toLowerCase() }

        if (name.toLowerCase() in movieNameList)
            throw InvalidMovieDataException("Movie with this name already exists in this theatre")
    }

    fun getMovieByMovieId(movieId: Int, theatreId: Int): Movie {
        val movieList = getMoviesInATheatre(theatreId)
        var result = Movie(0, "", 0, 0)
        for (movie in movieList) {
            if (movie.id == movieId) {
                result = movie
                break
            }
        }
        return result
    }
}

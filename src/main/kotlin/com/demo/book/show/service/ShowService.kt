package com.demo.book.show.service

import com.demo.book.ticket.entity.Ticket
import com.demo.book.movie.service.MovieService
import com.demo.book.show.entity.Show
import com.demo.book.show.entity.AllShows
import java.sql.Timestamp
import com.demo.book.show.exception.InvalidShowDataException
import com.demo.book.show.exception.OverlapException
import com.demo.book.show.repository.ShowRepository
import com.demo.book.show.request.ShowRequest
import com.demo.book.ticket.repository.TicketRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ShowService(
    @Inject val showRepository: ShowRepository,
    @Inject val movieService: MovieService,
    @Inject val ticketRepository: TicketRepository
) {

    fun addShowToATheatre(showRequest: ShowRequest, theatreId: Int): Show {
        validateTheatreId(theatreId)
        validateTotalSeats(showRequest.totalSeats)
        validateMovieId(showRequest.movieId, theatreId)

        validateShowOverlap(theatreId, showRequest)

        return showRepository.addShowToATheatre(showRequest, theatreId)
    }

    private fun validateTotalSeats(totalSeats: Int) {
        if (totalSeats <= 0) {
            throw InvalidShowDataException("Total Seats cannot be zero")
        }
    }

    fun getAllShowsInATheatre(theatreId: Int): List<Show> {
        validateTheatreId(theatreId)

        return showRepository.getAllShowsInATheatre(theatreId).sortedByDescending { it.startTime.time }
    }

    fun getShowFromId(showId: Int): Show {
        return showRepository.getShowFromId(showId)
    }

    fun getTicketsForAShow(showId: Int, theatreId: Int): List<Ticket> {
        return showRepository.getTicketsForAShow(showId, theatreId)
            .map { ticketRepository.getTicketDetailsFromTicketNumber(it) }
    }

    fun allShowsByOrder(theatreId: Int): Map<String, List<AllShows>> {
        validateTheatreId(theatreId)
        val allShows: List<Show> =
            showRepository.getAllShowsInATheatre(theatreId)
        val past: ArrayList<AllShows> = ArrayList<AllShows>()
        val upComing: ArrayList<AllShows> = ArrayList<AllShows>()
        val onGoing: ArrayList<AllShows> = ArrayList<AllShows>()
        for (show in allShows) {
            val currentTimeMillis = System.currentTimeMillis()
            val showStartTime = show.startTime.time
            val showEndTime = showStartTime + movieService.getMovieByMovieId(
                show.movieId,
                show.theatreId
            ).durationInMinutes * 60 * 1000
            if (currentTimeMillis > showStartTime && currentTimeMillis < showEndTime) {
                onGoing.add(
                    AllShows(
                        show.id,
                        Timestamp(showStartTime),
                        show.movieId,
                        movieService.getMovieByMovieId(show.movieId, show.theatreId).title
                    )
                )
            } else if (showStartTime < currentTimeMillis &&
                currentTimeMillis >
                showEndTime
            ) {
                past.add(
                    AllShows(
                        show.id,
                        show.startTime,
                        show.movieId,
                        movieService.getMovieByMovieId(show.movieId, show.theatreId).title
                    )
                )
            } else if (showStartTime > currentTimeMillis) {
                upComing.add(
                    AllShows(
                        show.id,
                        show.startTime,
                        show.movieId,
                        movieService.getMovieByMovieId(show.movieId, show.theatreId).title
                    )
                )
            }
        }
        past.sortByDescending { it.startTime }
        upComing.sortBy { it.startTime }
        onGoing.sortBy { it.startTime.time + getShowDuration(it.movieId, theatreId) * 60 * 1000 }
        return mapOf(
            Pair("Past:", past),
            Pair("Ongoing:", onGoing),
            Pair("Upcoming:", upComing)
        )
    }

    private fun getShowDuration(movieId: Int, theatreId: Int): Int {
        val allMovies = movieService.getMoviesInATheatre(theatreId)
        for (movie in allMovies) {
            if (movie.id == movieId) {
                return movie.durationInMinutes
            }
        }
        throw InvalidShowDataException("Movie with this id does not exist in this theatre")
    }

    private fun validateMovieId(movieId: Int, theatreId: Int) {
        val movieIdList = movieService.getMoviesInATheatre(theatreId).map { it.id }

        if (movieId !in movieIdList)
            throw InvalidShowDataException("Movie with this id does not exist in this theatre")
    }

    private fun validateTheatreId(theatreId: Int) {
        movieService.validateTheatreId(theatreId)
    }

    private fun validateShowId(theatreId: Int, showId: Int) {
        val listOfShowsId = getAllShowsInATheatre(theatreId).map { it.id }

        if (showId !in listOfShowsId)
            throw InvalidShowDataException("Show with this id does not exist in this theatre")
    }

    private fun validateShowOverlap(theatreId: Int, showRequest: ShowRequest) {
        val allShowsList = getAllShowsInATheatre(theatreId)

        if (showRequest.startTime < System.currentTimeMillis())
            throw InvalidShowDataException("Cannot create a show for past date")

        for (show in allShowsList) {
            val showMovieDuration = getShowDuration(show.movieId, theatreId)
            val showRequestMovieDuration = getShowDuration(showRequest.movieId, theatreId)
            val startTimeOfShow = show.startTime.time
            val endTimeOfShow = startTimeOfShow + showMovieDuration * 60 * 1000
            val startTimeOfShowRequest = showRequest.startTime
            val endTimeOfShowRequest = startTimeOfShowRequest + showRequestMovieDuration * 60 * 1000

            if ((startTimeOfShowRequest >= startTimeOfShow) && (startTimeOfShowRequest <= endTimeOfShow))
                throw OverlapException("Already have a show scheduled during that time")
            else if ((endTimeOfShowRequest >= startTimeOfShow) && (endTimeOfShowRequest <= endTimeOfShow))
                throw OverlapException("Already have a show scheduled during that time")
        }
    }
}

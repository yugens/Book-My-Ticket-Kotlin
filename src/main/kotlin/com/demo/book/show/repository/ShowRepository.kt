package com.demo.book.show.repository

import com.demo.book.show.request.ShowRequest
import com.demo.book.show.entity.Show
import norm.query
import show.*
import ticket.GetTicketsForAShowParams
import ticket.GetTicketsForAShowQuery
import java.sql.Timestamp
import javax.inject.Inject
import javax.inject.Singleton
import javax.sql.DataSource

@Singleton
class ShowRepository(@Inject private val datasource: DataSource) {

    fun addShowToATheatre(showToSave: ShowRequest, theatreId: Int): Show = datasource.connection.use { connection ->
        SaveShowsQuery().query(
            connection,
            SaveShowsParams(
                Timestamp(showToSave.startTime),
                showToSave.movieId,
                theatreId,
                showToSave.totalSeats
            )
        )
    }.map {
        Show(
            it.id,
            it.startTime,
            it.movieId!!,
            it.theatreId!!,
            it.totalSeats,
            it.totalSeats
        )
    }.first()

    fun getAllShowsInATheatre(theatreId: Int): List<Show> = datasource.connection.use { connection ->
        GetAllShowsInATheatreQuery().query(
            connection,
            GetAllShowsInATheatreParams(theatreId)
        )
    }.map {
        Show(
            it.id,
            it.startTime,
            it.movieId!!,
            it.theatreId!!,
            it.totalSeats,
            it.totalSeats - getCountOfTicketsOfAShow(it.id)
        )
    }

    fun getTicketsForAShow(showId: Int, theatreId: Int): List<Int> = datasource.connection.use { connection ->
        GetTicketsForAShowQuery().query(
            connection,
            GetTicketsForAShowParams(
                showId,
                theatreId
            )
        )
    }.map {
        it.ticketNumber
    }

    fun getCountOfTicketsOfAShow(showId: Int): Int = datasource.connection.use { connection ->
        GetCountOfTicketsOfAShowQuery().query(
            connection,
            GetCountOfTicketsOfAShowParams(
                showId
            )
        ).first().count!!.toInt()
    }

    fun getShowFromId(showId: Int): Show = datasource.connection.use { connection ->
        GetShowFromIdQuery().query(
            connection,
            GetShowFromIdParams(
                showId
            )
        ).map {
            Show(
                it.id,
                it.startTime,
                it.movieId!!,
                it.theatreId!!,
                it.totalSeats,
                it.totalSeats - getCountOfTicketsOfAShow(it.id)
            )
        }
    }.first()
}

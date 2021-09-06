package com.demo.book.movie.repository

import com.demo.book.movie.entity.Movie
import com.demo.book.movie.request.MovieRequest
import movie.GetMoviesInATheatreParams
import movie.GetMoviesInATheatreQuery
import movie.SaveMovieParams
import movie.SaveMovieQuery
import norm.query
import javax.inject.Inject
import javax.inject.Singleton
import javax.sql.DataSource

@Singleton
class MovieRepository(@Inject private val datasource: DataSource) {

    fun addMovieToATheatre(movieToSave: MovieRequest, theatreId: Int): Movie = datasource.connection.use { connection ->
        SaveMovieQuery().query(
            connection,
            SaveMovieParams(
                movieToSave.title,
                movieToSave.durationInMinutes,
                theatreId
            )
        )
    }.map {
        Movie(
            it.id,
            it.title,
            it.durationInMinutes,
            it.theatreId
        )
    }.first()

    fun getMoviesInATheatre(theatreId: Int): List<Movie> = datasource.connection.use { connection ->
        GetMoviesInATheatreQuery().query(
            connection,
            GetMoviesInATheatreParams(
                theatreId
            )
        )
    }.map {
        Movie(
            it.id,
            it.title,
            it.durationInMinutes,
            it.theatreId
        )
    }
}

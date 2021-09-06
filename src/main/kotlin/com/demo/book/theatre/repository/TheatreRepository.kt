package com.demo.book.theatre.repository

import com.demo.book.theatre.request.TheatreRequest
import com.demo.book.theatre.entity.Theatre
import norm.query
import theatre.*
import javax.inject.Inject
import javax.inject.Singleton
import javax.sql.DataSource

@Singleton
class TheatreRepository(@Inject private val dataSource: DataSource) {

    fun addTheatre(theatreRequest: TheatreRequest): Theatre = dataSource.connection.use { connection ->
        SaveTheatreQuery().query(
            connection,
            SaveTheatreParams(
                theatreRequest.name
            )
        )
    }.map {
        Theatre(
            it.id,
            it.name
        )
    }.first()

    fun getAllTheatres(): List<Theatre> = dataSource.connection.use { connection ->
        GetAllTheatresQuery().query(
            connection,
            GetAllTheatresParams()
        )
    }.map {
        Theatre(
            it.id,
            it.name
        )
    }

    fun getTheatreFromName(name: String): Theatre = dataSource.connection.use { connection ->
        GetTheatreFromNameQuery().query(
            connection,
            GetTheatreFromNameParams(name)
        )
    }.map {
        Theatre(
            it.id,
            it.name
        )
    }.first()

    fun getTheatreFromId(id: Int): Theatre = dataSource.connection.use { connection ->
        GetTheatreFromIdQuery().query(
            connection,
            GetTheatreFromIdParams(id)
        )
    }.map {
        Theatre(
            it.id,
            it.name
        )
    }.first()
}

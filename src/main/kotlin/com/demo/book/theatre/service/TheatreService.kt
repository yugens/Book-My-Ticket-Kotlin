package com.demo.book.theatre.service

import com.demo.book.theatre.entity.Theatre
import com.demo.book.theatre.exception.InvalidTheatreDataException
import com.demo.book.theatre.repository.TheatreRepository
import com.demo.book.theatre.request.TheatreRequest
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TheatreService(@Inject val theatreRepository: TheatreRepository) {

    fun addTheatre(theatreRequest: TheatreRequest): Theatre {
        checkIfTheatreWithNameExists(theatreRequest.name)

        if (theatreRequest.name == "")
            throw InvalidTheatreDataException("Theatre name cannot be empty")

        return theatreRepository.addTheatre(theatreRequest)
    }

    fun getAllTheatres(): List<Theatre> {
        return theatreRepository.getAllTheatres()
    }

    fun getTheatreFromName(name: String): Theatre {
        validateTheatreName(name)

        return theatreRepository.getTheatreFromName(name)
    }

    fun getTheatreFromId(theatreId: Int): Theatre {
        validateTheatreId(theatreId)

        return theatreRepository.getTheatreFromId(theatreId)
    }

    private fun checkIfTheatreWithNameExists(name: String) {
        val theatreNameList = getAllTheatres().map { it.name }

        if (name in theatreNameList)
            throw InvalidTheatreDataException("Theatre with this name already exists")
    }

    private fun validateTheatreName(name: String) {
        val theatreNameList = getAllTheatres().map { it.name }

        if (name !in theatreNameList)
            throw InvalidTheatreDataException("Theatre with this name does not exist")
    }

    fun validateTheatreId(theatreId: Int) {
        val theatreIdList = getAllTheatres().map { it.id }

        if (theatreId !in theatreIdList)
            throw InvalidTheatreDataException("Theatre with this id does not exist")
    }
}

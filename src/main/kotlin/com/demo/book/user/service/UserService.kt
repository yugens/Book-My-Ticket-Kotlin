package com.demo.book.user.service

import com.demo.book.ticket.entity.Ticket
import com.demo.book.ticket.service.TicketService
import com.demo.book.user.entity.User
import com.demo.book.user.exception.InvalidUserDataException
import com.demo.book.user.repository.UserRepository
import com.demo.book.user.request.UserRequest
import java.util.regex.Pattern
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserService(@Inject val userRepository: UserRepository, @Inject val ticketService: TicketService) {

    fun addUser(userRequest: UserRequest): User {
        if (userRequest.name == "" || userRequest.email == "")
            throw InvalidUserDataException("User Details can't be empty")
        if (!isEmailValid(userRequest.email))
            throw InvalidUserDataException("Email not valid")
        validateEmail(userRequest.email)

        return userRepository.add(userRequest)
    }

    fun getAllUsers(): List<User> {
        return userRepository.getAll()
    }

    fun getTicketsForAUser(userId: Int): List<Ticket> {
        validateUser(userId)
        return userRepository.getTicketsForAUser(userId).map { ticketService.getTicketDetailsFromTicketNumber(it) }
    }

    private fun isEmailValid(email: String): Boolean {
        return Pattern.compile(
            "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
                    "\\@" +
                    "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                    "(" +
                    "\\." +
                    "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
                    ")+"
        ).matcher(email).matches()
    }

    private fun validateUser(userId: Int) {
        val allUsers = getAllUsers()
        var isUserAvailable = false
        for (user in allUsers) {
            if (user.id == userId)
                isUserAvailable = true
        }
        if (!isUserAvailable) {
            throw InvalidUserDataException("This user does not exist")
        }
    }

    private fun validateEmail(email: String) {

        val userEmailList = getAllUsers().map { it.email }

        if (email in userEmailList)
            throw InvalidUserDataException("User already exists")
    }
}

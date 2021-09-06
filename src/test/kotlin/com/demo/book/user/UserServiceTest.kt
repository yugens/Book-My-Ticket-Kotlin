package com.demo.book.user

import com.demo.book.ticket.entity.Ticket
import com.demo.book.ticket.service.TicketService
import com.demo.book.user.entity.User
import com.demo.book.user.exception.InvalidUserDataException
import com.demo.book.user.repository.UserRepository
import com.demo.book.user.request.UserRequest
import com.demo.book.user.service.UserService
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.assertions.throwables.shouldThrowExactly
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.sql.Timestamp

class UserServiceTest : StringSpec({
    var mockUserRepository = mockk<UserRepository>(relaxed = true)
    var mockTicketService = mockk<TicketService>(relaxed = true)
    var userService = UserService(mockUserRepository, mockTicketService)

    beforeEach {
        mockUserRepository = mockk(relaxed = true)
        mockTicketService = mockk(relaxed = true)
        userService = UserService(mockUserRepository, mockTicketService)
    }

    "should ensure we can add a user if details are valid" {
        // Given
        every {
            mockUserRepository.add(UserRequest("Shivam", "shivam@gmail.com"))
        } returns User(1, "Shivam", "shivam@gmail.com")

        // When
        val user = userService.addUser(UserRequest("Shivam", "shivam@gmail.com"))

        // Then
        user shouldBe User(1, "Shivam", "shivam@gmail.com")
        verify(exactly = 1) { mockUserRepository.add(UserRequest("Shivam", "shivam@gmail.com")) }
    }

    "should throw an exception if the same user is added twice" {
        // Given
        every {
            mockUserRepository.getAll()
        } returns listOf(User(1, "Shivam", "shivam@gmail.com"))

        // When
        val exception = shouldThrowExactly<InvalidUserDataException> {
            userService.addUser(UserRequest("Shivam", "shivam@gmail.com"))
        }

        // Then
        exception.message shouldBe "User already exists"
    }

    "should return all added users" {
        // Given
        every {
            mockUserRepository.getAll()
        } returns listOf(User(1, "Shivam", "shivam@gmail.com"))

        // When
        val userList = userService.getAllUsers()

        // Then
        userList shouldBe listOf(User(1, "Shivam", "shivam@gmail.com"))
        verify(exactly = 1) { mockUserRepository.getAll() }
    }

    "should be able to get all tickets for a user" {
        // Given
        every {
            mockUserRepository.getTicketsForAUser(1)
        } returns listOf(1)
        every {
            mockUserRepository.getAll()
        } returns listOf(User(1, "Shivam", "shivam@gmail.com"))

        every {
            mockTicketService.getTicketDetailsFromTicketNumber(1)
        } returns Ticket(1, "Shivam", "Medly", "War", Timestamp(11), 120)

        // When
        val listTicket = userService.getTicketsForAUser(1)

        // Then
        listTicket shouldBe listOf(Ticket(1, "Shivam", "Medly", "War", Timestamp(11), 120))
        verify(exactly = 1) { mockUserRepository.getTicketsForAUser(1) }
    }

    "should throw exception when try to add user with empty detail" {
        val exception =
            shouldThrow<InvalidUserDataException> {
                userService.addUser(
                    UserRequest("", "")
                )
            }

        exception.message shouldBe "User Details can't be empty"
    }

    "should throw exception when try to add user with invalid email" {
        val exception =
            shouldThrow<InvalidUserDataException> {
                userService.addUser(
                    UserRequest("Shivam", "shivam.com")
                )
            }

        exception.message shouldBe "Email not valid"
    }
})

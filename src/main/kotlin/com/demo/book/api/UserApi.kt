package com.demo.book.api

import com.demo.book.ticket.entity.Ticket
import com.demo.book.user.entity.User
import com.demo.book.user.request.UserRequest
import com.demo.book.user.service.UserService
import io.micronaut.http.HttpResponse
import io.micronaut.http.MutableHttpResponse
import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.Post
import javax.inject.Inject

@Controller
class UserApi(@Inject val userService: UserService) {

    @Get("/users")
    fun allUsers(): HttpResponse<List<User>> {
        return HttpResponse.ok(userService.getAllUsers())
    }

    @Post("/users")
    fun addUser(@Body userRequest: UserRequest): MutableHttpResponse<User> {
        return HttpResponse.ok(userService.addUser(userRequest))
    }

    @Get("/users/{userId}/tickets")
    fun ticketsForAUser(userId: Int): MutableHttpResponse<List<Ticket>> {
        return HttpResponse.ok(userService.getTicketsForAUser(userId))
    }
}

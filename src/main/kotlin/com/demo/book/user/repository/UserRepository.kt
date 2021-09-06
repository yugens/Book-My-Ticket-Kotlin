package com.demo.book.user.repository

import com.demo.book.user.request.UserRequest
import com.demo.book.user.entity.User
import norm.query
import ticket.GetTicketsForUserParams
import ticket.GetTicketsForUserQuery
import user.AddUserParams
import user.AddUserQuery
import user.GetAllUsersParams
import user.GetAllUsersQuery
import javax.inject.Inject
import javax.inject.Singleton
import javax.sql.DataSource

@Singleton
class UserRepository(@Inject private val datasource: DataSource) {

    fun add(userToAdd: UserRequest): User = datasource.connection.use { connection ->
        AddUserQuery().query(
            connection,
            AddUserParams(
                userToAdd.name,
                userToAdd.email
            )
        )
    }.map {
        User(
            it.id,
            it.name,
            it.email
        )
    }.first()

    fun getAll(): List<User> = datasource.connection.use { connection ->
        GetAllUsersQuery().query(
            connection,
            GetAllUsersParams()
        )
    }.map {
        User(
            it.id,
            it.name,
            it.email
        )
    }

    fun getTicketsForAUser(userId: Int): List<Int> = datasource.connection.use { connection ->
        GetTicketsForUserQuery().query(
            connection,
            GetTicketsForUserParams(
                userId
            )
        )
    }.map {
        it.ticketNumber
    }
}

package com.demo.book.ticket.entity

import com.fasterxml.jackson.annotation.JsonFormat
import java.sql.Timestamp

data class Ticket(
    val ticketNumber: Int,
    val userName: String,
    val theatreName: String,
    val movieName: String,
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+5:30")
    val startTime: Timestamp,
    val durationInMinutes: Int
)

package com.demo.book.show.entity

import com.fasterxml.jackson.annotation.JsonFormat
import java.sql.Timestamp

data class Show(
    val id: Int,
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+5:30")
    val startTime: Timestamp,
    val movieId: Int,
    val theatreId: Int,
    val totalSeats: Int,
    val remainingSeats: Int
)

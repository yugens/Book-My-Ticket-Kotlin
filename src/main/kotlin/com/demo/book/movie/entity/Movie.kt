package com.demo.book.movie.entity

data class Movie(
    val id: Int,
    val title: String,
    val durationInMinutes: Int,
    val theatreId: Int
)

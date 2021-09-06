package com.demo.book.movie.exception

import java.lang.RuntimeException

class InvalidMovieDataException(override val message: String?) : RuntimeException(message)

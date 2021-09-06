package com.demo.book.theatre.exception

import java.lang.RuntimeException

class InvalidTheatreDataException(override val message: String?) : RuntimeException(message)

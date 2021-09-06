package com.demo.book.show.exception

import java.lang.RuntimeException

class InvalidShowDataException(override val message: String?) : RuntimeException(message)

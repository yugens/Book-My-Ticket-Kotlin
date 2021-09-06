package com.demo.book.user.exception

import java.lang.RuntimeException

class InvalidUserDataException(override val message: String?) : RuntimeException(message)

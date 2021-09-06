package com.demo.book.ticket.exception

import java.lang.RuntimeException

class InvalidTicketDataException(override val message: String?) : RuntimeException(message)

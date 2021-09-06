package com.demo.book.theatre.exception

import com.demo.book.ApiErrorResponse
import io.micronaut.context.annotation.Requirements
import io.micronaut.context.annotation.Requires
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Produces
import io.micronaut.http.server.exceptions.ExceptionHandler
import javax.inject.Singleton

@Produces
@Singleton
@Requirements(
    Requires(classes = [InvalidTheatreDataException::class, ExceptionHandler::class])
)
class InvalidTheatreDataExceptionHandler :
    ExceptionHandler<InvalidTheatreDataException, HttpResponse<ApiErrorResponse>> {
    override fun handle(
        request: HttpRequest<*>?,
        exception: InvalidTheatreDataException
    ): HttpResponse<ApiErrorResponse> {
        return HttpResponse.badRequest(
            ApiErrorResponse(
                variant = "com.medly.bmt.api.error",
                header = "Wrong Theatre Details",
                message = exception.message.toString()
            )
        )
    }
}

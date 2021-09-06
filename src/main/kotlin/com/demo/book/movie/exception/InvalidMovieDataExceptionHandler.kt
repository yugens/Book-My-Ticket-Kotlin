package com.demo.book.movie.exception

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
    Requires(classes = [InvalidMovieDataException::class, ExceptionHandler::class])
)
class InvalidMovieDataExceptionHandler : ExceptionHandler<InvalidMovieDataException, HttpResponse<ApiErrorResponse>> {
    override fun handle(
        request: HttpRequest<*>?,
        exception: InvalidMovieDataException
    ): HttpResponse<ApiErrorResponse> {
        return HttpResponse.badRequest(
            ApiErrorResponse(
                variant = "com.medly.bmt.api.error",
                header = "Invalid Movie Data",
                message = exception.message.toString()
            )
        )
    }
}

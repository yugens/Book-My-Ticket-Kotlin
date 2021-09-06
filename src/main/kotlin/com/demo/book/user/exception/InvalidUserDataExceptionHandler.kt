package com.demo.book.user.exception

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
    Requires(classes = [InvalidUserDataException::class, ExceptionHandler::class])
)
class InvalidUserDataExceptionHandler : ExceptionHandler<InvalidUserDataException, HttpResponse<ApiErrorResponse>> {
    override fun handle(
        request: HttpRequest<*>?,
        exception: InvalidUserDataException
    ): HttpResponse<ApiErrorResponse> {
        return HttpResponse.badRequest(
            ApiErrorResponse(
                variant = "com.medly.bmt.api.error",
                header = "Invalid User Details",
                message = exception.message.toString()
            )
        )
    }
}

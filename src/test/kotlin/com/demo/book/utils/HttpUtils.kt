package com.demo.book.utils

import io.micronaut.http.HttpRequest
import io.micronaut.http.client.HttpClient

inline fun <reified T> HttpClient.get(url: String) = toBlocking().exchange(url, T::class.java)
inline fun <reified T> HttpClient.post(url: String, body: T) = toBlocking()
    .exchange(
        HttpRequest.POST(url, body),
        T::class.java
    )

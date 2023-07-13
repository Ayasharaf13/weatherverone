package com.example.weathermate.network

sealed class ResponseState<out T> {
    class Success<T>(val data:T): ResponseState<T>()
    class Failure(val msg:Throwable): ResponseState<Nothing>()
    object Loading: ResponseState<Nothing>()
}


/*
sealed class ResponseState<out T> {
    class Success<T>(val data:T): ResponseState<T>()
    class Failure(val msg:Throwable): ResponseState<Nothing>()
    object Loading: ResponseState<Nothing>()
}
 */

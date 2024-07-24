package com.varunkumar.safespace.utils

sealed class Result<T>(val data: T? = null, val msg: String? = null) {
    class Idle<T> : Result<T>()
    class Loading<T> : Result<T>(msg = "Loading")
    class Success<T>(data: T?) : Result<T>(data = data)
    class Error<T>(msg: String?) : Result<T>(msg = msg)
}
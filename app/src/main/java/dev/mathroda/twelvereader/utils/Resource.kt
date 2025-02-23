package dev.mathroda.twelvereader.utils

sealed class Resource<T>(val message: String = "") {
    data class Success<T>(val data: T) : Resource<T>()
    class Error<T>(message: String) : Resource<T>(message)
    class Loading<T> : Resource<T>()
}
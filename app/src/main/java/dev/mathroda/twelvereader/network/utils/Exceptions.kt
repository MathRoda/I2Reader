package dev.mathroda.twelvereader.network.utils

import io.ktor.client.call.body
import io.ktor.client.statement.HttpResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.io.IOException

enum class ApiServiceErrors {
    ServiceUnavailable,
    ClientError,
    ServerError,
    UnknownError
}

class ApiServiceException(error: ApiServiceErrors) : Exception(
    "Something went wrong: ${error.name}"
)

suspend inline fun <reified T> handleErrors(
    crossinline response: suspend () -> HttpResponse
): T = withContext(Dispatchers.IO) {

    val result = try {
        response()
    } catch(e: IOException) {
        throw ApiServiceException(ApiServiceErrors.ServiceUnavailable)
    }

    when(result.status.value) {
        in 200..299 -> Unit
        in 400..499 -> throw ApiServiceException(ApiServiceErrors.ClientError)
        500 -> throw ApiServiceException(ApiServiceErrors.ServerError)
        else -> throw ApiServiceException(ApiServiceErrors.UnknownError)
    }

    return@withContext try {
        result.body()
    } catch(e: Exception) {
        throw e.cause ?: ApiServiceException(ApiServiceErrors.UnknownError)
    }

}
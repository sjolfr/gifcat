package uk.co.gifcat.api

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.ResponseException
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.statement.HttpResponse
import io.ktor.http.isSuccess
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import uk.co.gifcat.api.errors.ApiErrors
import uk.co.gifcat.api.errors.BadRequest
import uk.co.gifcat.api.errors.NoInternetConnection
import uk.co.gifcat.api.errors.ServerError
import uk.co.gifcat.api.models.Breed
import uk.co.gifcat.api.models.Image
import co.touchlab.kermit.Logger as KLogger

object CatsApi {
    private const val BaseUrl = "https://api.thecatapi.com/v1/"
    private const val StartOfClientErrorResponses = 400
    private const val EndOfClientErrorResponses = 499
    private const val StartOfServerErrorResponses = 500
    private const val EndOfServerErrorResponses = 599
    private const val BreedIdLength = 4
    private const val ApiKeyHeaderKey = "x-api-key"
    private const val ApiKeyHeaderValue = "live_wfEkv0YW5zYpFoYEtZUu7e8apkDznpfsTI7u7jvdtPXoVznE2KJ8k0UpKgySzev"
    private const val loggerKey = "HTTP Client"

    private val client = HttpClient {
        install(ContentNegotiation) {
            json(
                Json {
                    prettyPrint = true
                    isLenient = true
                    ignoreUnknownKeys = true
                }
            )
        }
        install(Logging) {
            logger = object : Logger {
                override fun log(message: String) {
                    KLogger.d(loggerKey) {
                        message
                    }
                }
            }
            level = LogLevel.ALL
        }
    }

    suspend fun getBreeds(limit: Int = 10, page: Int = 0): List<Breed>? {
        require(limit > 0) {
            "limit must be greater than zero"
        }
        require(page >= 0) {
            "page cannot be negative"
        }

        return getRequest<List<Breed>>(
            url = BaseUrl + "breeds?limit=$limit&page=$page",
            action = "loading the cat breeds"
        )
    }

    suspend fun getBreedImages(breedId: String, limit: Int = 3, page: Int = 0): List<Image>? {
        require(breedId.length == BreedIdLength) {
            "the breed id must be four letters long"
        }

        return getRequest<List<Image>>(
            url = BaseUrl + "images/search?mime_types=png,jpg&page=$page&limit=$limit&breed_ids=$breedId",
            action = "loading the images for $breedId"
        )
    }

    private suspend inline fun <reified T> getRequest(url: String, action: String): T? {
        val response: HttpResponse? = try {
             client.get(url) {
                header(ApiKeyHeaderKey, ApiKeyHeaderValue)
            }
        } catch (responseException: ResponseException) {
            KLogger.e(loggerKey, responseException)
            null
        } catch (exception: Exception) {
            KLogger.e(loggerKey, exception)
            ApiErrors.errors.emit(NoInternetConnection())
            null
        }

        return response?.let {
            checkStatusCode(response.status.value, action)

            if (response.status.isSuccess()) {
                response.body()
            } else {
                null
            }
        }
    }

    private suspend fun checkStatusCode(statusCode: Int, action: String) {
        val clientErrorRange = StartOfClientErrorResponses..EndOfClientErrorResponses
        val serverErrorRange = StartOfServerErrorResponses..EndOfServerErrorResponses

        when (statusCode) {
            in clientErrorRange -> ApiErrors.errors.emit(
                BadRequest("Sorry, we had trouble $action")
            )
            in serverErrorRange -> ApiErrors.errors.emit(
                ServerError("Hey, currently are servers are experiencing some issues, " +
                    "but we will be back up soon")
            )
        }
    }
}

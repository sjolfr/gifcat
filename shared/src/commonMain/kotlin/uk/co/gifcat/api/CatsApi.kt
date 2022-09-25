package uk.co.gifcat.api

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import uk.co.gifcat.api.models.Breed

object CatsApi {
    private const val BaseUrl = "https://api.thecatapi.com/v1/"

    private const val StartOfClientErrorResponses = 400
    private const val EndOfClientErrorResponses = 499
    private const val StartOfServerErrorResponses = 500
    private const val EndOfServerErrorResponses = 599

    private val client = HttpClient() {
        install(ContentNegotiation) {
            json(
                Json {
                    prettyPrint = true
                    isLenient = true
                }
            )
        }
    }

    suspend fun getBreeds(limit: Int, page: Int): List<Breed> {
        val response: HttpResponse = client.get(
            BaseUrl + "breeds?limit=$limit&page=$page"
        )
        checkStatusCode(response.status.value)
        return response.body()
    }

    private suspend fun checkStatusCode(statusCode: Int) {
        val clientErrorRange = StartOfClientErrorResponses..EndOfClientErrorResponses
        val serverErrorRange = StartOfServerErrorResponses..EndOfServerErrorResponses

        when (statusCode) {
            in clientErrorRange -> ApiErrors.errors.emit(
                "We had trouble loading the cat breeds"
            )
            in serverErrorRange -> ApiErrors.errors.emit(
                "Hey, currently are servers are experiencing some issues, " +
                    "but we will be back up soon"
            )
        }
    }
}

package uk.co.gifcat.api

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import uk.co.gifcat.api.models.Breed
import uk.co.gifcat.api.models.Image

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

    suspend fun getBreeds(limit: Int = 10, page: Int = 0): List<Breed> {
        require(limit > 0) {
            "limit must be greater than zero"
        }
        require(page >= 0) {
            "page cannot be negative"
        }

        val response: HttpResponse = client.get(
            BaseUrl + "breeds?limit=$limit&page=$page"
        )
        checkStatusCode(response.status.value, "loading the cat breeds")
        return response.body()
    }

    suspend fun getBreedImages(breedId: String, limit: Int = 3, page: Int = 0): List<Image> {
        require(breedId.length == 4) {
            "the breed id must be four letters long"
        }
        val response: HttpResponse = client.get(
            BaseUrl + "images/search?mime_types=png,jpg&page=$page&limit=$limit&breed_ids=$breedId"
        )
        checkStatusCode(response.status.value, "loading the images for $breedId")
        return response.body()
    }

    private suspend fun checkStatusCode(statusCode: Int, action: String) {
        val clientErrorRange = StartOfClientErrorResponses..EndOfClientErrorResponses
        val serverErrorRange = StartOfServerErrorResponses..EndOfServerErrorResponses

        when (statusCode) {
            in clientErrorRange -> ApiErrors.errors.emit(
                "Sorry, we had trouble $action"
            )
            in serverErrorRange -> ApiErrors.errors.emit(
                "Hey, currently are servers are experiencing some issues, " +
                        "but we will be back up soon"
            )
        }
    }
}

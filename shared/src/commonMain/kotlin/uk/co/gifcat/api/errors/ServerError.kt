package uk.co.gifcat.api.errors

class ServerError : ApiError {
    override val message: String

    internal constructor(message: String) {
        this.message = message
    }
}

package uk.co.gifcat.api.errors

class BadRequest : ApiError {
    override val message: String

    internal constructor(message: String) {
        this.message = message
    }
}

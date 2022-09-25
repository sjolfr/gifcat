package uk.co.gifcat.api.models

import kotlinx.serialization.Serializable

@Serializable
data class Image(
    val id: String,
    val width: Long,
    val height: Long,
    val url: String
)

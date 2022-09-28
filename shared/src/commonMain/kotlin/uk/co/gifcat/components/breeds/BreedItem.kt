package uk.co.gifcat.components.breeds

data class BreedItem(
    val name: String,
    val origin: String,
    val temperament: String,
    val imageUrl: String?,
    internal val id: String,
    val attributes: Map<String, Long>,
)

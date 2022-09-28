package uk.co.gifcat.components.breeds

data class BreedsModel(
    internal val limit: Int,
    internal val page: Int,
    val isLoading: Boolean,
    val breeds: List<BreedItem>
) {
    constructor() : this(DefaultLimit, DefaultPage, false, listOf())

    private companion object {
        const val DefaultLimit = 10
        const val DefaultPage = 0
    }
}

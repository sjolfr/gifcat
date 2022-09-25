package uk.co.gifcat.components.breeds

import com.arkivanov.decompose.value.Value

interface BreedList {
    val model: Value<BreedsModel>

    suspend fun onBreedSelected(breed: BreedItem)
    suspend fun loadMore()
}

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

data class BreedItem(
    val name: String,
    val origin: String,
    val imageUrl: String,
    val temperament: String,
    internal val id: String
)

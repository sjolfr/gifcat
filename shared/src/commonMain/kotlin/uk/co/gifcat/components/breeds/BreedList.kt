package uk.co.gifcat.components.breeds

import com.arkivanov.decompose.value.Value

interface BreedList {
    val model: Value<BreedsModel>

    suspend fun onBreedSelected(breed: BreedItem)
    suspend fun load()
}

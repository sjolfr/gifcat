package uk.co.gifcat.components.breeds

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.decompose.value.reduce
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import uk.co.gifcat.api.CatsApi
import uk.co.gifcat.api.models.Breed
import uk.co.gifcat.extensions.coroutineScope

internal class BreedListComponent(
    componentContext: ComponentContext,
    private val onBreedItemSelected: (BreedItem) -> Unit
) : BreedList, ComponentContext by componentContext {
    private val _value = MutableValue(BreedsModel())
    override val model: Value<BreedsModel> = _value

    init {
        componentContext.coroutineScope(Dispatchers.Default + SupervisorJob())
            .launch {
                loadCats()
            }
    }

    private suspend fun loadCats() {
        _value.reduce {
            it.copy(isLoading = true)
        }

        val page = model.value.page
        val limit = model.value.limit

        val response = CatsApi.getBreeds(limit, page)

        response?.let {
            updateBreeds(it)
        }

        _value.reduce {
            it.copy(isLoading = false)
        }
    }

    private fun updateBreeds(apiBreeds: List<Breed>) {
        val mappedBreeds = model.value.breeds + apiBreeds.map {
            BreedItem(it.name, it.origin, it.temperament, it.image?.url, it.id, mappedAttributes(it))
        }

        if (mappedBreeds.isNotEmpty()) {
            _value.reduce {
                it.copy(
                    page = it.page + 1,
                    breeds = mappedBreeds
                )
            }
        }
    }

    private fun mappedAttributes(it: Breed): Map<String, Long> {
        return mapOf(
            Pair("Adaptability", it.adaptability),
            Pair("Affection Level", it.affectionLevel),
            Pair("Child Friendly", it.childFriendly),
            Pair("Dog Friendly", it.dogFriendly),
            Pair("Energy Level", it.energyLevel),
            Pair("Grooming", it.grooming),
            Pair("Health Issues", it.healthIssues),
            Pair("Intelligence", it.intelligence),
            Pair("Shedding Level", it.sheddingLevel),
            Pair("Social Needs", it.socialNeeds),
            Pair("Stranger Friendly", it.strangerFriendly),
            Pair("Vocalisation", it.vocalisation),
        )
    }

    override suspend fun onBreedSelected(breed: BreedItem) = onBreedItemSelected(breed)

    override suspend fun load() {
        loadCats()
    }
}

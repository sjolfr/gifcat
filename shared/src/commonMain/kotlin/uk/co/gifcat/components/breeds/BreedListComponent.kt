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
    private val onBreeItemSelected: (BreedItem) -> Unit
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
            BreedItem(it.name, it.origin, it.temperament, it.image?.url, it.id)
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

    override suspend fun onBreedSelected(breed: BreedItem) = onBreeItemSelected(breed)

    override suspend fun loadMore() {
        loadCats()
    }
}

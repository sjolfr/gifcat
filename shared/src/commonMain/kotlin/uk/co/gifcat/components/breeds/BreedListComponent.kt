package uk.co.gifcat.components.breeds

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.decompose.value.reduce
import com.arkivanov.essenty.lifecycle.doOnCreate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import uk.co.gifcat.api.CatsApi
import uk.co.gifcat.extensions.coroutineScope

internal class BreedListComponent(
    componentContext: ComponentContext,
    private val onBreeItemSelected: (BreedItem) -> Unit
) : BreedList, ComponentContext by componentContext {
    private val _value = MutableValue(BreedsModel())
    override val model: Value<BreedsModel> = _value

    init {
        lifecycle.doOnCreate {
            componentContext.coroutineScope(Dispatchers.Default + SupervisorJob())
                .launch {
                    loadCats()
                }
        }
    }

    private suspend fun loadCats() {
        _value.reduce {
            it.copy(isLoading = true)
        }

        val page = model.value.page
        val limit = model.value.limit
        val apiBreeds = CatsApi.getBreeds(limit, page)

        val mappedBreeds = apiBreeds.map {
            BreedItem(it.name, it.origin, it.image.url, it.temperament, it.id)
        }

        if (mappedBreeds.isNotEmpty()) {
            _value.reduce {
                it.copy(
                    page = it.page + 1,
                    breeds = mappedBreeds
                )
            }
        }

        _value.reduce {
            it.copy(isLoading = false)
        }
    }

    override suspend fun onBreedSelected(breed: BreedItem) = onBreeItemSelected(breed)

    override suspend fun loadMore() = loadCats()
}

package uk.co.gifcat.components.imageGallery

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.decompose.value.reduce
import com.arkivanov.essenty.backhandler.BackCallback
import com.arkivanov.essenty.lifecycle.doOnCreate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import uk.co.gifcat.api.CatsApi
import uk.co.gifcat.api.models.Image
import uk.co.gifcat.extensions.coroutineScope

internal class ImageGalleryComponent(
    private val breedId: String,
    breedName: String,
    componentContext: ComponentContext,
    private val onFinished: () -> Unit
) : ImageGallery, ComponentContext by componentContext {
    private val _value = MutableValue(ImageGalleryModel(breedId = breedId, breedName = breedName))
    override val model: Value<ImageGalleryModel> = _value

    private val backCallback = BackCallback(isEnabled = true, onBack = onFinished)

    override fun onBackPressed() = onFinished()

    init {
        backHandler.register(backCallback)

        lifecycle.doOnCreate {
            componentContext.coroutineScope(Dispatchers.Default + SupervisorJob())
                .launch {
                    loadCat()
                }
        }
    }

    private suspend fun loadCat() {
        _value.reduce {
            it.copy(isLoading = true)
        }

        val page = model.value.page
        val limit = model.value.limit
        val breedImages: List<Image> = CatsApi.getBreedImages(breedId, page = page, limit = limit)
        val mappedImages = breedImages.map {
            ImageModel(imageUrl = it.url, height = it.height, width = it.width)
        }

        if (breedImages.isNotEmpty()) {
            _value.reduce {
                it.copy(
                    images = mappedImages
                )
            }
        }

        _value.reduce {
            it.copy(isLoading = false)
        }
    }
}

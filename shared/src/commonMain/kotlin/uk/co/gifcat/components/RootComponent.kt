package uk.co.gifcat.components

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.push
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.decompose.value.reduce
import com.arkivanov.essenty.parcelable.Parcelable
import com.arkivanov.essenty.parcelable.Parcelize
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import uk.co.gifcat.api.errors.ApiErrors
import uk.co.gifcat.api.errors.BadRequest
import uk.co.gifcat.api.errors.NoInternetConnection
import uk.co.gifcat.api.errors.ServerError
import uk.co.gifcat.components.Root.Child
import uk.co.gifcat.components.breeds.BreedItem
import uk.co.gifcat.components.breeds.BreedListComponent
import uk.co.gifcat.components.imageGallery.ImageGalleryComponent
import uk.co.gifcat.extensions.coroutineScope

class RootComponent(
    componentContext: ComponentContext,
) : Root, ComponentContext by componentContext {
    private val navigation = StackNavigation<Config>()
    private val stack =
        childStack(
            source = navigation,
            initialStack = { listOf(Config.BreedList) },
            childFactory = ::child,
        )

    private val _apiError = MutableValue("")
    override val apiError: Value<String> = _apiError

    override val childStack: Value<ChildStack<*, Child>> = stack

    init {
        componentContext.coroutineScope(Dispatchers.Default + SupervisorJob())
            .launch {
                ApiErrors.errors.collect { error ->
                    when (error) {
                        is BadRequest -> {}
                        is NoInternetConnection, is ServerError -> {
                            _apiError.reduce {
                                error.message
                            }
                        }
                    }
                }
            }
    }

    private fun child(config: Config, componentContext: ComponentContext): Child =
        when (config) {
            Config.BreedList -> Child.BreedListChild(BreedListComponent(componentContext, ::onBreedItemSelected))
            is Config.ImageGallery -> Child.ImageGalleryChild(
                ImageGalleryComponent(config.breedId, config.breedName, componentContext, ::onGalleryFinished)
            )
        }

    private fun onGalleryFinished() = navigation.pop()

    private fun onBreedItemSelected(breedItem: BreedItem) =
        navigation.push(Config.ImageGallery(breedName = breedItem.name, breedId = breedItem.id))

    private sealed interface Config : Parcelable {
        @Parcelize
        object BreedList : Config

        @Parcelize
        data class ImageGallery(val breedId: String, val breedName: String) : Config
    }
}

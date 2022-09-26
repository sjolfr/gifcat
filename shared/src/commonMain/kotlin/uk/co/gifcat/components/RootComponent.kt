package uk.co.gifcat.components

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.push
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.parcelable.Parcelable
import com.arkivanov.essenty.parcelable.Parcelize
import uk.co.gifcat.components.Root.Child
import uk.co.gifcat.components.breeds.BreedItem
import uk.co.gifcat.components.breeds.BreedListComponent
import uk.co.gifcat.components.imageGallery.ImageGalleryComponent

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

    override val childStack: Value<ChildStack<*, Child>> = stack

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

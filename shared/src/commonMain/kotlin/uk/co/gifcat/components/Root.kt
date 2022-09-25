package uk.co.gifcat.components

import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.value.Value
import uk.co.gifcat.components.imageGallery.ImageGallery
import uk.co.gifcat.components.breeds.BreedList

interface Root {
    val childStack: Value<ChildStack<*, Child>>

    sealed class Child {
        class BreedListChild(val component: BreedList) : Child()
        class ImageGalleryChild(val component: ImageGallery) : Child()
    }
}

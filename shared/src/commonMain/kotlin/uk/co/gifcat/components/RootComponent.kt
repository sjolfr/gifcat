package uk.co.gifcat.components

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.parcelable.Parcelable
import com.arkivanov.essenty.parcelable.Parcelize
import uk.co.gifcat.components.Root.Child
import uk.co.gifcat.components.breeds.BreedListComponent

class RootComponent(
    componentContext: ComponentContext,
) : Root, ComponentContext by componentContext {

    private val navigation = StackNavigation<Config>()
    private val stack =
        childStack(
            source = navigation,
            initialStack = { listOf(Config.CatList) },
            childFactory = ::child,
        )

    override val childStack: Value<ChildStack<*, Child>> = stack

    private fun child(config: Config, componentContext: ComponentContext): Child =
        when (config) {
            Config.CatList -> Child.CatListChild(BreedListComponent(componentContext))
        }

    private sealed interface Config : Parcelable {
        @Parcelize
        object CatList : Config
    }
}

package uk.co.gifcat.android.compose

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.extensions.compose.jetpack.stack.Children
import com.arkivanov.decompose.extensions.compose.jetpack.subscribeAsState
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import uk.co.gifcat.components.Root

@OptIn(ExperimentalDecomposeApi::class)
@Composable
fun RootContent(root: Root) {
    val childStack by root.childStack.subscribeAsState()

    Children(
        stack = childStack
    ) {
        when (val child = it.instance) {
            is Root.Child.BreedListChild -> BreedsContent(component = child.component, modifier = Modifier.fillMaxSize())
            is Root.Child.ImageGalleryChild -> ImageGallery(component = child.component, modifier = Modifier.fillMaxSize())
        }
    }
}

@Preview
@Composable
fun RootContentPreview() {
    val fakeRootComponent = object : Root {
        override val childStack: Value<ChildStack<*, Root.Child>> =
            MutableValue(
                ChildStack(
                    configuration = Unit,
                    instance = Root.Child.BreedListChild(fakeBreedsComponent),
                )
            )
    }
    RootContent(fakeRootComponent)
}

package uk.co.gifcat.android.compose

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import com.arkivanov.decompose.extensions.compose.jetpack.stack.Children
import com.arkivanov.decompose.extensions.compose.jetpack.subscribeAsState
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import kotlinx.coroutines.launch
import uk.co.gifcat.android.R
import uk.co.gifcat.components.Root

@Composable
fun RootContent(root: Root) {
    val childStack by root.childStack.subscribeAsState()
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    root.apiError.subscribe {
        if (it.isNotEmpty()) {
            scope.launch {
                snackbarHostState.showSnackbar(message = it)
            }
        }
    }

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            CenterAlignedTopAppBar(
                scrollBehavior = scrollBehavior,
                title = {
                    Text(
                        stringResource(R.string.app_name),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Children(
            stack = childStack,
            modifier = Modifier
                .padding(innerPadding)
        ) {
            when (val child = it.instance) {
                is Root.Child.BreedListChild -> {
                    BreedsContent(child.component, modifier = Modifier.fillMaxSize())
                }
                is Root.Child.ImageGalleryChild -> {
                    ImageGallery(child.component, modifier = Modifier.fillMaxSize())
                }
            }
        }
    }
}

@Preview
@Composable
fun RootContentPreview() {
    val fakeRootComponent = object : Root {
        override val apiError: Value<String> = MutableValue("")
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

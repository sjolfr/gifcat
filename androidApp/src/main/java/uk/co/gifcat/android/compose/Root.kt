package uk.co.gifcat.android.compose

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.extensions.compose.jetpack.stack.Children
import com.arkivanov.decompose.extensions.compose.jetpack.subscribeAsState
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import uk.co.gifcat.android.R
import uk.co.gifcat.components.Root

@OptIn(ExperimentalDecomposeApi::class, ExperimentalMaterial3Api::class)
@Composable
fun RootContent(root: Root, modifier: Modifier = Modifier) {
    val childStack by root.childStack.subscribeAsState()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())

    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
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
    ) { innerPadding ->
        Children(
            stack = childStack,
            modifier = Modifier
                .padding(innerPadding)
        ) {
            when (val child = it.instance) {
                is Root.Child.CatListChild -> BreedsContent(component = child.component, modifier = Modifier.fillMaxSize())
            }
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
                    instance = Root.Child.CatListChild(fakeBreedsComponent),
                )
            )
    }
    RootContent(fakeRootComponent)
}

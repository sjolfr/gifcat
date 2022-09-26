package uk.co.gifcat.android.compose

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.imageLoader
import com.arkivanov.decompose.extensions.compose.jetpack.subscribeAsState
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.reduce
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.coil.CoilImage
import com.skydoves.landscapist.components.rememberImageComponent
import com.skydoves.landscapist.placeholder.shimmer.ShimmerPlugin
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import uk.co.gifcat.android.R
import uk.co.gifcat.android.compose.views.LoadingBox
import uk.co.gifcat.components.breeds.BreedItem
import uk.co.gifcat.components.breeds.BreedList
import uk.co.gifcat.components.breeds.BreedsModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BreedsContent(component: BreedList, modifier: Modifier) {
    val model by component.model.subscribeAsState()
    val coroutineScope = rememberCoroutineScope()
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
        Column(modifier = modifier
            .padding(innerPadding)
            .verticalScroll(rememberScrollState())
        ) {
            model.breeds.forEach {
                BreedRow(it) {
                    coroutineScope.launch {
                        component.onBreedSelected(it)
                    }
                }
            }
            if (model.isLoading) {
                LoadingBox()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BreedRow(breed: BreedItem, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier.padding(16.dp),
    ) {
        val textModifier = Modifier.padding(horizontal = 32.dp)

        Text(text = breed.name,
            style = MaterialTheme.typography.headlineSmall,
            modifier = textModifier
        )
        Text(
            breed.origin,
            style = MaterialTheme.typography.titleSmall,
            modifier = textModifier
        )
        CoilImage(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp),
            imageModel = breed.imageUrl,
            imageLoader = { LocalContext.current.imageLoader },
            imageOptions = ImageOptions(
                contentScale = ContentScale.Crop,
                alignment = Alignment.Center
            ),
            component = rememberImageComponent {
                +ShimmerPlugin(
                    baseColor = Color.DarkGray,
                    highlightColor = Color.LightGray,
                )
            },
        )
        Text(
            breed.temperament,
            maxLines = 2,
            modifier = textModifier
        )
    }
}

private val americanBobTail = BreedItem(
    "American Bobtail",
    "United States",
    "https://cdn2.thecatapi.com/images/hBXicehMA.jpg",
    "Intelligent, Interactive, Lively, Playful, Sensitive",
    "abob"
)

val fakeBreedsComponent = object : BreedList {
    override val model = MutableValue(BreedsModel().copy(
        breeds = lotsOfAmericanBobtails()
    ))

    override suspend fun onBreedSelected(breed: BreedItem) {
        println(breed)
    }

    override suspend fun loadMore() {
        model.reduce {
            it.copy(isLoading = true)
        }
        delay(1000)
        model.reduce {
            val breeds = it.breeds.toMutableList()
            breeds.addAll(lotsOfAmericanBobtails())
            it.copy(breeds = breeds)
        }
    }

    private fun lotsOfAmericanBobtails(): List<BreedItem> {
        return listOf(
            americanBobTail,
            americanBobTail,
            americanBobTail,
            americanBobTail,
            americanBobTail,
            americanBobTail,
            americanBobTail,
            americanBobTail,
            americanBobTail,
            americanBobTail,
        )
    }
}

@Preview
@Composable
fun BreedsContentPreview() {
    BreedsContent(fakeBreedsComponent, modifier = Modifier.fillMaxSize())
}

@Preview
@Composable
fun BreedRowPreview() {
    BreedRow(americanBobTail) {
        println("breed row clicked")
    }
}

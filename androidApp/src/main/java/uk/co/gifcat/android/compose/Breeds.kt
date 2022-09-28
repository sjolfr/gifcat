package uk.co.gifcat.android.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.imageLoader
import com.arkivanov.decompose.extensions.compose.jetpack.subscribeAsState
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.reduce
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.coil.CoilImage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import uk.co.gifcat.android.R
import uk.co.gifcat.android.compose.views.InfiniteList
import uk.co.gifcat.components.breeds.BreedItem
import uk.co.gifcat.components.breeds.BreedList
import uk.co.gifcat.components.breeds.BreedsModel

@Composable
fun BreedsContent(component: BreedList, modifier: Modifier) {
    val model by component.model.subscribeAsState()
    val composableScope = rememberCoroutineScope()

    Column(modifier) {
        if (model.isLoading) {
            LinearProgressIndicator(color = MaterialTheme.colorScheme.secondary, modifier = Modifier.fillMaxWidth())
        }

        InfiniteList(items = model.breeds,
            onLoadMore = {
                composableScope.launch {
                    withContext(Dispatchers.Main) {
                        component.load()
                    }
                }
            }) { breedItem ->
            BreedRow(breedItem) {
                composableScope.launch {
                    withContext(Dispatchers.Main) {
                        component.onBreedSelected(breedItem)
                    }
                }
            }
        }
    }
}

@Composable
fun BreedRow(breed: BreedItem, onClick: () -> Unit) {
    val isExpandedState = remember {
        mutableStateOf(false)
    }
    val isExpanded by isExpandedState

    ElevatedCard(
        onClick = onClick,
        modifier = Modifier.padding(16.dp)
    ) {
        val textModifier = Modifier.padding(horizontal = 32.dp)

        Text(text = breed.name,
            style = MaterialTheme.typography.headlineMedium,
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
        )
        Row(modifier = Modifier.fillMaxWidth()) {
            Text(
                breed.temperament,
                maxLines = 2,
                modifier = textModifier
                    .weight(1f)
            )
            Button(onClick = {
                isExpandedState.value = !isExpanded
            }) {
                if (isExpanded) {
                    Icon(imageVector = Icons.Default.ExpandLess, stringResource(R.string.expand_less))
                } else {
                    Icon(imageVector = Icons.Default.ExpandMore, stringResource(R.string.expand_more))
                }
            }
        }
        if (isExpanded) {
            Attributes(breed.attributes)
        }
    }
}

@Composable
fun Attributes(attributes: Map<String, Long>) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier
            .padding(8.dp)) {
        attributes.forEach {
            Box(modifier = Modifier
                .fillMaxWidth()
            ) {
                val widthMultiplier = (it.value.toFloat() / 5)
                Box(modifier = Modifier.fillMaxWidth(widthMultiplier)
                    .height(24.dp)
                    .background(MaterialTheme.colorScheme.tertiaryContainer,
                        shape = CutCornerShape(0, 100, 0, 0))
                )
                Text(it.key, color = MaterialTheme.colorScheme.onTertiaryContainer, modifier = Modifier.padding(8.dp, 0.dp, 0.dp, 0.dp))
            }
        }
    }
}

private val americanBobTail = BreedItem(
    "American Bobtail",
    "United States",
    "https://cdn2.thecatapi.com/images/hBXicehMA.jpg",
    "Intelligent, Interactive, Lively, Playful, Sensitive",
    "abob",
    mapOf()
)

val fakeBreedsComponent = object : BreedList {
    override val model = MutableValue(BreedsModel().copy(
        breeds = lotsOfAmericanBobtails()
    ))

    override suspend fun onBreedSelected(breed: BreedItem) {
        println(breed)
    }

    override suspend fun load() {
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

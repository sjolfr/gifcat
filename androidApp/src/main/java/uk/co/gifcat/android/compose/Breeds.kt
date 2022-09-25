package uk.co.gifcat.android.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.extensions.compose.jetpack.subscribeAsState
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.reduce
import kotlinx.coroutines.delay
import uk.co.gifcat.components.breeds.BreedItem
import uk.co.gifcat.components.breeds.BreedList
import uk.co.gifcat.components.breeds.BreedsModel

@Composable
fun BreedsContent(component: BreedList, modifier: Modifier) {
    val model by component.model.subscribeAsState()

    Column(modifier = modifier
        .padding(8.dp)
        .verticalScroll(rememberScrollState())
    ) {
        model.breeds.forEach {
            BreedRow(it) { }
        }
        if (model.isLoading) {
            Box(modifier = Modifier
                .fillMaxWidth()) {
                CircularProgressIndicator(modifier = Modifier
                    .height(24.dp)
                    .width(24.dp)
                    .align(Alignment.Center))
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
        // image place holder, later to be replaced with an image
        Box(modifier = Modifier
            .background(MaterialTheme.colorScheme.secondary)
            .fillMaxWidth()
            .height(120.dp)
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
    "Intelligent, Interactive, Lively, Playful, Sensitive"
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

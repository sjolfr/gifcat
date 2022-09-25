package uk.co.gifcat.android.compose

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.extensions.compose.jetpack.subscribeAsState
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.coil.CoilImage
import com.skydoves.landscapist.components.rememberImageComponent
import com.skydoves.landscapist.placeholder.shimmer.ShimmerPlugin
import uk.co.gifcat.components.imageGallery.ImageGallery
import uk.co.gifcat.components.imageGallery.ImageGalleryModel
import uk.co.gifcat.components.imageGallery.ImageModel

@OptIn(ExperimentalPagerApi::class)
@Composable
fun ImageGallery(component: ImageGallery, modifier: Modifier) {
    val model by component.model.subscribeAsState()

    Column(modifier = modifier
        .padding(8.dp)
        .verticalScroll(rememberScrollState())
    ) {
        HorizontalPager(model.images.count()) { page ->
            Column {
                CoilImage(
                    imageModel = model.images[page].imageUrl,
                    imageOptions = ImageOptions(contentScale = ContentScale.Crop),
                    component = rememberImageComponent {
                        +ShimmerPlugin(
                            baseColor = Color.Blue,
                            highlightColor = Color.Cyan
                        )
                    },
                )
                Text(
                    text = "Page: $page",
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}


@Preview
@Composable
fun ImageGalleryPreview() {
    ImageGallery(fakeImageComponent, modifier = Modifier)
}

val fakeImageComponent = object : ImageGallery {
    override val model: Value<ImageGalleryModel> = MutableValue(
        ImageGalleryModel("abys", false, "abys", listOf(
            ImageModel("https://cdn2.thecatapi.com/images/AH8T_hDMq.jpg", width = 299, height = 168),
            ImageModel("https://cdn2.thecatapi.com/images/rRLX_RH_o.jpg", width = 299, height = 168),
            ImageModel("https://cdn2.thecatapi.com/images/p6x60nX6U.jpg", width = 299, height = 168),
        ),
        0,
        3)
    )

    override fun onBackPressed() { }
}

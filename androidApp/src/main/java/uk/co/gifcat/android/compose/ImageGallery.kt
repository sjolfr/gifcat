package uk.co.gifcat.android.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.palette.graphics.Palette
import androidx.palette.graphics.Palette.Swatch
import coil.imageLoader
import com.arkivanov.decompose.extensions.compose.jetpack.subscribeAsState
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.coil.CoilImage
import com.skydoves.landscapist.components.rememberImageComponent
import com.skydoves.landscapist.palette.PalettePlugin
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import uk.co.gifcat.components.imageGallery.ImageGallery
import uk.co.gifcat.components.imageGallery.ImageGalleryModel
import uk.co.gifcat.components.imageGallery.ImageModel

@OptIn(ExperimentalPagerApi::class)
@Composable
fun ImageGallery(component: ImageGallery, modifier: Modifier) {
    val model by component.model.subscribeAsState()

    val pagerState = rememberPagerState()
    HorizontalPager(
        modifier = modifier,
        count = model.images.count(),
        state = pagerState,
        verticalAlignment = Alignment.CenterVertically,
    ) { page ->
        GalleryImage(model.images[page].imageUrl)
    }
}

@Composable
fun GalleryImage(imageUrl: String) {
    val paletteSharedFlow = MutableSharedFlow<Palette?>()
    fun Swatch.toColor() : Color = Color(this.rgb)
    val palette: Palette? by paletteSharedFlow.collectAsState(null)
    val composableScope = rememberCoroutineScope()

    Box(modifier = Modifier
        .background(palette?.dominantSwatch?.toColor() ?: MaterialTheme.colorScheme.surface)) {
        CoilImage(
            imageModel = imageUrl,
            imageLoader = { LocalContext.current.imageLoader },
            imageOptions = ImageOptions(contentScale = ContentScale.FillWidth),
            component = rememberImageComponent {
                +PalettePlugin {
                    composableScope.launch {
                       paletteSharedFlow.emit(it)
                    }
                }
            },
        )
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

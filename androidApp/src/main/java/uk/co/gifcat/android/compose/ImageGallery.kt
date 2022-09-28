package uk.co.gifcat.android.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.ScaleFactor
import androidx.compose.ui.layout.lerp
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.palette.graphics.Palette
import androidx.palette.graphics.Palette.Swatch
import coil.imageLoader
import com.arkivanov.decompose.extensions.compose.jetpack.subscribeAsState
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.calculateCurrentOffsetForPage
import com.google.accompanist.pager.rememberPagerState
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.coil.CoilImage
import com.skydoves.landscapist.components.rememberImageComponent
import com.skydoves.landscapist.palette.PalettePlugin
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import uk.co.gifcat.android.R
import uk.co.gifcat.components.imageGallery.ImageGallery
import uk.co.gifcat.components.imageGallery.ImageGalleryModel
import uk.co.gifcat.components.imageGallery.ImageModel
import kotlin.math.absoluteValue

@Composable
fun ImageGallery(component: ImageGallery, modifier: Modifier) {
    val model by component.model.subscribeAsState()

    val pagerState = rememberPagerState()
    Box(modifier = modifier
        .background(MaterialTheme.colorScheme.background)) {
        if (model.isLoading) {
            Column(
                modifier = Modifier.align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(32.dp)) {
                Text(
                    text = stringResource(R.string.image_gallery_loading_text, model.breedName),
                    style = MaterialTheme.typography.bodyLarge,
                )
                LinearProgressIndicator(color = MaterialTheme.colorScheme.secondary)
            }
        } else {
            HorizontalPager(
                count = model.images.count(),
                state = pagerState,
                verticalAlignment = Alignment.CenterVertically,
            ) { page ->
                GalleryImage(model.images[page].imageUrl, modifier = Modifier
                    .graphicsLayer {
                        val pageOffset = calculateCurrentOffsetForPage(page).absoluteValue

                        lerp(
                            start = ScaleFactor(0.85f, 0.85f),
                            stop = ScaleFactor(1f, 1f),
                            fraction = 1f - pageOffset.coerceIn(0f, 1f)
                        ).also { scale ->
                            scaleX = scale.scaleX
                            scaleY = scale.scaleY
                        }
                    })
            }
        }
    }
}

@Composable
fun GalleryImage(imageUrl: String, modifier: Modifier) {
    val paletteSharedFlow = MutableSharedFlow<Palette?>()
    val palette: Palette? by paletteSharedFlow.collectAsState(null)
    val composableScope = rememberCoroutineScope()

    Box(modifier = modifier
        .background(galleryBackground(palette))) {
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

@Composable
fun galleryBackground(palette: Palette?): Color {
    fun Swatch.toColor() : Color = Color(this.rgb)

    val vibrantSwatch: Color? = if (isSystemInDarkTheme()) {
        palette?.darkVibrantSwatch?.toColor()
    } else {
        palette?.lightVibrantSwatch?.toColor()
    }

    return if (vibrantSwatch == null) {
        val mutedSwatch: Color? = if (isSystemInDarkTheme()) {
            palette?.darkMutedSwatch?.toColor()
        } else {
            palette?.lightMutedSwatch?.toColor()
        }
        mutedSwatch ?: MaterialTheme.colorScheme.background
    } else {
        vibrantSwatch
    }
}

@Preview
@Composable
fun ImageGalleryPreview() {
    ImageGallery(fakeImageComponent, modifier = Modifier.fillMaxSize())
}

val fakeImageComponent = object : ImageGallery {
    override val model: Value<ImageGalleryModel> = MutableValue(
        ImageGalleryModel("abys", true, "abys", listOf(
            ImageModel("https://cdn2.thecatapi.com/images/AH8T_hDMq.jpg", width = 299, height = 168),
            ImageModel("https://cdn2.thecatapi.com/images/rRLX_RH_o.jpg", width = 299, height = 168),
            ImageModel("https://cdn2.thecatapi.com/images/p6x60nX6U.jpg", width = 299, height = 168),
        ),
        0,
        3)
    )

    override fun onBackPressed() { }
}

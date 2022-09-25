package uk.co.gifcat.android.compose.views

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun LoadingBox() {
    Box(modifier = Modifier
        .fillMaxWidth()) {
        CircularProgressIndicator(modifier = Modifier
            .height(24.dp)
            .width(24.dp)
            .align(Alignment.Center))
    }
}

@Preview
@Composable
fun LoadingBoxPreview() {
    LoadingBox()
}

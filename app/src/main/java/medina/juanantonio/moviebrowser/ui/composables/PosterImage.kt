package medina.juanantonio.moviebrowser.ui.composables

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.BrushPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import coil.compose.AsyncImage
import coil.request.ImageRequest
import medina.juanantonio.moviebrowser.R
import medina.juanantonio.moviebrowser.ui.theme.MovieBrowserTheme

@Composable
fun PosterImage(
    modifier: Modifier = Modifier,
    imageUrl: String,
    contentDescription: String?
) {
    AsyncImage(
        modifier = modifier,
        contentScale = ContentScale.FillBounds,
        model = ImageRequest.Builder(LocalContext.current)
            .data(imageUrl)
            .crossfade(true)
            .build(),
        error = painterResource(R.drawable.ic_sad_face),
        contentDescription = contentDescription,
        placeholder = BrushPainter(
            Brush.linearGradient(
                listOf(
                    Color(color = 0xFFFFFFFF),
                    Color(color = 0xFFDDDDDD),
                )
            )
        )
    )
}

@Preview
@Composable
fun PosterImagePreview() {
    MovieBrowserTheme {
        PosterImage(imageUrl = "", contentDescription = "")
    }
}

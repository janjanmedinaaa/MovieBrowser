package medina.juanantonio.moviebrowser.ui.screens.details_screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.BrushPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import medina.juanantonio.moviebrowser.R
import medina.juanantonio.moviebrowser.data.models.Movie
import medina.juanantonio.moviebrowser.ui.composables.PosterImage
import medina.juanantonio.moviebrowser.ui.theme.MovieBrowserTheme

@Composable
fun DetailsScreen(
    navController: NavController,
    viewModel: DetailsScreenViewModel = hiltViewModel()
) {
    val movie by remember { viewModel.movie }

    DetailsScreenLayout(
        movie = movie,
        onBackButtonClicked = {
            viewModel.onBackButtonClicked {
                navController.popBackStack()
            }
        },
        onFavoriteButtonClicked = viewModel::onFavoriteButtonClicked
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailsScreenLayout(
    movie: Movie?,
    onBackButtonClicked: () -> Unit = {},
    onFavoriteButtonClicked: (Movie) -> Unit = {}
) {
    val navigationPaddingValues = WindowInsets.navigationBars.asPaddingValues()

    Box(modifier = Modifier.padding(navigationPaddingValues)) {
        Column(modifier = Modifier.fillMaxHeight()) {
            Box {
                AsyncImage(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(2F),
                    contentScale = ContentScale.Crop,
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(movie?.getImageUrl(30))
                        .crossfade(true)
                        .build(),
                    error = painterResource(R.drawable.ic_sad_face),
                    contentDescription = movie?.name,
                    placeholder = BrushPainter(
                        Brush.linearGradient(
                            listOf(
                                Color(color = 0xFFFFFFFF),
                                Color(color = 0xFFDDDDDD),
                            )
                        )
                    )
                )

                Column {
                    Spacer(
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(2.65F)
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxHeight(0.2F)
                            .padding(end = dimensionResource(R.dimen.element_spacing)),
                        verticalAlignment = Alignment.Bottom
                    ) {
                        PosterImage(
                            modifier = Modifier
                                .fillMaxHeight()
                                .aspectRatio(0.75F)
                                .padding(
                                    start = dimensionResource(R.dimen.element_spacing)
                                )
                                .clip(RoundedCornerShape(dimensionResource(R.dimen.image_corner_radius))),
                            imageUrl = movie?.getImageUrl(500) ?: "",
                            contentDescription = movie?.name
                        )

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = dimensionResource(R.dimen.element_spacing))
                        ) {
                            Text(
                                modifier = Modifier.padding(bottom = 2.dp),
                                style = MaterialTheme.typography.body1,
                                text = movie?.name ?: "",
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )

                            Text(
                                modifier = Modifier.padding(bottom = 2.dp),
                                text = movie?.genre ?: "",
                                style = MaterialTheme.typography.body2,
                                color = Color.Gray,
                                fontSize = 12.sp
                            )

                            Text(
                                text = "${movie?.minutes}min",
                                color = Color.Gray,
                                style = MaterialTheme.typography.body2,
                                fontSize = 12.sp
                            )

                            Text(
                                text = stringResource(
                                    R.string.release_date_label,
                                    movie?.releaseDate ?: ""
                                ),
                                color = Color.Gray,
                                style = MaterialTheme.typography.body2,
                                fontSize = 12.sp
                            )
                        }
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1F)
                            .padding(dimensionResource(R.dimen.element_spacing))
                    ) {
                        Text(
                            modifier = Modifier.padding(bottom = 8.dp),
                            style = MaterialTheme.typography.body1,
                            text = "Synopsis",
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )

                        Text(
                            modifier = Modifier.padding(bottom = 2.dp),
                            text = movie?.longDescription ?: "",
                            style = MaterialTheme.typography.body2,
                            color = Color.Gray
                        )
                    }

                    FilledTonalButton(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(dimensionResource(R.dimen.element_spacing)),
                        onClick = {
                            onFavoriteButtonClicked(movie ?: return@FilledTonalButton)
                        },
                        shape = RoundedCornerShape(dimensionResource(R.dimen.image_corner_radius))
                    ) {
                        Text(
                            text =
                            if (movie?.isFavorite == true) {
                                stringResource(R.string.added_to_favorites_label)
                            } else {
                                stringResource(R.string.add_to_favorites_label)
                            }
                        )
                    }
                }
            }
        }

        CenterAlignedTopAppBar(
            title = {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = movie?.name ?: "",
                    textAlign = TextAlign.Center,
                    color = Color.White
                )
            },
            navigationIcon = {
                IconButton(
                    modifier = Modifier
                        .padding(start = 8.dp)
                        .size(24.dp),
                    onClick = onBackButtonClicked
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = Icons.Default.ArrowBack.name,
                        tint = Color.White
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color.Transparent,
                titleContentColor = MaterialTheme.colors.primary,
            ),
        )
    }
}

@Preview
@Composable
fun DetailsScreenPreview() {
    MovieBrowserTheme {
        DetailsScreenLayout(
            movie = Movie(
                movieId = 1,
                name = "A Star Is Born (2018)",
                _imageUrl = "",
                _price = 7.99F,
                currency = "PHP",
                genre = "Romance",
                longDescription = "This is the Long Description",
                _timeMillis = 100000,
                _releaseDate = "1983-05-25T07:00:00Z"
            )
        )
    }
}
@file:OptIn(
    ExperimentalMaterialApi::class,
    ExperimentalFoundationApi::class,
    ExperimentalMaterial3Api::class
)

package medina.juanantonio.moviebrowser.ui.screens.home_screen

import android.icu.text.SimpleDateFormat
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import medina.juanantonio.moviebrowser.R
import medina.juanantonio.moviebrowser.data.models.Movie
import medina.juanantonio.moviebrowser.ui.composables.PosterImage
import medina.juanantonio.moviebrowser.ui.navigation.Screen
import medina.juanantonio.moviebrowser.ui.theme.MovieBrowserTheme
import medina.juanantonio.moviebrowser.ui.theme.MovieCardShadowColor
import java.util.*

sealed class HomeScreenUIState {
    class Error(val message: String) : HomeScreenUIState()
    class Success(val list: List<Movie>) : HomeScreenUIState()
}

/**
 * This composable handles the navController, uiStates, and viewModel.
 * It runs the LaunchedEffect once on start, to listen to updates from the
 * favorites list and cache movies list. The actual Layout is in the HomeScreenLayout,
 * which is Previewed below
 */
@Composable
fun HomeScreen(
    navController: NavController,
    homeViewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by homeViewModel.uiState.collectAsState()
    var searchText by rememberSaveable { homeViewModel.searchText }
    val isLoading by rememberSaveable { homeViewModel.isLoading }
    val lastTimeUpdated by rememberSaveable { homeViewModel.lastTimeUpdated }

    LaunchedEffect(Unit) {
        homeViewModel.listenToHomeScreenFeed()
    }

    HomeScreenLayout(
        searchText = searchText,
        uiState = uiState,
        isLoading = isLoading,
        lastTimeUpdated = lastTimeUpdated,
        onSearchChanged = {
            searchText = it
            homeViewModel.onSearchChanged(it)
        },
        onItemClicked = {
            homeViewModel.onItemClicked(it) {
                navController.navigate(Screen.DetailScreen.route)
            }
        },
        onFavoriteClicked = {
            homeViewModel.onFavoriteClicked(it)
        }
    )
}

/**
 * This composable is the actual layout for the HomeScreen, it accepts a uiState,
 * searchText, and isLoading.
 */
@Composable
fun HomeScreenLayout(
    searchText: String,
    uiState: HomeScreenUIState,
    isLoading: Boolean,
    lastTimeUpdated: String,
    onSearchChanged: (String) -> Unit = {},
    onItemClicked: (Movie) -> Unit = {},
    onFavoriteClicked: (Movie) -> Unit = {}
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = stringResource(R.string.movies_title),
                        textAlign = TextAlign.Center
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    titleContentColor = MaterialTheme.colors.primary,
                ),
            )
        },
    ) {
        Column {
            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        start = dimensionResource(R.dimen.element_spacing),
                        end = dimensionResource(R.dimen.element_spacing)
                    ),
                value = searchText,
                label = { Text(text = stringResource(R.string.search_input_label)) },
                textStyle = MaterialTheme.typography.body1,
                onValueChange = onSearchChanged,
                trailingIcon = {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .size(dimensionResource(R.dimen.icon_size)),
                            color = MaterialTheme.colors.primary
                        )
                    }
                }
            )

            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        top = 4.dp,
                        end = dimensionResource(R.dimen.element_spacing)
                    ),
                text = stringResource(
                    R.string.last_updated_label,
                    lastTimeUpdated.ifBlank {
                        SimpleDateFormat(
                            "yyyy-MM-dd HH:mm:ss.SSS",
                            Locale.getDefault()
                        ).format(Date())
                    }
                ),
                style = MaterialTheme.typography.body2,
                textAlign = TextAlign.End
            )

            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.element_spacing)))

            when (uiState) {
                is HomeScreenUIState.Success -> {
                    HomeScreenSuccessState(uiState.list, onItemClicked, onFavoriteClicked)
                }
                is HomeScreenUIState.Error -> {
                    if (isLoading) return@Column
                    HomeScreenErrorState(uiState.message)
                }
            }
        }
    }
}

@Composable
fun HomeScreenSuccessState(
    list: List<Movie>, onItemClicked: (Movie) -> Unit,
    onFavoriteClicked: (Movie) -> Unit = {}
) {
    val navigationPaddingValues = WindowInsets.navigationBars.asPaddingValues()

    LazyColumn {
        items(list, key = { it.movieId }) {
            MovieItem(
                modifier = Modifier.animateItemPlacement(),
                movie = it,
                onClick = onItemClicked,
                onFavoriteClicked = onFavoriteClicked
            )
        }

        item {
            Spacer(modifier = Modifier.padding(navigationPaddingValues))
        }
    }
}

@Composable
fun HomeScreenErrorState(message: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(R.drawable.ic_sad_face),
            contentDescription = Icons.Default.Warning.name,
            modifier = Modifier
                .fillMaxWidth(0.2F)
                .padding(8.dp)
        )

        Text(text = message, style = MaterialTheme.typography.body2)
    }
}

@Composable
fun MovieItem(
    modifier: Modifier = Modifier,
    movie: Movie,
    onClick: (Movie) -> Unit = {},
    onFavoriteClicked: (Movie) -> Unit = {}
) {
    Box(
        modifier = modifier.padding(
            start = dimensionResource(R.dimen.element_spacing),
            end = dimensionResource(R.dimen.element_spacing),
            bottom = dimensionResource(R.dimen.element_spacing)
        )
    ) {
        Card(
            modifier = Modifier
                .padding(top = 32.dp)
                .shadow(
                    elevation = dimensionResource(R.dimen.card_elevation),
                    ambientColor = MovieCardShadowColor,
                    spotColor = MovieCardShadowColor
                ),
            onClick = { onClick(movie) }
        ) {
            Row(modifier = Modifier.fillMaxWidth()) {
                Spacer(
                    modifier = Modifier
                        .width(dimensionResource(R.dimen.movie_item_poster_width))
                )

                Column(
                    modifier = Modifier
                        .padding(all = dimensionResource(R.dimen.element_spacing))
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.End
                    ) {
                        IconButton(
                            modifier = Modifier.size(dimensionResource(R.dimen.icon_size)),
                            onClick = { onFavoriteClicked(movie) }
                        ) {
                            Icon(
                                imageVector =
                                if (movie.isFavorite) Icons.Filled.Favorite
                                else Icons.Outlined.FavoriteBorder,
                                contentDescription = Icons.Default.Favorite.name,
                                tint = MaterialTheme.colors.primary
                            )
                        }
                    }

                    Text(
                        modifier = Modifier.padding(bottom = 2.dp),
                        style = MaterialTheme.typography.body1,
                        text = movie.name,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Text(
                        modifier = Modifier.padding(bottom = 2.dp),
                        text = stringResource(
                            R.string.genre_minutes_label,
                            movie.genre,
                            movie.minutes
                        ),
                        style = MaterialTheme.typography.body2,
                        color = Color.Gray
                    )

                    Text(
                        text = movie.displayPrice,
                        color = Color.Gray,
                        style = MaterialTheme.typography.body2,
                    )
                }
            }
        }

        PosterImage(
            modifier = Modifier
                .width(dimensionResource(R.dimen.movie_item_poster_width))
                .aspectRatio(0.75F)
                .padding(
                    start = dimensionResource(R.dimen.element_spacing),
                    bottom = dimensionResource(R.dimen.element_spacing)
                )
                .clip(RoundedCornerShape(dimensionResource(R.dimen.image_corner_radius)))
                .align(Alignment.BottomStart),
            imageUrl = movie.getImageUrl(750),
            contentDescription = movie.name
        )
    }
}

@Preview(device = Devices.PIXEL)
@Preview(device = Devices.PIXEL_XL)
@Preview(showBackground = true)
@Composable
fun HomeScreenSuccessPreview() {
    MovieBrowserTheme {
        HomeScreenLayout(
            searchText = "Superman",
            uiState = HomeScreenUIState.Success(
                list = listOf(
                    Movie(
                        movieId = 1,
                        name = "A Star Is Born (2018)",
                        _imageUrl = "",
                        _price = 7.99F,
                        currency = "PHP",
                        genre = "Romance",
                        longDescription = "This is the Long Description",
                        _timeMillis = 100,
                        _releaseDate = ""
                    )
                )
            ),
            isLoading = false,
            lastTimeUpdated = "2023-01-12 10:10:10"
        )
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenErrorPreview() {
    MovieBrowserTheme {
        HomeScreenLayout(
            searchText = "Superman",
            uiState = HomeScreenUIState.Error(message = "No Results Found"),
            isLoading = true,
            lastTimeUpdated = "2023-01-12 10:10:10"
        )
    }
}

@Preview
@Composable
fun MovieItemPreview() {
    MovieBrowserTheme {
        MovieItem(
            movie = Movie(
                movieId = 1,
                name = "A Star Is Born (2018)",
                _imageUrl = "",
                _price = 7.99F,
                currency = "PHP",
                genre = "Romance",
                longDescription = "This is the Long Description",
                _timeMillis = 100000,
                _releaseDate = ""
            )
        )
    }
}
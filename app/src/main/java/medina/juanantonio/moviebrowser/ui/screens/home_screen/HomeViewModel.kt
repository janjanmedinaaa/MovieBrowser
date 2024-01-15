package medina.juanantonio.moviebrowser.ui.screens.home_screen

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import medina.juanantonio.moviebrowser.data.models.CacheMovie
import medina.juanantonio.moviebrowser.data.models.Favorite
import medina.juanantonio.moviebrowser.data.models.Movie
import medina.juanantonio.moviebrowser.data.repository.ItunesRepository
import medina.juanantonio.moviebrowser.di.CoroutineDispatchers
import medina.juanantonio.moviebrowser.network.Result
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val itunesRepository: ItunesRepository,
    private val dispatchers: CoroutineDispatchers
) : ViewModel() {

    private var searchJob: Job? = null
    val searchText = mutableStateOf("")
    val isLoading = mutableStateOf(true)

    val uiState = MutableStateFlow<HomeScreenUIState>(
        HomeScreenUIState.Error(message = "No Favorites Yet")
    )

    // This functions runs once on start of the HomeScreen Composable
    // and listens to changes from the local Favorites list and Cache Movies
    // list
    fun listenToHomeScreenFeed() {
        itunesRepository
            .getHomeScreenFeed { favorites, cacheMovies ->
                updateUIState(favorites, cacheMovies)
            }
            .flowOn(dispatchers.io)
            .launchIn(viewModelScope)
    }

    private fun updateUIState(
        favorites: List<Favorite>,
        cacheMovies: List<CacheMovie>
    ) {
        // This restores the last search keyword that the user entered
        searchText.value = cacheMovies.firstOrNull()?.keyword ?: ""

        // No Search Input so just return the favorites
        val newUIState = if (searchText.value == "") {
            if (favorites.isEmpty()) {
                HomeScreenUIState.Error(message = "No Favorites Yet")
            } else {
                HomeScreenUIState.Success(
                    favorites.map {
                        it.isFavorite = true
                        it
                    }
                )
            }
        } else {
            if (cacheMovies.isEmpty()) {
                HomeScreenUIState.Error(message = "No Results Found")
            } else {
                HomeScreenUIState.Success(
                    cacheMovies.map { cacheMovie ->
                        cacheMovie.isFavorite = favorites.any { favorite ->
                            favorite.favoriteId == cacheMovie.movieId
                        }
                        cacheMovie
                    }
                )
            }
        }

        uiState.update { newUIState }
        isLoading.value = false
    }

    // This runs every time user inputs in the search bar
    // I added a delay so that it waits for the user to finish typing
    // This function calls the iTunes Search API from the repository,
    // then the repository handles the UI Changes by updating the Kotlin Flows
    // The results from the Search API is automatically cached, the cached items are
    // used for displaying the results.
    fun onSearchChanged(input: String) {
        if (searchJob?.isActive == true) {
            searchJob?.cancel()
            isLoading.value = false
        }

        // In case the input is blank again, clear the cache movies so that the
        // Kotlin Flows just displays the Favorites list
        if (input.isBlank()) {
            viewModelScope.launch(dispatchers.io) {
                itunesRepository.clearCacheMovies()
            }

            return
        }

        searchJob = viewModelScope.launch(dispatchers.io) {
            delay(250)

            isLoading.value = true
            val results = itunesRepository.getSearchResults(input)

            if (results is Result.Error) {
                withContext(dispatchers.main) {
                    results.message?.let {
                        uiState.value = HomeScreenUIState.Error(message = it)
                    }
                }
            }

            isLoading.value = false
        }
    }

    // This updates the favorite state of the movie item in the database
    // and causes the Kotlin Flows to update, in turns updating the UI list
    fun onFavoriteClicked(movie: Movie) {
        viewModelScope.launch(dispatchers.io) {
            if (!movie.isFavorite) {
                itunesRepository.addMovieToFavorites(movie)
            } else {
                itunesRepository.removeFavorite(movie.movieId)
            }
        }
    }

    // This saves the movie item in the database and will be used in the Details Screen.
    // The cached movie item is also used for the Persistence functionality of the app.
    fun onItemClicked(movie: Movie, action: () -> Unit) {
        viewModelScope.launch(dispatchers.io) {
            itunesRepository.setDisplayedCacheMovie(movie.movieId)

            withContext(dispatchers.main) {
                action()
            }
        }
    }
}
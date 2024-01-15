package medina.juanantonio.moviebrowser.ui.screens.details_screen

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import medina.juanantonio.moviebrowser.data.models.Movie
import medina.juanantonio.moviebrowser.data.repository.ItunesRepository
import medina.juanantonio.moviebrowser.di.CoroutineDispatchers
import javax.inject.Inject

@HiltViewModel
class DetailsScreenViewModel @Inject constructor(
    private val itunesRepository: ItunesRepository,
    private val dispatchers: CoroutineDispatchers
) : ViewModel() {

    val movie = mutableStateOf<Movie?>(null)

    init {
        // This runs once and listens to changes in the local Favorites list
        // and update the UI accordingly. For the display item, I retrieve
        // the cached display item from the database.
        viewModelScope.launch {
            itunesRepository
                .getFavorites()
                .flowOn(dispatchers.io)
                .collectLatest {
                    movie.value = itunesRepository.getDisplayedCacheMovie()?.apply {
                        isFavorite = it.any { it.favoriteId == movieId }
                    }
                }
        }
    }

    // When exiting the screen, remove the displayed cache movie
    fun onBackButtonClicked(action: () -> Unit = {}) {
        viewModelScope.launch(dispatchers.io) {
            itunesRepository.removeDisplayedCacheMovie()

            withContext(dispatchers.main) {
                action()
            }
        }
    }

    // This updates the favorite state of the movie item in the database
    // and causes the Kotlin Flows to update, in turns updating the UI list
    fun onFavoriteButtonClicked(movie: Movie) {
        viewModelScope.launch(dispatchers.io) {
            if (!movie.isFavorite) {
                itunesRepository.addMovieToFavorites(movie)
            } else {
                itunesRepository.removeFavorite(movie.movieId)
            }
        }
    }
}
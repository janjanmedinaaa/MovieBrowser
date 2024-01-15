package medina.juanantonio.moviebrowser.data.repository

import kotlinx.coroutines.flow.*
import medina.juanantonio.moviebrowser.data.models.CacheMovie
import medina.juanantonio.moviebrowser.data.models.Favorite
import medina.juanantonio.moviebrowser.data.models.Movie
import medina.juanantonio.moviebrowser.data.models.toMovie
import medina.juanantonio.moviebrowser.database.sources.CacheMovieLocalSource
import medina.juanantonio.moviebrowser.database.sources.FavoritesLocalSource
import medina.juanantonio.moviebrowser.network.Result
import medina.juanantonio.moviebrowser.network.models.search.SearchResultsResponse
import medina.juanantonio.moviebrowser.network.sources.ItunesRemoteSource

class ItunesRepositoryImpl(
    private val remoteSource: ItunesRemoteSource,
    private val favoritesLocalSource: FavoritesLocalSource,
    private val cacheMovieLocalSource: CacheMovieLocalSource
) : ItunesRepository {

    private val manuallyUpdateFlow = MutableStateFlow(Unit)

    override suspend fun getSearchResults(
        query: String,
        country: String,
        media: String
    ): Result<SearchResultsResponse> {
        val result = remoteSource.getSearchResult(query, country, media)

        if (result is Result.Success) {
            val movieList = result.data?.results?.map { it.toMovie() } ?: emptyList()
            addToCache(movieList, query)
        }

        return result
    }

    override fun getFavorites(): Flow<List<Favorite>> {
        return favoritesLocalSource.getFavorites()
    }

    override suspend fun addMovieToFavorites(movie: Movie) {
        val favorite = Favorite(
            favoriteId = movie.movieId,
            name = movie.name,
            _imageUrl = movie._imageUrl,
            _price = movie._price,
            currency = movie.currency,
            genre = movie.genre,
            longDescription = movie.longDescription,
            _timeMillis = movie._timeMillis,
            _releaseDate = movie._releaseDate
        )

        favoritesLocalSource.addFavorite(favorite)
    }

    override suspend fun removeFavorite(id: Int) {
        favoritesLocalSource.removeVideo(id)
    }

    private suspend fun addToCache(list: List<Movie>, keyword: String = "") {
        val cacheMovieList = list.map { movie ->
            CacheMovie(
                cacheMovieId = movie.movieId,
                name = movie.name,
                _imageUrl = movie._imageUrl,
                _price = movie._price,
                currency = movie.currency,
                genre = movie.genre,
                longDescription = movie.longDescription,
                _timeMillis = movie._timeMillis,
                _releaseDate = movie._releaseDate
            ).apply {
                this.keyword = keyword
            }
        }

        cacheMovieLocalSource.addToCache(cacheMovieList)
    }

    override suspend fun getDisplayedCacheMovie(): Movie? {
        return cacheMovieLocalSource.getDisplayedCacheMovie()
    }

    override suspend fun setDisplayedCacheMovie(id: Int) {
        val cacheMovieList = cacheMovieLocalSource.getCacheMovies().firstOrNull()

        if (cacheMovieList.isNullOrEmpty()) {
            val favoritesList = getFavorites().firstOrNull() ?: return
            addToCache(favoritesList)
        }

        cacheMovieLocalSource.setDisplayedCacheMovie(id)
    }

    override suspend fun removeDisplayedCacheMovie() {
        cacheMovieLocalSource.removeDisplayedCacheMovie()
    }

    override suspend fun clearCacheMovies() {
        val cacheMovieList = cacheMovieLocalSource.getCacheMovies().firstOrNull()

        if (cacheMovieList.isNullOrEmpty()) {
            manuallyUpdateFlow.update { }
        } else {
            cacheMovieLocalSource.clearCacheMovies()
        }
    }

    override fun getHomeScreenFeed(
        transform: (List<Favorite>, List<CacheMovie>) -> Unit
    ): Flow<Unit> {
        return getFavorites()
            .combine(manuallyUpdateFlow) { favorites, _ -> favorites }
            .combine(cacheMovieLocalSource.getCacheMovies(), transform)
    }
}

interface ItunesRepository {
    suspend fun getSearchResults(
        query: String,
        country: String = "au",
        media: String = "movie"
    ): Result<SearchResultsResponse>

    fun getFavorites(): Flow<List<Favorite>>
    suspend fun addMovieToFavorites(movie: Movie)
    suspend fun removeFavorite(id: Int)

    suspend fun getDisplayedCacheMovie(): Movie?
    suspend fun setDisplayedCacheMovie(id: Int)
    suspend fun removeDisplayedCacheMovie()
    suspend fun clearCacheMovies()

    fun getHomeScreenFeed(transform: (List<Favorite>, List<CacheMovie>) -> Unit): Flow<Unit>
}
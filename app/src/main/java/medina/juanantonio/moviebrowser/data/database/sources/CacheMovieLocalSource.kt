package medina.juanantonio.moviebrowser.data.database.sources

import kotlinx.coroutines.flow.Flow
import medina.juanantonio.moviebrowser.data.database.models.CacheMovie
import medina.juanantonio.moviebrowser.data.database.MovieBrowserDb

class CacheMovieLocalSourceImpl(movieBrowserDb: MovieBrowserDb): CacheMovieLocalSource {
    private val cacheMovieDao = movieBrowserDb.cacheMovieDao()

    override suspend fun addToCache(list: List<CacheMovie>) {
        cacheMovieDao.insertNew(list)
    }

    override fun getCacheMovies(): Flow<List<CacheMovie>> {
        return cacheMovieDao.getAll()
    }

    override suspend fun getDisplayedCacheMovie(): CacheMovie? {
        return cacheMovieDao.getCurrentlyDisplayed()
    }

    override suspend fun setDisplayedCacheMovie(id: Int) {
        cacheMovieDao.updateCurrentlyDisplayed(id)
    }

    override suspend fun removeDisplayedCacheMovie() {
        cacheMovieDao.deleteCurrentlyDisplayed()
    }

    override suspend fun clearCacheMovies() {
        cacheMovieDao.clear()
    }
}

interface CacheMovieLocalSource {
    suspend fun addToCache(list: List<CacheMovie>)
    fun getCacheMovies(): Flow<List<CacheMovie>>
    suspend fun getDisplayedCacheMovie(): CacheMovie?
    suspend fun setDisplayedCacheMovie(id: Int)
    suspend fun removeDisplayedCacheMovie()
    suspend fun clearCacheMovies()
}
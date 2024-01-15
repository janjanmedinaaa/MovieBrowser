package medina.juanantonio.moviebrowser.database.sources

import kotlinx.coroutines.flow.Flow
import medina.juanantonio.moviebrowser.data.models.Favorite
import medina.juanantonio.moviebrowser.database.MovieBrowserDb

class FavoritesLocalSourceImpl(movieBrowserDb: MovieBrowserDb) : FavoritesLocalSource {
    private val favoriteDao = movieBrowserDb.favoriteDao()

    override suspend fun addFavorite(favorite: Favorite) {
        favoriteDao.insert(favorite)
    }

    override fun getFavorites(): Flow<List<Favorite>> {
        return favoriteDao.getAll()
    }

    override suspend fun removeVideo(id: Int) {
        favoriteDao.delete(id)
    }
}

interface FavoritesLocalSource {
    suspend fun addFavorite(favorite: Favorite)
    fun getFavorites(): Flow<List<Favorite>>
    suspend fun removeVideo(id: Int)
}
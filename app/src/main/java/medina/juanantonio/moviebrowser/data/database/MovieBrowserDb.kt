package medina.juanantonio.moviebrowser.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import medina.juanantonio.moviebrowser.data.database.models.CacheMovie
import medina.juanantonio.moviebrowser.data.database.models.Favorite
import medina.juanantonio.moviebrowser.data.database.dao.CacheMovieDao
import medina.juanantonio.moviebrowser.data.database.dao.FavoriteDao

@Database(
    entities = [
        Favorite::class,
        CacheMovie::class
    ],
    version = MovieBrowserDb.VERSION_CODE,
    exportSchema = true
)

abstract class MovieBrowserDb : RoomDatabase() {
    abstract fun favoriteDao(): FavoriteDao
    abstract fun cacheMovieDao(): CacheMovieDao

    companion object {
        const val VERSION_CODE = 1
    }
}
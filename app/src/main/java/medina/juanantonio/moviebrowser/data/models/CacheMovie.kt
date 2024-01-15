package medina.juanantonio.moviebrowser.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class CacheMovie(
    @PrimaryKey val cacheMovieId: Int,
    name: String,
    _imageUrl: String,
    _price: Float?,
    currency: String,
    genre: String,
    longDescription: String,
    _timeMillis: Int,
    _releaseDate: String
) : Movie(
    cacheMovieId,
    name,
    _imageUrl,
    _price,
    currency,
    genre,
    longDescription,
    _timeMillis,
    _releaseDate
) {

    var currentlyDisplayed: Boolean = false
    var keyword: String = ""
}
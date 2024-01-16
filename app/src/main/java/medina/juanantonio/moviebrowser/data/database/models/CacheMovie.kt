package medina.juanantonio.moviebrowser.data.database.models

import android.icu.text.SimpleDateFormat
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity
class CacheMovie(
    @PrimaryKey val cacheMovieId: Int,
    name: String,
    _imageUrl: String,
    _price: Float?,
    currency: String?,
    genre: String?,
    longDescription: String?,
    _timeMillis: Int?,
    _releaseDate: String?
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

    var timeSaved: String =
        SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault()).format(Date())
}
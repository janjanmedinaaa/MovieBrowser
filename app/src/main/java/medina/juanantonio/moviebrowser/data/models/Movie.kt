package medina.juanantonio.moviebrowser.data.models

import android.icu.text.SimpleDateFormat
import androidx.room.Ignore
import medina.juanantonio.moviebrowser.network.models.search.SearchResult
import java.util.*

open class Movie(
    @Ignore
    val movieId: Int,

    val name: String,
    val _imageUrl: String,
    val _price: Float?,
    val currency: String,
    val genre: String,
    val longDescription: String,
    val _timeMillis: Int,
    val _releaseDate: String
) {

    @get:Ignore
    val displayPrice: String
        get() = if (_price == null) "FREE" else "${currency.uppercase()} $_price"


    @get:Ignore
    val minutes: Int
        get() = _timeMillis / 60000

    @get:Ignore
    val releaseDate: String
        get() {
            val formatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.ENGLISH)
            val parseDate = formatter.parse(_releaseDate)

            return SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).format(parseDate)
        }

    fun getImageUrl(height: Int): String {
        return _imageUrl.replace("100x100", "${height}x${height}")
    }

    @Ignore
    var isFavorite: Boolean = false
}

fun SearchResult.toMovie(): Movie {
    return Movie(
        movieId = this.id,
        name = this.name,
        _imageUrl = this.imageUrl,
        _price = this.price,
        currency = this.currency,
        genre = this.genre,
        longDescription = this.longDescription,
        _timeMillis = this.trackTimeMillis,
        _releaseDate = this.releaseDate
    )
}
package medina.juanantonio.moviebrowser.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class Favorite(
    @PrimaryKey val favoriteId: Int,
    name: String,
    _imageUrl: String,
    _price: Float?,
    currency: String?,
    genre: String?,
    longDescription: String?,
    _timeMillis: Int?,
    _releaseDate: String?
) : Movie(
    favoriteId,
    name,
    _imageUrl,
    _price,
    currency,
    genre,
    longDescription,
    _timeMillis,
    _releaseDate
)
package medina.juanantonio.moviebrowser.network.models.search

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SearchResultsResponse(
    @Json(name = "resultCount")
    val resultCount: Int,

    @Json(name = "results")
    val results: List<SearchResult>
)

// I only implemented required fields from the API so that it's
// easier to read
@JsonClass(generateAdapter = true)
class SearchResult(
    @Json(name = "trackId")
    val id: Int,

    @Json(name = "trackName")
    val name: String,

    @Json(name = "artworkUrl100")
    val imageUrl: String,

    @Json(name = "trackPrice")
    val price: Float?,

    @Json(name = "currency")
    val currency: String,

    @Json(name = "primaryGenreName")
    val genre: String,

    @Json(name = "longDescription")
    val longDescription: String,

    @Json(name = "trackTimeMillis")
    val trackTimeMillis: Int,

    @Json(name = "releaseDate")
    val releaseDate: String
)
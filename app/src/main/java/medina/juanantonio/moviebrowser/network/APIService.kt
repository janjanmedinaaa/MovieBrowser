package medina.juanantonio.moviebrowser.network

import medina.juanantonio.moviebrowser.network.models.search.SearchResultsResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface APIService {

    @GET("search")
    suspend fun getSearchResults(
        @Query("term") query: String,
        @Query("country") country: String,
        @Query("media") media: String
    ): Response<SearchResultsResponse>
}
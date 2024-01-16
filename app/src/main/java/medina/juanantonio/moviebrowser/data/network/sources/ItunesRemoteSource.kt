package medina.juanantonio.moviebrowser.data.network.sources

import android.content.Context
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.withContext
import medina.juanantonio.moviebrowser.di.CoroutineDispatchers
import medina.juanantonio.moviebrowser.data.network.APIService
import medina.juanantonio.moviebrowser.data.network.Result
import medina.juanantonio.moviebrowser.data.network.models.search.SearchResultsResponse
import medina.juanantonio.moviebrowser.data.network.wrapWithResult

class ItunesRemoteSourceImpl(
    context: Context,
    private val apiService: APIService,
    private val dispatchers: CoroutineDispatchers
) : BaseRemoteSource(context), ItunesRemoteSource {

    override suspend fun getSearchResult(
        query: String,
        country: String,
        media: String
    ): Result<SearchResultsResponse> {
        return try {
            val response = withContext(dispatchers.io) {
                apiService.getSearchResults(
                    query = query,
                    country = country,
                    media = media,
                )
            }
            response.wrapWithResult()
        } catch (exception: CancellationException) {
            Result.Cancelled()
        } catch (exception: Exception) {
            getDefaultErrorResponse()
        }
    }
}

interface ItunesRemoteSource {
    suspend fun getSearchResult(
        query: String,
        country: String,
        media: String
    ): Result<SearchResultsResponse>
}
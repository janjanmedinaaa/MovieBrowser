package medina.juanantonio.moviebrowser.network.sources

import android.content.Context
import medina.juanantonio.moviebrowser.R
import medina.juanantonio.moviebrowser.network.Result

open class BaseRemoteSource(private val context: Context) {
    fun <T> getDefaultErrorResponse(internetError: Boolean = false): Result<T> {
        return if (internetError) {
            Result.Error(-1, context.getString(R.string.no_internet_connection))
        } else Result.Error(-2, context.getString(R.string.something_went_wrong))
    }
}
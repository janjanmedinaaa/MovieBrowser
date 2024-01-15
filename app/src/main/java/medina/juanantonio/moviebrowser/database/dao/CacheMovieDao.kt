package medina.juanantonio.moviebrowser.database.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import medina.juanantonio.moviebrowser.data.models.CacheMovie

@Dao
interface CacheMovieDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(list: List<CacheMovie>)

    @Query("SELECT * FROM cacheMovie")
    fun getAll(): Flow<List<CacheMovie>>

    @Query("SELECT * FROM cacheMovie WHERE currentlyDisplayed = 1")
    suspend fun getCurrentlyDisplayed(): CacheMovie?

    @Query("UPDATE cacheMovie SET currentlyDisplayed = 1 WHERE cacheMovieId = :id")
    fun updateCurrentlyDisplayed(id: Int)

    @Query("UPDATE cacheMovie SET currentlyDisplayed = 0 WHERE currentlyDisplayed = 1")
    suspend fun deleteCurrentlyDisplayed()

    @Query("DELETE FROM cacheMovie")
    suspend fun clear()

    @Transaction
    suspend fun insertNew(list: List<CacheMovie>) {
        clear()
        insert(list)
    }
}
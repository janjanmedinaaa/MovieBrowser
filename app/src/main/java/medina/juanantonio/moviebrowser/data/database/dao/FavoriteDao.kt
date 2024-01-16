package medina.juanantonio.moviebrowser.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import medina.juanantonio.moviebrowser.data.database.models.Favorite

@Dao
interface FavoriteDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(favorite: Favorite)

    @Query("SELECT * FROM favorite")
    fun getAll(): Flow<List<Favorite>>

    @Query("DELETE FROM favorite WHERE favoriteId = :id")
    suspend fun delete(id: Int)
}
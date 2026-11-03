package com.kax.across.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for [SavedItem] operations including
 * comprehensive FTS5 joins, tag filters, and state toggles.
 */
@Dao
interface SavedItemDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: SavedItem): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<SavedItem>): List<Long>

    @Update
    suspend fun update(item: SavedItem)

    @Delete
    suspend fun delete(item: SavedItem)

    @Query("DELETE FROM saved_items WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM saved_items")
    suspend fun deleteAll()

    @Query("UPDATE saved_items SET isFavorite = :isFavorite WHERE id = :id")
    suspend fun setFavorite(id: Long, isFavorite: Boolean)

    @Query("SELECT * FROM saved_items WHERE id = :id")
    fun getItemById(id: Long): Flow<SavedItem?>

    @Query("SELECT * FROM saved_items WHERE url = :url ORDER BY id DESC LIMIT 1")
    suspend fun getItemByUrl(url: String): SavedItem?

    @Query("SELECT * FROM saved_items WHERE isArchived = 0 ORDER BY timestamp DESC")
    fun getAllActive(): Flow<List<SavedItem>>

    @Query("SELECT * FROM saved_items WHERE isFavorite = 1 AND isArchived = 0 ORDER BY timestamp DESC")
    fun getFavorites(): Flow<List<SavedItem>>

    @Query("SELECT COUNT(*) FROM saved_items")
    fun getCount(): Flow<Int>

    /**
     * Primary FTS5 Full-Text Search query that joins the FTS5 virtual table
     * back to the core `saved_items` table using the implicit rowid.
     */
    @Query("""
        SELECT saved_items.* FROM saved_items
        JOIN saved_items_fts ON saved_items.id = saved_items_fts.docid
        WHERE saved_items_fts MATCH :query
        ORDER BY saved_items.timestamp DESC
    """)
    fun searchFts(query: String): Flow<List<SavedItem>>

    /**
     * Resilient fallback query for substring matching and special characters.
     */
    @Query("""
        SELECT * FROM saved_items
        WHERE (title LIKE '%' || :query || '%' 
           OR description LIKE '%' || :query || '%' 
           OR tags LIKE '%' || :query || '%' 
           OR siteName LIKE '%' || :query || '%'
           OR url LIKE '%' || :query || '%')
          AND isArchived = 0
        ORDER BY timestamp DESC
    """)
    fun searchLike(query: String): Flow<List<SavedItem>>

    @Query("""
        SELECT * FROM saved_items 
        WHERE tags LIKE '%' || :tag || '%' 
          AND isArchived = 0
        ORDER BY timestamp DESC
    """)
    fun getByTag(tag: String): Flow<List<SavedItem>>
}

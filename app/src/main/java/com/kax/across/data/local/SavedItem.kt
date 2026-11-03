package com.kax.across.data.local

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Core persistent model representing an archived URL bookmark
 * with scraped OpenGraph metadata, classification tags, and indexing states.
 */
@Entity(
    tableName = "saved_items",
    indices = [
        Index(value = ["url"], unique = false),
        Index(value = ["timestamp"])
    ]
)
data class SavedItem(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val url: String,
    val title: String,
    val description: String,
    val imageUrl: String? = null,
    val siteName: String = "",
    val tags: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val isFavorite: Boolean = false,
    val isArchived: Boolean = false
) {
    /**
     * Splits space-delimited tags into an iterable list.
     */
    val tagList: List<String>
        get() = tags.split(" ").filter { it.isNotBlank() }
}

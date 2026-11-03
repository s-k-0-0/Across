package com.kax.across.data.local

import androidx.room.Entity
import androidx.room.Fts4

/**
 * FTS Virtual Table entity mapped to [SavedItem] using Room FTS4.
 * Enables ultra-fast full-text indexing over title, description, tags, url, and siteName.
 */
@Entity(tableName = "saved_items_fts")
@Fts4(contentEntity = SavedItem::class)
data class SavedItemFts(
    val title: String,
    val description: String,
    val tags: String,
    val url: String,
    val siteName: String
)

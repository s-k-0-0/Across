package com.kax.across.data.local

import android.content.Context
import android.database.sqlite.SQLiteException
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [SavedItem::class, SavedItemFts::class],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun savedItemDao(): SavedItemDao

    companion object {
        private const val TAG = "AppDatabase"

        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "omnisave_database.db"
                )
                    .addCallback(DatabaseCallback())
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class DatabaseCallback : RoomDatabase.Callback() {
        override fun onOpen(db: SupportSQLiteDatabase) {
            super.onOpen(db)
            // Clean up legacy manual triggers that conflict with Room's built-in FTS sync triggers
            try {
                db.execSQL("DROP TRIGGER IF EXISTS saved_items_ai")
                db.execSQL("DROP TRIGGER IF EXISTS saved_items_ad")
                db.execSQL("DROP TRIGGER IF EXISTS saved_items_au")
            } catch (e: SQLiteException) {
                Log.e(TAG, "Failed to drop legacy FTS triggers", e)
            }
        }

        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val now = System.currentTimeMillis()
                    val sampleItems = listOf(
                        SavedItem(
                            url = "https://x.com/AndroidDev/status/18400000001",
                            title = "Kotlin 2.2 & Jetpack Compose Rendering Engine Deep Dive",
                            description = "New frame pacing optimizations and multi-window layout passes on Android 16 foldables.",
                            imageUrl = "https://developer.android.com/static/images/jetpack/compose-social.png",
                            siteName = "X",
                            tags = "#x #posts #android #tech",
                            timestamp = now - 1000 * 60 * 10,
                            isFavorite = true
                        ),
                        SavedItem(
                            url = "https://x.com/karpathy/status/18400000002",
                            title = "LLM Systems Architecture and Context Compression",
                            description = "Notes on token caching, speculative decoding, and low-latency local inference pipelines.",
                            imageUrl = null,
                            siteName = "X",
                            tags = "#x #posts #ai #tech",
                            timestamp = now - 1000 * 60 * 25,
                            isFavorite = false
                        ),
                        SavedItem(
                            url = "https://www.youtube.com/watch?v=dQw4w9WgXcQ",
                            title = "Rick Astley - Never Gonna Give You Up (Official Music Video)",
                            description = "The official music video for Rick Astley - Never Gonna Give You Up.",
                            imageUrl = "https://i.ytimg.com/vi/dQw4w9WgXcQ/maxresdefault.jpg",
                            siteName = "YouTube",
                            tags = "#youtube #video #music",
                            timestamp = now - 1000 * 60 * 45,
                            isFavorite = true
                        ),
                        SavedItem(
                            url = "https://www.youtube.com/watch?v=9bZkp7q19f0",
                            title = "PSY - GANGNAM STYLE (강남스타일) M/V",
                            description = "Official Music Video for PSY - GANGNAM STYLE.",
                            imageUrl = "https://i.ytimg.com/vi/9bZkp7q19f0/maxresdefault.jpg",
                            siteName = "YouTube",
                            tags = "#youtube #video #music",
                            timestamp = now - 1000 * 60 * 80,
                            isFavorite = false
                        ),
                        SavedItem(
                            url = "https://www.reddit.com/r/androiddev/comments/omnisave_architecture",
                            title = "r/androiddev: Best practices for WorkManager + Jsoup HTML scraping",
                            description = "Discussion on handling redirects, user-agent headers, and offline Room caching when saving URLs from Share Sheet.",
                            imageUrl = null,
                            siteName = "Reddit",
                            tags = "#reddit #community #android #code",
                            timestamp = now - 1000 * 60 * 140,
                            isFavorite = true
                        ),
                        SavedItem(
                            url = "https://www.reddit.com/r/LocalLLaMA/comments/quantization_benchmarks",
                            title = "r/LocalLLaMA: On-device quantization throughput comparison",
                            description = "Community benchmarks across mobile NPUs and Vulkan compute backends.",
                            imageUrl = null,
                            siteName = "Reddit",
                            tags = "#reddit #community #ai #hardware",
                            timestamp = now - 1000 * 60 * 210,
                            isFavorite = false
                        ),
                        SavedItem(
                            url = "https://github.com/torvalds/linux",
                            title = "torvalds/linux: Linux Kernel Source Tree",
                            description = "Monolithic Unix-like operating system kernel source tree maintained by Linus Torvalds.",
                            imageUrl = "https://opengraph.githubassets.com/1/torvalds/linux",
                            siteName = "GitHub",
                            tags = "#github #code #opensource #linux",
                            timestamp = now - 1000 * 60 * 320,
                            isFavorite = true
                        ),
                        SavedItem(
                            url = "https://github.com/jhy/jsoup",
                            title = "jhy/jsoup: Java HTML Parser for OpenGraph & DOM Extraction",
                            description = "Jsoup parses HTML to the same DOM as modern browsers, providing CSS selector queries and attribute extraction.",
                            imageUrl = "https://opengraph.githubassets.com/1/jhy/jsoup",
                            siteName = "GitHub",
                            tags = "#github #code #opensource #java",
                            timestamp = now - 1000 * 60 * 400,
                            isFavorite = false
                        ),
                        SavedItem(
                            url = "https://arxiv.org/abs/1706.03762",
                            title = "Attention Is All You Need - Transformer Architecture",
                            description = "Sequence transduction models based strictly on attention mechanisms, dispensing with recurrence and convolutions entirely.",
                            imageUrl = "https://arxiv.org/static/browse/0.3.4/images/arxiv-logo-fb.png",
                            siteName = "arXiv",
                            tags = "#article #reading #ai #science",
                            timestamp = now - 1000 * 60 * 500,
                            isFavorite = true
                        ),
                        SavedItem(
                            url = "https://www.seriouseats.com/the-best-crispy-roast-potatoes-recipe",
                            title = "The Best Crispy Roast Potatoes Technical Guide",
                            description = "Alkaline water parboiling creates a starchy slurry that dehydrates into a glass-like crisp crust in the oven.",
                            imageUrl = "https://www.seriouseats.com/thmb/d-p_H1MvVn6sK405M_687_530.jpg",
                            siteName = "Serious Eats",
                            tags = "#article #reading #recipe #food",
                            timestamp = now - 1000 * 60 * 640,
                            isFavorite = false
                        )
                    )
                    INSTANCE?.savedItemDao()?.insertAll(sampleItems)
                } catch (e: SQLiteException) {
                    Log.e(TAG, "Failed to seed initial database items", e)
                }
            }
        }
    }
}

package com.kax.across.data.repository

import android.content.Context
import android.content.SharedPreferences
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.kax.across.data.local.SavedItem
import com.kax.across.data.local.SavedItemDao
import com.kax.across.worker.ScraperWorker
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf

/**
 * Repository mediating between Room DAO operations, FTS4 full-text queries,
 * persistent onboarding preferences, and asynchronous WorkManager metadata scraping tasks.
 */
class SavedItemRepository(
    private val dao: SavedItemDao,
    private val context: Context
) {
    companion object {
        private const val PREFS_NAME = "across_the_internet_prefs"
        private const val KEY_TUTORIAL_COMPLETED = "tutorial_completed"
    }

    private val prefs: SharedPreferences by lazy {
        context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun isTutorialCompleted(): Boolean {
        return prefs.getBoolean(KEY_TUTORIAL_COMPLETED, false)
    }

    fun setTutorialCompleted(completed: Boolean = true) {
        prefs.edit().putBoolean(KEY_TUTORIAL_COMPLETED, completed).apply()
    }

    fun getAllItems(): Flow<List<SavedItem>> = dao.getAllActive()

    fun getFavorites(): Flow<List<SavedItem>> = dao.getFavorites()

    fun getCount(): Flow<Int> = dao.getCount()

    fun getByTag(tag: String): Flow<List<SavedItem>> = dao.getByTag(tag)

    /**
     * Executes an FTS full-text query with graceful fallback to LIKE
     * in case of SQLite tokenizer constraints or unmatched wildcard syntax.
     */
    fun search(query: String): Flow<List<SavedItem>> {
        val trimmed = query.trim()
        if (trimmed.isBlank()) {
            return dao.getAllActive()
        }

        if (trimmed.startsWith("#")) {
            return dao.getByTag(trimmed)
        }

        val sanitizedFtsQuery = sanitizeForFts(trimmed)

        return dao.searchFts(sanitizedFtsQuery).catch {
            emitAll(dao.searchLike(trimmed))
        }.flatMapLatest { results ->
            if (results.isEmpty()) {
                dao.searchLike(trimmed)
            } else {
                flowOf(results)
            }
        }
    }

    private suspend fun <T> kotlinx.coroutines.flow.FlowCollector<T>.emitAll(flow: Flow<T>) {
        flow.collect { emit(it) }
    }

    private fun sanitizeForFts(query: String): String {
        val cleaned = query.replace(Regex("""["*^()+\-:]"""), " ").trim()
        val tokens = cleaned.split(Regex("""\s+""")).filter { it.isNotBlank() }
        if (tokens.isEmpty()) return "$query*"
        return tokens.joinToString(" AND ") { "$it*" }
    }

    fun enqueueScrape(url: String) {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val request = OneTimeWorkRequestBuilder<ScraperWorker>()
            .setInputData(workDataOf(ScraperWorker.KEY_URL to url.trim()))
            .setConstraints(constraints)
            .addTag(ScraperWorker.TAG)
            .build()

        WorkManager.getInstance(context).enqueue(request)
    }

    suspend fun setFavorite(id: Long, isFavorite: Boolean) {
        dao.setFavorite(id, isFavorite)
    }

    suspend fun delete(item: SavedItem) {
        dao.delete(item)
    }

    suspend fun deleteById(id: Long) {
        dao.deleteById(id)
    }

    suspend fun deleteAll() {
        dao.deleteAll()
    }

    suspend fun insert(item: SavedItem): Long {
        return dao.insert(item)
    }
}

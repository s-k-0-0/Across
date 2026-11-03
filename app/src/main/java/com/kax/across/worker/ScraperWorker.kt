package com.kax.across.worker

import android.content.Context
import android.database.sqlite.SQLiteException
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.kax.across.data.local.AppDatabase
import com.kax.across.data.local.SavedItem
import com.kax.across.util.AutoTagger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import java.io.IOException
import java.net.URI
import java.util.Locale

/**
 * Asynchronous WorkManager worker that fetches target HTML,
 * parses Open Graph / Twitter Cards / semantic HTML metadata,
 * generates taxonomy hashtags, and records the item in Room.
 */
class ScraperWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    companion object {
        const val TAG = "ScraperWorker"
        const val KEY_URL = "target_url"
        const val WORK_NAME = "omnisave_metadata_scraper"

        private const val USER_AGENT =
            "Mozilla/5.0 (Linux; Android 14; Pixel 8) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/124.0.0.0 Mobile Safari/537.36 OmniSave/1.0"
        private const val TIMEOUT_MILLIS = 10_000
    }

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        val rawUrl = inputData.getString(KEY_URL)
        if (rawUrl.isNullOrBlank()) {
            Log.e(TAG, "ScraperWorker aborted: target_url is null or empty")
            return@withContext Result.failure()
        }

        val cleanUrl = sanitizeUrl(rawUrl)
        val domain = extractDomain(cleanUrl)

        try {
            Log.d(TAG, "Initiating metadata extraction for: $cleanUrl")

            val document = Jsoup.connect(cleanUrl)
                .userAgent(USER_AGENT)
                .timeout(TIMEOUT_MILLIS)
                .followRedirects(true)
                .ignoreHttpErrors(true)
                .get()

            val title = sequenceOf(
                document.selectFirst("meta[property=og:title]")?.attr("content"),
                document.selectFirst("meta[name=twitter:title]")?.attr("content"),
                document.title(),
                document.selectFirst("h1")?.text()
            ).firstOrNull { !it.isNullOrBlank() }?.trim() ?: "Archive [$domain]"

            val description = sequenceOf(
                document.selectFirst("meta[property=og:description]")?.attr("content"),
                document.selectFirst("meta[name=description]")?.attr("content"),
                document.selectFirst("meta[name=twitter:description]")?.attr("content"),
                document.selectFirst("p")?.text()?.take(240)
            ).firstOrNull { !it.isNullOrBlank() }?.trim() ?: "Metadata indexed from $domain"

            val imageUrl = sequenceOf(
                document.selectFirst("meta[property=og:image:secure_url]")?.absUrl("content"),
                document.selectFirst("meta[property=og:image]")?.absUrl("content"),
                document.selectFirst("meta[property=og:image]")?.attr("content"),
                document.selectFirst("meta[name=twitter:image]")?.absUrl("content"),
                document.selectFirst("meta[name=twitter:image:src]")?.absUrl("content"),
                document.selectFirst("link[rel=image_src]")?.absUrl("href"),
                document.selectFirst("img[src]")?.absUrl("src")
            ).firstOrNull { !it.isNullOrBlank() }?.trim()

            val siteName = sequenceOf(
                document.selectFirst("meta[property=og:site_name]")?.attr("content"),
                document.selectFirst("meta[name=application-name]")?.attr("content"),
                domain
            ).firstOrNull { !it.isNullOrBlank() }?.trim() ?: domain

            val tags = AutoTagger.generateTags(
                url = cleanUrl,
                title = title,
                description = description,
                siteName = siteName
            )

            val dao = AppDatabase.getInstance(applicationContext).savedItemDao()
            val existing = dao.getItemByUrl(cleanUrl)
            if (existing != null) {
                val updatedItem = existing.copy(
                    title = title,
                    description = description,
                    imageUrl = imageUrl?.takeIf { it.startsWith("http") },
                    siteName = siteName,
                    tags = tags
                )
                dao.update(updatedItem)
                Log.i(TAG, "Successfully updated item #${existing.id}: $title")
            } else {
                val savedItem = SavedItem(
                    url = cleanUrl,
                    title = title,
                    description = description,
                    imageUrl = imageUrl?.takeIf { it.startsWith("http") },
                    siteName = siteName,
                    tags = tags,
                    timestamp = System.currentTimeMillis()
                )
                val insertedId = dao.insert(savedItem)
                Log.i(TAG, "Successfully inserted new item #$insertedId: $title")
            }

            Result.success()
        } catch (ioException: IOException) {
            Log.e(TAG, "Network IOException while scraping $cleanUrl", ioException)
            insertFallbackRecord(cleanUrl, domain)
            Result.success()
        } catch (sqliteException: SQLiteException) {
            Log.e(TAG, "Database SQLiteException while inserting scraped record for $cleanUrl", sqliteException)
            Result.failure()
        } catch (exception: Exception) {
            Log.e(TAG, "Unexpected exception while scraping $cleanUrl", exception)
            insertFallbackRecord(cleanUrl, domain)
            Result.success()
        }
    }

    private suspend fun insertFallbackRecord(cleanUrl: String, domain: String) {
        try {
            val dao = AppDatabase.getInstance(applicationContext).savedItemDao()
            val existing = dao.getItemByUrl(cleanUrl)
            if (existing == null) {
                val fallbackTitle = "Saved Link [$domain]"
                val fallbackDescription = "Offline bookmark captured for $cleanUrl"
                val tags = AutoTagger.generateTags(cleanUrl, fallbackTitle, fallbackDescription, domain)

                val fallbackItem = SavedItem(
                    url = cleanUrl,
                    title = fallbackTitle,
                    description = fallbackDescription,
                    imageUrl = null,
                    siteName = domain,
                    tags = tags,
                    timestamp = System.currentTimeMillis()
                )
                dao.insert(fallbackItem)
            }
        } catch (sqliteException: SQLiteException) {
            Log.e(TAG, "SQLiteException during fallback insert for $cleanUrl", sqliteException)
        }
    }

    private fun sanitizeUrl(url: String): String {
        val trimmed = url.trim()
        return if (!trimmed.startsWith("http://") && !trimmed.startsWith("https://")) {
            "https://$trimmed"
        } else {
            trimmed
        }
    }

    private fun extractDomain(url: String): String {
        return runCatching {
            val host = URI(url).host?.lowercase(Locale.ROOT) ?: ""
            if (host.startsWith("www.")) host.substring(4) else host
        }.getOrDefault("web")
    }
}

package com.kax.across

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.kax.across.data.local.AppDatabase
import com.kax.across.data.local.SavedItem
import com.kax.across.util.AutoTagger
import com.kax.across.worker.ScraperWorker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import java.net.URI
import java.util.Locale

/**
 * Transparent Activity interceptor registered for system-wide share sheet
 * ACTION_SEND, ACTION_SEND_MULTIPLE, and ACTION_VIEW intents.
 * Extracts target URLs from shared payloads, saves immediate database record,
 * enqueues scraping in WorkManager, and terminates instantly.
 */
class ShareActivity : Activity() {

    companion object {
        private const val TAG = "ShareActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        handleIncomingIntent(intent)
        finish()
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        handleIncomingIntent(intent)
        finish()
    }

    private fun handleIncomingIntent(intent: Intent?) {
        if (intent == null) return

        val extractedUrls = extractAllUrlsFromIntent(intent)
        if (extractedUrls.isNotEmpty()) {
            for (cleanUrl in extractedUrls) {
                saveInitialItem(cleanUrl)
                enqueueScrapeTask(cleanUrl)
            }

            val toastMessage = if (extractedUrls.size > 1) {
                "[ACROSS THE INTERNET] ${extractedUrls.size} LINKS SAVED"
            } else {
                val first = extractedUrls.first()
                val displayPreview = if (first.length > 38) first.take(35) + "..." else first
                "[ACROSS THE INTERNET] LINK SAVED // $displayPreview"
            }
            Toast.makeText(this, toastMessage, Toast.LENGTH_SHORT).show()
        } else if (Intent.ACTION_SEND == intent.action || Intent.ACTION_VIEW == intent.action) {
            Toast.makeText(
                this,
                "[ACROSS THE INTERNET] NO VALID URL FOUND IN SHARED LINK",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun saveInitialItem(targetUrl: String) {
        try {
            val domain = extractDomain(targetUrl)
            val dao = AppDatabase.getInstance(applicationContext).savedItemDao()

            runBlocking(Dispatchers.IO) {
                val existing = dao.getItemByUrl(targetUrl)
                if (existing == null) {
                    val initialTitle = "Saved Link [$domain]"
                    val initialDesc = "Bookmark captured for $targetUrl"
                    val tags = AutoTagger.generateTags(targetUrl, initialTitle, initialDesc, domain)

                    val item = SavedItem(
                        url = targetUrl,
                        title = initialTitle,
                        description = initialDesc,
                        siteName = domain,
                        tags = tags,
                        timestamp = System.currentTimeMillis()
                    )
                    dao.insert(item)
                    Log.i(TAG, "Immediately inserted initial item into database for $targetUrl")
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to insert initial item into database", e)
        }
    }

    private fun extractAllUrlsFromIntent(intent: Intent): List<String> {
        val foundUrls = LinkedHashSet<String>()

        val dataUriStr = intent.dataString ?: intent.data?.toString()
        if (!dataUriStr.isNullOrBlank()) {
            foundUrls.addAll(AutoTagger.extractAllUrlsFromText(dataUriStr))
        }

        val extraText = intent.getCharSequenceExtra(Intent.EXTRA_TEXT)?.toString()
        if (!extraText.isNullOrBlank()) {
            foundUrls.addAll(AutoTagger.extractAllUrlsFromText(extraText))
        }

        val extraSubject = intent.getCharSequenceExtra(Intent.EXTRA_SUBJECT)?.toString()
        if (!extraSubject.isNullOrBlank()) {
            foundUrls.addAll(AutoTagger.extractAllUrlsFromText(extraSubject))
        }

        intent.clipData?.let { clipData ->
            for (i in 0 until clipData.itemCount) {
                val clipText = clipData.getItemAt(i)?.text?.toString()
                    ?: clipData.getItemAt(i)?.uri?.toString()
                if (!clipText.isNullOrBlank()) {
                    foundUrls.addAll(AutoTagger.extractAllUrlsFromText(clipText))
                }
            }
        }

        try {
            @Suppress("DEPRECATION")
            val streamUri = intent.getParcelableExtra<Uri>(Intent.EXTRA_STREAM)
            if (streamUri != null) {
                foundUrls.addAll(AutoTagger.extractAllUrlsFromText(streamUri.toString()))
            }
        } catch (e: Exception) {
            Log.w(TAG, "Failed to read EXTRA_STREAM", e)
        }

        val stringList = intent.getStringArrayListExtra(Intent.EXTRA_TEXT)
        if (stringList != null) {
            for (textItem in stringList) {
                foundUrls.addAll(AutoTagger.extractAllUrlsFromText(textItem))
            }
        }

        return foundUrls.toList()
    }

    private fun extractDomain(url: String): String {
        return runCatching {
            val host = URI(url).host?.lowercase(Locale.ROOT) ?: ""
            if (host.startsWith("www.")) host.substring(4) else host
        }.getOrDefault("web")
    }

    private fun enqueueScrapeTask(targetUrl: String) {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val scrapeRequest = OneTimeWorkRequestBuilder<ScraperWorker>()
            .setInputData(workDataOf(ScraperWorker.KEY_URL to targetUrl))
            .setConstraints(constraints)
            .addTag(ScraperWorker.TAG)
            .build()

        WorkManager.getInstance(applicationContext).enqueue(scrapeRequest)
    }
}

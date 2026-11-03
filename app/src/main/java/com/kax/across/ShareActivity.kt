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
import java.util.regex.Pattern

/**
 * Transparent Activity interceptor registered for system-wide share sheet
 * ACTION_SEND, ACTION_SEND_MULTIPLE, and ACTION_VIEW intents.
 * Extracts target URLs from shared payloads, saves immediate database record,
 * enqueues scraping in WorkManager, and terminates instantly.
 */
class ShareActivity : Activity() {

    companion object {
        private const val TAG = "ShareActivity"

        // Regex pattern matching HTTP/HTTPS URLs embedded in shared text
        private val URL_REGEX: Pattern = Pattern.compile(
            """https?://[^\s<>"'{}|\\^`]+""",
            Pattern.CASE_INSENSITIVE
        )

        // Fallback pattern matching naked domains (e.g., "github.com/torvalds/linux")
        private val DOMAIN_REGEX: Pattern = Pattern.compile(
            """\b(?:[a-zA-Z0-9-]+\.)+(?:com|org|net|edu|gov|io|ai|dev|co|me|app|tv|so|xyz)(?:/[^\s<>"'{}|\\^`]*)?""",
            Pattern.CASE_INSENSITIVE
        )
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

        val extractedUrl = extractUrlFromIntent(intent)
        if (!extractedUrl.isNullOrBlank()) {
            val cleanUrl = sanitizeUrl(extractedUrl)
            saveInitialItem(cleanUrl)
            enqueueScrapeTask(cleanUrl)

            val displayPreview = if (cleanUrl.length > 38) {
                cleanUrl.take(35) + "..."
            } else {
                cleanUrl
            }
            Toast.makeText(
                this,
                "[ACROSS THE INTERNET] LINK SAVED // $displayPreview",
                Toast.LENGTH_SHORT
            ).show()
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

    private fun extractUrlFromIntent(intent: Intent): String? {
        // 1. Check direct Intent data URI (e.g., ACTION_VIEW or direct web intent)
        val dataUriStr = intent.dataString ?: intent.data?.toString()
        if (!dataUriStr.isNullOrBlank()) {
            val urlFromData = extractUrlFromText(dataUriStr)
            if (urlFromData != null) return urlFromData
        }

        // 2. Check EXTRA_TEXT (handles String, CharSequence, and SpannableString)
        val extraText = intent.getCharSequenceExtra(Intent.EXTRA_TEXT)?.toString()
        if (!extraText.isNullOrBlank()) {
            val urlFromText = extractUrlFromText(extraText)
            if (urlFromText != null) return urlFromText
        }

        // 3. Check EXTRA_SUBJECT (handles String and CharSequence)
        val extraSubject = intent.getCharSequenceExtra(Intent.EXTRA_SUBJECT)?.toString()
        if (!extraSubject.isNullOrBlank()) {
            val urlFromSubject = extractUrlFromText(extraSubject)
            if (urlFromSubject != null) return urlFromSubject
        }

        // 4. Check ClipData items (some apps attach URLs to clipData)
        intent.clipData?.let { clipData ->
            for (i in 0 until clipData.itemCount) {
                val clipText = clipData.getItemAt(i)?.text?.toString()
                    ?: clipData.getItemAt(i)?.uri?.toString()
                if (!clipText.isNullOrBlank()) {
                    val urlFromClip = extractUrlFromText(clipText)
                    if (urlFromClip != null) return urlFromClip
                }
            }
        }

        // 5. Check EXTRA_STREAM Uri if present
        try {
            @Suppress("DEPRECATION")
            val streamUri = intent.getParcelableExtra<Uri>(Intent.EXTRA_STREAM)
            if (streamUri != null) {
                val urlFromStream = extractUrlFromText(streamUri.toString())
                if (urlFromStream != null) return urlFromStream
            }
        } catch (e: Exception) {
            Log.w(TAG, "Failed to read EXTRA_STREAM", e)
        }

        // 6. Check ACTION_SEND_MULTIPLE text list
        val stringList = intent.getStringArrayListExtra(Intent.EXTRA_TEXT)
        if (stringList != null) {
            for (textItem in stringList) {
                val urlFromList = extractUrlFromText(textItem)
                if (urlFromList != null) return urlFromList
            }
        }

        return null
    }

    private fun extractUrlFromText(text: String): String? {
        val matcher = URL_REGEX.matcher(text)
        if (matcher.find()) {
            var raw = matcher.group(0) ?: return null
            while (raw.isNotEmpty() && (raw.endsWith(".") || raw.endsWith(",") || raw.endsWith(")") || raw.endsWith("!"))) {
                raw = raw.dropLast(1)
            }
            return raw
        }

        val domainMatcher = DOMAIN_REGEX.matcher(text)
        if (domainMatcher.find()) {
            var raw = domainMatcher.group(0) ?: return null
            while (raw.isNotEmpty() && (raw.endsWith(".") || raw.endsWith(",") || raw.endsWith(")") || raw.endsWith("!"))) {
                raw = raw.dropLast(1)
            }
            return "https://$raw"
        }

        return null
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

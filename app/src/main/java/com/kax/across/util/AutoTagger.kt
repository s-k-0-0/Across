package com.kax.across.util

import java.net.URI
import java.util.Locale

/**
 * Intelligent keyword-scanning utility that inspects URL domains,
 * page titles, OpenGraph descriptions, and inline hashtags to synthesize
 * standardized tactical taxonomy tags.
 */
object AutoTagger {

    private val domainRules = mapOf(
        "github.com" to listOf("#code", "#opensource", "#tech"),
        "gitlab.com" to listOf("#code", "#opensource"),
        "youtube.com" to listOf("#video", "#media"),
        "youtu.be" to listOf("#video", "#media"),
        "instagram.com" to listOf("#social", "#visual"),
        "reddit.com" to listOf("#community", "#discussion"),
        "x.com" to listOf("#social", "#news"),
        "twitter.com" to listOf("#social", "#news"),
        "tiktok.com" to listOf("#video", "#media"),
        "arxiv.org" to listOf("#science", "#research", "#ai"),
        "seriouseats.com" to listOf("#recipe", "#food"),
        "allrecipes.com" to listOf("#recipe", "#food"),
        "medium.com" to listOf("#article", "#reading"),
        "substack.com" to listOf("#newsletter", "#reading"),
        "spotify.com" to listOf("#music", "#audio"),
        "soundcloud.com" to listOf("#music", "#audio"),
        "figma.com" to listOf("#design", "#ui")
    )

    private val keywordTaxonomy = mapOf(
        "#ai" to listOf("ai", "artificial intelligence", "llm", "gemini", "gpt", "claude", "machine learning", "deep learning", "neural", "transformer", "diffusion"),
        "#tech" to listOf("technology", "tech", "android", "kotlin", "software", "developer", "hardware", "cyber", "linux", "cloud", "database", "security", "api"),
        "#code" to listOf("code", "programming", "github", "git", "python", "javascript", "typescript", "rust", "c++", "compiler", "algorithm", "source code"),
        "#android" to listOf("android", "kotlin", "jetpack", "compose", "apk", "google play"),
        "#recipe" to listOf("recipe", "ingredients", "cook", "cooking", "bake", "baking", "delicious", "kitchen", "dinner", "breakfast", "dessert", "roast", "crispy", "chef"),
        "#food" to listOf("food", "cuisine", "snack", "meal", "restaurant", "sauce", "pasta", "pizza", "flavor"),
        "#design" to listOf("design", "ui", "ux", "typography", "graphic", "branding", "figma", "aesthetic", "interface", "minimalist"),
        "#science" to listOf("science", "physics", "quantum", "space", "astronomy", "nasa", "biology", "chemistry", "research", "paper", "arxiv"),
        "#finance" to listOf("finance", "stock", "stocks", "invest", "investment", "crypto", "bitcoin", "ethereum", "economy", "market", "trading"),
        "#gaming" to listOf("gaming", "game", "games", "steam", "playstation", "xbox", "nintendo", "rpg", "esports", "gameplay"),
        "#music" to listOf("music", "song", "album", "band", "track", "spotify", "artist", "audio", "remix"),
        "#video" to listOf("video", "stream", "clip", "vlog", "watch", "youtube", "tiktok", "vimeo"),
        "#news" to listOf("news", "breaking", "politics", "report", "journalism", "article")
    )

    /**
     * Synthesizes a clean space-separated list of hashtag strings.
     */
    fun generateTags(
        url: String,
        title: String,
        description: String,
        siteName: String
    ): String {
        val detectedTags = LinkedHashSet<String>()

        // 1. Domain-level rules
        val host = runCatching {
            val h = URI(url).host?.lowercase(Locale.ROOT) ?: ""
            if (h.startsWith("www.")) h.substring(4) else h
        }.getOrDefault("")

        domainRules.entries.firstOrNull { host.contains(it.key) }?.value?.let {
            detectedTags.addAll(it)
        }

        // 2. Extract explicit hashtags in description or title
        val hashRegex = Regex("""#[a-zA-Z0-9_]+""")
        val textCorpus = "$url $title $description $siteName".lowercase(Locale.ROOT)
        hashRegex.findAll(textCorpus).forEach { match ->
            val tag = match.value.lowercase(Locale.ROOT)
            if (tag.length in 3..20) {
                detectedTags.add(tag)
            }
        }

        // 3. Keyword scan over full corpus
        for ((tag, keywords) in keywordTaxonomy) {
            for (keyword in keywords) {
                val pattern = Regex("""\b${Regex.escape(keyword)}\b""", RegexOption.IGNORE_CASE)
                if (pattern.containsMatchIn(textCorpus)) {
                    detectedTags.add(tag)
                    break
                }
            }
        }

        // Fallback default tag if nothing matched
        if (detectedTags.isEmpty()) {
            val fallbackDomainTag = if (host.isNotBlank()) "#${host.replace(".", "_")}" else "#archive"
            detectedTags.add(fallbackDomainTag)
            detectedTags.add("#bookmark")
        }

        // Cap to the top 4 most pertinent tags for compact tactical display
        return detectedTags.take(4).joinToString(separator = " ")
    }

    /**
     * Extracts ALL distinct URLs contained within a text payload.
     * Splitting multiple concatenated or space/newline-separated URLs into individual clean links.
     */
    fun extractAllUrlsFromText(text: String): List<String> {
        if (text.isBlank()) return emptyList()

        val urls = LinkedHashSet<String>()

        val urlMatcher = java.util.regex.Pattern.compile(
            """https?://[^\s<>"'{}|\\^`]+""",
            java.util.regex.Pattern.CASE_INSENSITIVE
        ).matcher(text)

        while (urlMatcher.find()) {
            var raw = urlMatcher.group(0) ?: continue
            while (raw.isNotEmpty() && (raw.endsWith(".") || raw.endsWith(",") || raw.endsWith(")") || raw.endsWith("!") || raw.endsWith(";"))) {
                raw = raw.dropLast(1)
            }
            val clean = sanitizeUrl(raw)
            if (clean.isNotBlank()) {
                urls.add(clean)
            }
        }

        if (urls.isEmpty()) {
            val domainMatcher = java.util.regex.Pattern.compile(
                """\b(?:[a-zA-Z0-9-]+\.)+(?:com|org|net|edu|gov|io|ai|dev|co|me|app|tv|so|xyz)(?:/[^\s<>"'{}|\\^`]*)?""",
                java.util.regex.Pattern.CASE_INSENSITIVE
            ).matcher(text)

            while (domainMatcher.find()) {
                var raw = domainMatcher.group(0) ?: continue
                while (raw.isNotEmpty() && (raw.endsWith(".") || raw.endsWith(",") || raw.endsWith(")") || raw.endsWith("!") || raw.endsWith(";"))) {
                    raw = raw.dropLast(1)
                }
                val clean = sanitizeUrl(raw)
                if (clean.isNotBlank()) {
                    urls.add(clean)
                }
            }
        }

        return urls.toList()
    }

    private fun sanitizeUrl(url: String): String {
        val trimmed = url.trim()
        return if (!trimmed.startsWith("http://") && !trimmed.startsWith("https://")) {
            "https://$trimmed"
        } else {
            trimmed
        }
    }
}

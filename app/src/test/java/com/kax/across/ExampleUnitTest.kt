package com.kax.across

import com.kax.across.util.AutoTagger
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {

    @Test
    fun autoTagger_detectsCodeTagsForGitHub() {
        val tags = AutoTagger.generateTags(
            url = "https://github.com/torvalds/linux",
            title = "Linux Kernel",
            description = "Linux kernel source tree",
            siteName = "GitHub"
        )
        assertTrue(tags.contains("#code") || tags.contains("#opensource") || tags.contains("#tech"))
    }

    @Test
    fun autoTagger_detectsRecipeTags() {
        val tags = AutoTagger.generateTags(
            url = "https://www.seriouseats.com/the-best-crispy-roast-potatoes-recipe",
            title = "The Best Crispy Roast Potatoes Recipe",
            description = "Boiled in alkaline water, tossed with infused oil, and baked until ultra crisp.",
            siteName = "Serious Eats"
        )
        assertTrue(tags.contains("#recipe") || tags.contains("#food"))
    }

    @Test
    fun autoTagger_detectsAiTags() {
        val tags = AutoTagger.generateTags(
            url = "https://arxiv.org/abs/1706.03762",
            title = "Attention Is All You Need",
            description = "We propose the Transformer, a model architecture eschewing recurrence and instead relying entirely on an attention mechanism.",
            siteName = "arXiv"
        )
        assertTrue(tags.contains("#ai") || tags.contains("#science"))
    }
}

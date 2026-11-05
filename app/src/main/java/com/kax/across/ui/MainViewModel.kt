package com.kax.across.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.kax.across.data.local.SavedItem
import com.kax.across.data.repository.SavedItemRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Locale

enum class VaultCategory(
    val title: String,
    val subtitle: String,
    val activeSegments: Int
) {
    ALL("ALL LINKS", "SAVED BOOKMARKS", 4),
    X_POSTS("X / POSTS", "POSTS & TWEETS", 2),
    YOUTUBE("YOUTUBE", "SAVED VIDEOS", 1),
    REDDIT("REDDIT", "SAVED THREADS", 3),
    GITHUB("GITHUB", "REPOSITORIES", 1),
    CHROME_WEB("CHROME & WEB", "FAMOUS DOMAINS", 2),
    ARTICLES("ARTICLES & READS", "OFFLINE READS", 3)
}

data class CategoryStats(
    val xPostsCount: Int = 0,
    val youtubeCount: Int = 0,
    val redditCount: Int = 0,
    val githubCount: Int = 0,
    val chromeCount: Int = 0,
    val articlesCount: Int = 0
)

data class MainUiState(
    val items: List<SavedItem> = emptyList(),
    val allItems: List<SavedItem> = emptyList(),
    val searchQuery: String = "",
    val selectedTag: String? = null,
    val selectedCategory: VaultCategory? = null,
    val isSearchModeActive: Boolean = false,
    val showFavoritesOnly: Boolean = false,
    val totalCount: Int = 0,
    val categoryStats: CategoryStats = CategoryStats(),
    val availableTags: List<String> = emptyList(),
    val isManualAddDialogOpen: Boolean = false,
    val isTutorialOpen: Boolean = false,
    val isReplayingTour: Boolean = false,
    val isReplayPromptOpen: Boolean = false,
    val selectedItemForDetail: SavedItem? = null,
    val itemPendingDelete: SavedItem? = null,
    val isClearAllConfirmationOpen: Boolean = false,
    val toastMessage: String? = null
)

@OptIn(ExperimentalCoroutinesApi::class)
class MainViewModel(
    private val repository: SavedItemRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _selectedTag = MutableStateFlow<String?>(null)
    val selectedTag = _selectedTag.asStateFlow()

    private val _selectedCategory = MutableStateFlow<VaultCategory?>(null)
    val selectedCategory = _selectedCategory.asStateFlow()

    private val _isSearchModeActive = MutableStateFlow(false)
    val isSearchModeActive = _isSearchModeActive.asStateFlow()

    private val _showFavoritesOnly = MutableStateFlow(false)
    val showFavoritesOnly = _showFavoritesOnly.asStateFlow()

    private val _isManualAddDialogOpen = MutableStateFlow(false)
    val isManualAddDialogOpen = _isManualAddDialogOpen.asStateFlow()

    // Only open tutorial automatically if the user has NOT completed or dismissed it before
    private val _isTutorialOpen = MutableStateFlow(!repository.isTutorialCompleted())
    val isTutorialOpen = _isTutorialOpen.asStateFlow()

    private val _isReplayingTour = MutableStateFlow(false)
    val isReplayingTour = _isReplayingTour.asStateFlow()

    private val _isReplayPromptOpen = MutableStateFlow(false)
    val isReplayPromptOpen = _isReplayPromptOpen.asStateFlow()

    private val _selectedItemForDetail = MutableStateFlow<SavedItem?>(null)
    val selectedItemForDetail = _selectedItemForDetail.asStateFlow()

    private val _itemPendingDelete = MutableStateFlow<SavedItem?>(null)
    val itemPendingDelete = _itemPendingDelete.asStateFlow()

    private val _isClearAllConfirmationOpen = MutableStateFlow(false)
    val isClearAllConfirmationOpen = _isClearAllConfirmationOpen.asStateFlow()

    private val _toastMessage = MutableStateFlow<String?>(null)
    val toastMessage = _toastMessage.asStateFlow()

    private data class FilterConfig(
        val query: String,
        val tag: String?,
        val category: VaultCategory?,
        val favsOnly: Boolean,
        val isSearchActive: Boolean
    )

    private val filterConfigFlow = combine(
        _searchQuery,
        _selectedTag,
        _selectedCategory,
        _showFavoritesOnly,
        _isSearchModeActive
    ) { query, tag, category, favsOnly, isSearchActive ->
        FilterConfig(query, tag, category, favsOnly, isSearchActive)
    }

    private data class OverlayConfig(
        val isDialogOpen: Boolean,
        val selectedDetail: SavedItem?,
        val toastMsg: String?,
        val isTutorialOpen: Boolean,
        val itemPendingDelete: SavedItem?,
        val isClearAllOpen: Boolean,
        val isReplayingTour: Boolean,
        val isReplayPromptOpen: Boolean
    )

    private val overlayConfigFlow = combine(
        _isManualAddDialogOpen,
        _selectedItemForDetail,
        _toastMessage,
        _isTutorialOpen,
        _itemPendingDelete,
        _isClearAllConfirmationOpen,
        _isReplayingTour,
        _isReplayPromptOpen
    ) { args ->
        @Suppress("UNCHECKED_CAST")
        OverlayConfig(
            isDialogOpen = args[0] as Boolean,
            selectedDetail = args[1] as SavedItem?,
            toastMsg = args[2] as String?,
            isTutorialOpen = args[3] as Boolean,
            itemPendingDelete = args[4] as SavedItem?,
            isClearAllOpen = args[5] as Boolean,
            isReplayingTour = args[6] as Boolean,
            isReplayPromptOpen = args[7] as Boolean
        )
    }

    private val filteredItemsFlow = filterConfigFlow.flatMapLatest { config ->
        val effectiveQuery = when {
            config.tag != null -> config.tag
            config.query.isNotBlank() -> config.query
            else -> ""
        }

        val baseFlow = if (effectiveQuery.isNotBlank()) {
            repository.search(effectiveQuery)
        } else if (config.favsOnly) {
            repository.getFavorites()
        } else {
            repository.getAllItems()
        }

        baseFlow.combine(filterConfigFlow) { items, activeFilter ->
            var list = items
            if (activeFilter.favsOnly && effectiveQuery.isNotBlank()) {
                list = list.filter { it.isFavorite }
            }
            val category = activeFilter.category
            if (category != null && category != VaultCategory.ALL) {
                list = list.filter { item -> matchesCategory(item, category) }
            }
            list
        }
    }

    val uiState: StateFlow<MainUiState> = combine(
        filteredItemsFlow,
        repository.getAllItems(),
        filterConfigFlow,
        overlayConfigFlow
    ) { filteredItems, allActiveItems, filterConfig, overlayConfig ->
        val allTags = allActiveItems.flatMap { it.tagList }.distinct().sorted()

        val xCount = allActiveItems.count { matchesCategory(it, VaultCategory.X_POSTS) }
        val ytCount = allActiveItems.count { matchesCategory(it, VaultCategory.YOUTUBE) }
        val redditCount = allActiveItems.count { matchesCategory(it, VaultCategory.REDDIT) }
        val ghCount = allActiveItems.count { matchesCategory(it, VaultCategory.GITHUB) }
        val chromeCount = allActiveItems.count { matchesCategory(it, VaultCategory.CHROME_WEB) }
        val articlesCount = allActiveItems.count { matchesCategory(it, VaultCategory.ARTICLES) }

        MainUiState(
            items = filteredItems,
            allItems = allActiveItems,
            searchQuery = filterConfig.query,
            selectedTag = filterConfig.tag,
            selectedCategory = filterConfig.category,
            isSearchModeActive = filterConfig.isSearchActive,
            showFavoritesOnly = filterConfig.favsOnly,
            totalCount = allActiveItems.size,
            categoryStats = CategoryStats(
                xPostsCount = xCount,
                youtubeCount = ytCount,
                redditCount = redditCount,
                githubCount = ghCount,
                chromeCount = chromeCount,
                articlesCount = articlesCount
            ),
            availableTags = allTags,
            isManualAddDialogOpen = overlayConfig.isDialogOpen,
            isTutorialOpen = overlayConfig.isTutorialOpen,
            isReplayingTour = overlayConfig.isReplayingTour,
            isReplayPromptOpen = overlayConfig.isReplayPromptOpen,
            selectedItemForDetail = overlayConfig.selectedDetail,
            itemPendingDelete = overlayConfig.itemPendingDelete,
            isClearAllConfirmationOpen = overlayConfig.isClearAllOpen,
            toastMessage = overlayConfig.toastMsg
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = MainUiState()
    )

    companion object {
        fun matchesCategory(item: SavedItem, category: VaultCategory): Boolean {
            val corpus = "${item.url} ${item.siteName} ${item.tags}".lowercase(Locale.ROOT)
            return when (category) {
                VaultCategory.ALL -> true
                VaultCategory.X_POSTS -> {
                    corpus.contains("x.com") ||
                        corpus.contains("twitter.com") ||
                        corpus.contains("#x") ||
                        corpus.contains("#posts") ||
                        item.siteName.equals("X", ignoreCase = true)
                }
                VaultCategory.YOUTUBE -> {
                    corpus.contains("youtube.com") ||
                        corpus.contains("youtu.be") ||
                        corpus.contains("#youtube") ||
                        corpus.contains("#video")
                }
                VaultCategory.REDDIT -> {
                    corpus.contains("reddit.com") ||
                        corpus.contains("#reddit") ||
                        corpus.contains("#community")
                }
                VaultCategory.GITHUB -> {
                    corpus.contains("github.com") ||
                        corpus.contains("gitlab.com") ||
                        corpus.contains("#github") ||
                        corpus.contains("#code")
                }
                VaultCategory.CHROME_WEB -> {
                    corpus.contains("google.com") ||
                        corpus.contains("wikipedia.org") ||
                        corpus.contains("stackoverflow.com") ||
                        corpus.contains("medium.com") ||
                        corpus.contains("substack.com") ||
                        corpus.contains("linkedin.com") ||
                        corpus.contains("chrome") ||
                        corpus.contains("#chrome") ||
                        corpus.contains("#web")
                }
                VaultCategory.ARTICLES -> {
                    !matchesCategory(item, VaultCategory.X_POSTS) &&
                        !matchesCategory(item, VaultCategory.YOUTUBE) &&
                        !matchesCategory(item, VaultCategory.REDDIT) &&
                        !matchesCategory(item, VaultCategory.GITHUB) &&
                        !matchesCategory(item, VaultCategory.CHROME_WEB)
                }
            }
        }
    }

    fun onSearchQueryChanged(newQuery: String) {
        _searchQuery.value = newQuery
        if (newQuery.isNotBlank()) {
            _isSearchModeActive.value = true
        }
    }

    fun onSelectCategory(category: VaultCategory?) {
        _selectedCategory.value = category
    }

    fun onSetSearchModeActive(active: Boolean) {
        _isSearchModeActive.value = active
        if (!active) {
            _searchQuery.value = ""
        }
    }

    fun onTagSelected(tag: String?) {
        if (_selectedTag.value == tag) {
            _selectedTag.value = null
        } else {
            _selectedTag.value = tag
            _searchQuery.value = ""
        }
    }

    fun onToggleFavoritesOnly() {
        _showFavoritesOnly.update { !it }
    }

    fun onToggleFavorite(item: SavedItem) {
        viewModelScope.launch {
            repository.setFavorite(item.id, !item.isFavorite)
        }
    }

    fun requestDeleteItem(item: SavedItem) {
        _itemPendingDelete.value = item
    }

    fun cancelDeleteItem() {
        _itemPendingDelete.value = null
    }

    fun confirmDeleteItem() {
        val item = _itemPendingDelete.value
        _itemPendingDelete.value = null
        if (item != null) {
            onDeleteItem(item)
        }
    }

    fun onDeleteItem(item: SavedItem) {
        viewModelScope.launch {
            repository.delete(item)
            if (_selectedItemForDetail.value?.id == item.id) {
                _selectedItemForDetail.value = null
            }
            _toastMessage.value = "Link removed"
        }
    }

    fun requestClearAll() {
        _isClearAllConfirmationOpen.value = true
    }

    fun cancelClearAll() {
        _isClearAllConfirmationOpen.value = false
    }

    fun confirmClearAll() {
        _isClearAllConfirmationOpen.value = false
        onClearAll()
    }

    fun onClearAll() {
        viewModelScope.launch {
            repository.deleteAll()
            _toastMessage.value = "All saved links cleared"
        }
    }

    fun openManualAddDialog() {
        _isManualAddDialogOpen.value = true
    }

    fun closeManualAddDialog() {
        _isManualAddDialogOpen.value = false
    }

    fun openTutorial() {
        onHelpClicked()
    }

    fun onHelpClicked() {
        if (repository.isTutorialCompleted()) {
            _isReplayPromptOpen.value = true
        } else {
            _isReplayingTour.value = false
            _isTutorialOpen.value = true
        }
    }

    fun confirmReplayTour() {
        _isReplayPromptOpen.value = false
        _isReplayingTour.value = true
        _isTutorialOpen.value = true
    }

    fun cancelReplayPrompt() {
        _isReplayPromptOpen.value = false
    }

    fun closeTutorial() {
        repository.setTutorialCompleted(true)
        _isTutorialOpen.value = false
        _isReplayingTour.value = false
    }

    fun startFreshTutorialChoice() {
        repository.setTutorialCompleted(true)
        _isTutorialOpen.value = false
        _isReplayingTour.value = false
        viewModelScope.launch {
            repository.deleteAll()
            _toastMessage.value = "Cleared sample links! Ready for your bookmarks."
        }
    }

    fun keepDemoTutorialChoice() {
        repository.setTutorialCompleted(true)
        _isTutorialOpen.value = false
        _isReplayingTour.value = false
        _toastMessage.value = "Sample links kept. Start sharing or saving!"
    }

    fun selectItemForDetail(item: SavedItem?) {
        _selectedItemForDetail.value = item
    }

    fun clearToastMessage() {
        _toastMessage.value = null
    }

    fun enqueueTargetUrl(rawText: String) {
        if (rawText.isBlank()) return
        val urls = com.kax.across.util.AutoTagger.extractAllUrlsFromText(rawText)
        if (urls.isEmpty()) return

        for (url in urls) {
            repository.enqueueScrape(url)
        }

        _toastMessage.value = if (urls.size > 1) {
            "Saved ${urls.size} links! Fetching details..."
        } else {
            "Saving link & fetching details..."
        }
        closeManualAddDialog()
    }

    fun injectQuickPreset(url: String) {
        repository.enqueueScrape(url)
        _toastMessage.value = "Saving sample link..."
    }
}

class MainViewModelFactory(
    private val repository: SavedItemRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            return MainViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}

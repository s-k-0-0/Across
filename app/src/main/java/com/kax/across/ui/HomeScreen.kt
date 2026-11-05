package com.kax.across.ui

import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.DeleteSweep
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kax.across.ui.components.AppTopBar
import com.kax.across.ui.components.AppTutorialDialog
import com.kax.across.ui.components.ArticlesReadsHorizontalCard
import com.kax.across.ui.components.CategorySquareTile
import com.kax.across.ui.components.ItemDetailDialog
import com.kax.across.ui.components.ManualUrlDialog
import com.kax.across.ui.components.SavedItemCard
import com.kax.across.ui.components.SavedLinksHeroCard
import com.kax.across.ui.components.VaultsAndSharesActionRow
import com.kax.across.ui.theme.CardBorderSubtle
import com.kax.across.ui.theme.CardSurface
import com.kax.across.ui.theme.IconCircleSurface
import com.kax.across.ui.theme.ObsidianBackground
import com.kax.across.ui.theme.PureBlack
import com.kax.across.ui.theme.PureWhite
import com.kax.across.ui.theme.SecondaryMutedText
import com.kax.across.ui.theme.SpaceGroteskFamily
import com.kax.across.ui.theme.SpaceMonoFamily
import java.util.Locale

@Composable
fun HomeScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    val isVaultSheetOpen = uiState.isSearchModeActive || uiState.selectedCategory != null

    BackHandler(enabled = isVaultSheetOpen) {
        viewModel.onSelectCategory(null)
        viewModel.onSetSearchModeActive(false)
    }

    LaunchedEffect(uiState.toastMessage) {
        uiState.toastMessage?.let { msg ->
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
            viewModel.clearToastMessage()
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = ObsidianBackground,
        contentWindowInsets = WindowInsets.safeDrawing
    ) { innerPadding ->
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(ObsidianBackground)
        ) {
            val isFoldableUnfolded = maxWidth >= 600.dp

            if (isFoldableUnfolded) {
                FoldableDualPaneDashboard(
                    uiState = uiState,
                    onSearchClick = { viewModel.onSetSearchModeActive(true) },
                    onQueryChange = { viewModel.onSearchQueryChanged(it) },
                    onSaveClick = { viewModel.openManualAddDialog() },
                    onHelpClick = { viewModel.openTutorial() },
                    onSelectCategory = { category ->
                        if (uiState.selectedCategory == category) {
                            viewModel.onSelectCategory(null)
                        } else {
                            viewModel.onSelectCategory(category)
                        }
                    },
                    onShareExport = { shareVaultSummary(context, uiState.totalCount) },
                    onToggleItemFavorite = { viewModel.onToggleFavorite(it) },
                    onDeleteItem = { viewModel.requestDeleteItem(it) },
                    onSelectItemDetail = { viewModel.selectItemForDetail(it) },
                    onTagClick = { tag -> viewModel.onTagSelected(tag) },
                    onClearAll = { viewModel.requestClearAll() }
                )
            } else {
                PhoneCompactDashboard(
                    uiState = uiState,
                    isVaultSheetOpen = isVaultSheetOpen,
                    onCloseVaultSheet = {
                        viewModel.onSelectCategory(null)
                        viewModel.onSetSearchModeActive(false)
                    },
                    onSearchClick = {
                        viewModel.onSelectCategory(VaultCategory.ALL)
                        viewModel.onSetSearchModeActive(true)
                    },
                    onQueryChange = { viewModel.onSearchQueryChanged(it) },
                    onSaveClick = { viewModel.openManualAddDialog() },
                    onHelpClick = { viewModel.openTutorial() },
                    onSelectCategory = { category ->
                        viewModel.onSelectCategory(category)
                    },
                    onShareExport = { shareVaultSummary(context, uiState.totalCount) },
                    onToggleItemFavorite = { viewModel.onToggleFavorite(it) },
                    onDeleteItem = { viewModel.requestDeleteItem(it) },
                    onSelectItemDetail = { viewModel.selectItemForDetail(it) },
                    onTagClick = { tag -> viewModel.onTagSelected(tag) },
                    onClearAll = { viewModel.requestClearAll() }
                )
            }
        }
    }

    // App Feature Guide & Tutorial Dialog
    if (uiState.isTutorialOpen) {
        AppTutorialDialog(
            isReplayMode = uiState.isReplayingTour,
            onDismiss = {
                viewModel.onSelectCategory(null)
                viewModel.onSetSearchModeActive(false)
                viewModel.closeTutorial()
            },
            onStartFresh = {
                viewModel.onSelectCategory(null)
                viewModel.onSetSearchModeActive(false)
                viewModel.startFreshTutorialChoice()
            },
            onKeepDemo = {
                viewModel.onSelectCategory(null)
                viewModel.onSetSearchModeActive(false)
                viewModel.keepDemoTutorialChoice()
            },
            onPageChange = { pageIndex ->
                // Page 2 & 3: Live scene transition to Vault & Search screen
                if (pageIndex == 2 || pageIndex == 3) {
                    viewModel.onSelectCategory(VaultCategory.ALL)
                } else {
                    viewModel.onSelectCategory(null)
                    viewModel.onSetSearchModeActive(false)
                }
            }
        )
    }

    // Replay Tour Confirmation Dialog for users who already completed onboarding
    if (uiState.isReplayPromptOpen) {
        com.kax.across.ui.components.ReplayTourConfirmationDialog(
            onConfirm = { viewModel.confirmReplayTour() },
            onDismiss = { viewModel.cancelReplayPrompt() }
        )
    }

    // Manual URL Paste / Entry Dialog
    if (uiState.isManualAddDialogOpen) {
        ManualUrlDialog(
            onDismiss = { viewModel.closeManualAddDialog() },
            onSubmit = { url -> viewModel.enqueueTargetUrl(url) }
        )
    }

    // Link Metadata Detail Inspector Dialog
    uiState.selectedItemForDetail?.let { selectedItem ->
        ItemDetailDialog(
            item = selectedItem,
            onDismiss = { viewModel.selectItemForDetail(null) },
            onRescrape = { url -> viewModel.enqueueTargetUrl(url) }
        )
    }

    // Single Link Delete Confirmation Dialog
    uiState.itemPendingDelete?.let { pendingItem ->
        com.kax.across.ui.components.SingleDeleteConfirmationDialog(
            item = pendingItem,
            onConfirm = { viewModel.confirmDeleteItem() },
            onDismiss = { viewModel.cancelDeleteItem() }
        )
    }

    // Clear All Saved Links Confirmation Dialog
    if (uiState.isClearAllConfirmationOpen) {
        com.kax.across.ui.components.ClearAllConfirmationDialog(
            totalCount = uiState.totalCount,
            onConfirm = { viewModel.confirmClearAll() },
            onDismiss = { viewModel.cancelClearAll() }
        )
    }
}

/**
 * Compact Phone Dashboard UI
 */
@Composable
private fun PhoneCompactDashboard(
    uiState: MainUiState,
    isVaultSheetOpen: Boolean,
    onCloseVaultSheet: () -> Unit,
    onSearchClick: () -> Unit,
    onQueryChange: (String) -> Unit,
    onSaveClick: () -> Unit,
    onHelpClick: () -> Unit,
    onSelectCategory: (VaultCategory) -> Unit,
    onShareExport: () -> Unit,
    onToggleItemFavorite: (com.kax.across.data.local.SavedItem) -> Unit,
    onDeleteItem: (com.kax.across.data.local.SavedItem) -> Unit,
    onSelectItemDetail: (com.kax.across.data.local.SavedItem) -> Unit,
    onTagClick: (String) -> Unit,
    onClearAll: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        if (!isVaultSheetOpen) {
            val scrollState = rememberScrollState()
            Column(
                modifier = Modifier
                    .widthIn(max = 440.dp)
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(horizontal = 18.dp)
                    .padding(bottom = 28.dp)
                    .testTag("dashboard_list"),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                AppTopBar(
                    title = "ACROSS THE INTERNET",
                    onGridActionClick = { onSelectCategory(VaultCategory.ALL) },
                    onHelpClick = onHelpClick
                )

                SavedLinksHeroCard(
                    totalCount = uiState.totalCount,
                    onSearchClick = onSearchClick,
                    onSaveClick = onSaveClick
                )

                // 2x2 Category Grid
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    CategorySquareTile(
                        category = VaultCategory.X_POSTS,
                        count = uiState.categoryStats.xPostsCount,
                        isSelected = uiState.selectedCategory == VaultCategory.X_POSTS,
                        onClick = { onSelectCategory(VaultCategory.X_POSTS) },
                        modifier = Modifier.weight(1f)
                    )

                    CategorySquareTile(
                        category = VaultCategory.YOUTUBE,
                        count = uiState.categoryStats.youtubeCount,
                        isSelected = uiState.selectedCategory == VaultCategory.YOUTUBE,
                        onClick = { onSelectCategory(VaultCategory.YOUTUBE) },
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    CategorySquareTile(
                        category = VaultCategory.REDDIT,
                        count = uiState.categoryStats.redditCount,
                        isSelected = uiState.selectedCategory == VaultCategory.REDDIT,
                        onClick = { onSelectCategory(VaultCategory.REDDIT) },
                        modifier = Modifier.weight(1f)
                    )

                    CategorySquareTile(
                        category = VaultCategory.GITHUB,
                        count = uiState.categoryStats.githubCount,
                        isSelected = uiState.selectedCategory == VaultCategory.GITHUB,
                        onClick = { onSelectCategory(VaultCategory.GITHUB) },
                        modifier = Modifier.weight(1f)
                    )
                }

                ArticlesReadsHorizontalCard(
                    count = uiState.categoryStats.articlesCount,
                    isSelected = uiState.selectedCategory == VaultCategory.ARTICLES,
                    onClick = { onSelectCategory(VaultCategory.ARTICLES) }
                )

                VaultsAndSharesActionRow(
                    onOpenVaultsClick = { onSelectCategory(VaultCategory.ALL) },
                    onShareExportClick = onShareExport
                )
            }
        } else {
            VaultListSheetContent(
                uiState = uiState,
                showBackButton = true,
                onBackClick = onCloseVaultSheet,
                onQueryChange = onQueryChange,
                onSelectCategory = onSelectCategory,
                onToggleItemFavorite = onToggleItemFavorite,
                onDeleteItem = onDeleteItem,
                onSelectItemDetail = onSelectItemDetail,
                onTagClick = onTagClick,
                onClearAll = onClearAll,
                onAddClick = onSaveClick,
                modifier = Modifier
                    .widthIn(max = 460.dp)
                    .fillMaxSize()
                    .padding(horizontal = 20.dp)
            )
        }
    }
}

/**
 * Unfolded Foldable Dual-Pane Dashboard Layout
 */
@Composable
private fun FoldableDualPaneDashboard(
    uiState: MainUiState,
    onSearchClick: () -> Unit,
    onQueryChange: (String) -> Unit,
    onSaveClick: () -> Unit,
    onHelpClick: () -> Unit,
    onSelectCategory: (VaultCategory) -> Unit,
    onShareExport: () -> Unit,
    onToggleItemFavorite: (com.kax.across.data.local.SavedItem) -> Unit,
    onDeleteItem: (com.kax.across.data.local.SavedItem) -> Unit,
    onSelectItemDetail: (com.kax.across.data.local.SavedItem) -> Unit,
    onTagClick: (String) -> Unit,
    onClearAll: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp, vertical = 12.dp)
            .testTag("foldable_dual_pane"),
        horizontalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        val leftScroll = rememberScrollState()
        Column(
            modifier = Modifier
                .weight(0.46f)
                .fillMaxHeight()
                .verticalScroll(leftScroll),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            AppTopBar(
                title = "ACROSS THE INTERNET",
                onGridActionClick = { onSelectCategory(VaultCategory.ALL) },
                onHelpClick = onHelpClick
            )

            SavedLinksHeroCard(
                totalCount = uiState.totalCount,
                onSearchClick = onSearchClick,
                onSaveClick = onSaveClick
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                CategorySquareTile(
                    category = VaultCategory.X_POSTS,
                    count = uiState.categoryStats.xPostsCount,
                    isSelected = uiState.selectedCategory == VaultCategory.X_POSTS,
                    onClick = { onSelectCategory(VaultCategory.X_POSTS) },
                    modifier = Modifier.weight(1f)
                )

                CategorySquareTile(
                    category = VaultCategory.YOUTUBE,
                    count = uiState.categoryStats.youtubeCount,
                    isSelected = uiState.selectedCategory == VaultCategory.YOUTUBE,
                    onClick = { onSelectCategory(VaultCategory.YOUTUBE) },
                    modifier = Modifier.weight(1f)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                CategorySquareTile(
                    category = VaultCategory.REDDIT,
                    count = uiState.categoryStats.redditCount,
                    isSelected = uiState.selectedCategory == VaultCategory.REDDIT,
                    onClick = { onSelectCategory(VaultCategory.REDDIT) },
                    modifier = Modifier.weight(1f)
                )

                CategorySquareTile(
                    category = VaultCategory.GITHUB,
                    count = uiState.categoryStats.githubCount,
                    isSelected = uiState.selectedCategory == VaultCategory.GITHUB,
                    onClick = { onSelectCategory(VaultCategory.GITHUB) },
                    modifier = Modifier.weight(1f)
                )
            }

            ArticlesReadsHorizontalCard(
                count = uiState.categoryStats.articlesCount,
                isSelected = uiState.selectedCategory == VaultCategory.ARTICLES,
                onClick = { onSelectCategory(VaultCategory.ARTICLES) }
            )

            VaultsAndSharesActionRow(
                onOpenVaultsClick = { onSelectCategory(VaultCategory.ALL) },
                onShareExportClick = onShareExport
            )
        }

        VaultListSheetContent(
            uiState = uiState,
            showBackButton = false,
            onBackClick = {},
            onQueryChange = onQueryChange,
            onSelectCategory = onSelectCategory,
            onToggleItemFavorite = onToggleItemFavorite,
            onDeleteItem = onDeleteItem,
            onSelectItemDetail = onSelectItemDetail,
            onTagClick = onTagClick,
            onClearAll = onClearAll,
            onAddClick = onSaveClick,
            modifier = Modifier
                .weight(0.54f)
                .fillMaxHeight()
        )
    }
}

@Composable
private fun VaultListSheetContent(
    uiState: MainUiState,
    showBackButton: Boolean,
    onBackClick: () -> Unit,
    onQueryChange: (String) -> Unit,
    onSelectCategory: (VaultCategory) -> Unit,
    onToggleItemFavorite: (com.kax.across.data.local.SavedItem) -> Unit,
    onDeleteItem: (com.kax.across.data.local.SavedItem) -> Unit,
    onSelectItemDetail: (com.kax.across.data.local.SavedItem) -> Unit,
    onTagClick: (String) -> Unit,
    onClearAll: () -> Unit,
    onAddClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (showBackButton) {
                    IconButton(
                        onClick = onBackClick,
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(CardSurface)
                            .border(1.dp, CardBorderSubtle, CircleShape)
                            .testTag("vault_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back to Dashboard",
                            tint = PureWhite,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                }

                Column {
                    Text(
                        text = (uiState.selectedCategory?.title ?: "SAVED LINKS").uppercase(Locale.US),
                        fontFamily = SpaceGroteskFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = PureWhite
                    )
                    Text(
                        text = "${uiState.items.size} LINKS",
                        fontFamily = SpaceMonoFamily,
                        fontSize = 10.sp,
                        letterSpacing = 1.4.sp,
                        color = SecondaryMutedText
                    )
                }
            }

            if (uiState.totalCount > 0) {
                IconButton(
                    onClick = onClearAll,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(CardSurface)
                        .border(1.dp, CardBorderSubtle, CircleShape)
                        .testTag("clear_all_button")
                ) {
                    Icon(
                        imageVector = Icons.Outlined.DeleteSweep,
                        contentDescription = "Clear All Links",
                        tint = SecondaryMutedText,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Search Field
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(999.dp))
                .background(CardSurface)
                .border(1.dp, CardBorderSubtle, RoundedCornerShape(999.dp))
                .padding(horizontal = 18.dp, vertical = 13.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Outlined.Search,
                contentDescription = "Search",
                tint = SecondaryMutedText,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Box(modifier = Modifier.weight(1f)) {
                if (uiState.searchQuery.isEmpty()) {
                    Text(
                        text = "SEARCH OR FILTER TAGS...",
                        fontFamily = SpaceMonoFamily,
                        fontSize = 11.sp,
                        letterSpacing = 1.2.sp,
                        color = SecondaryMutedText
                    )
                }
                BasicTextField(
                    value = uiState.searchQuery,
                    onValueChange = onQueryChange,
                    textStyle = MaterialTheme.typography.bodyMedium.copy(
                        color = PureWhite,
                        fontFamily = SpaceMonoFamily
                    ),
                    cursorBrush = SolidColor(PureWhite),
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("search_input")
                )
            }
            if (uiState.searchQuery.isNotEmpty()) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Clear Search",
                    tint = PureWhite,
                    modifier = Modifier
                        .size(18.dp)
                        .clickable { onQueryChange("") }
                        .testTag("search_clear_button")
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Horizontally Scrollable Category Filter Chips
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 2.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(VaultCategory.entries, key = { it.name }) { category ->
                val isSelected = (uiState.selectedCategory ?: VaultCategory.ALL) == category
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(999.dp))
                        .background(if (isSelected) PureWhite else CardSurface)
                        .border(1.dp, CardBorderSubtle, RoundedCornerShape(999.dp))
                        .clickable { onSelectCategory(category) }
                        .padding(horizontal = 14.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = category.title,
                        fontFamily = SpaceMonoFamily,
                        fontSize = 10.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSelected) PureBlack else SecondaryMutedText
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        if (uiState.items.isEmpty()) {
            EmptyVaultState(
                searchQuery = uiState.searchQuery,
                onOpenAdd = onAddClick
            )
        } else {
            val listState = rememberLazyListState()
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("vault_items_list"),
                contentPadding = PaddingValues(bottom = 28.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                items(
                    items = uiState.items,
                    key = { it.id }
                ) { item ->
                    SavedItemCard(
                        item = item,
                        onToggleFavorite = onToggleItemFavorite,
                        onDelete = onDeleteItem,
                        onClickDetail = onSelectItemDetail,
                        onTagClick = onTagClick
                    )
                }
            }
        }
    }
}

@Composable
private fun EmptyVaultState(
    searchQuery: String,
    onOpenAdd: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(26.dp))
            .background(CardSurface)
            .border(1.dp, CardBorderSubtle, RoundedCornerShape(26.dp))
            .padding(28.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(IconCircleSurface),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Outlined.BookmarkBorder,
                contentDescription = null,
                tint = PureWhite,
                modifier = Modifier.size(22.dp)
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        Text(
            text = if (searchQuery.isNotBlank()) "NO MATCHING LINKS" else "NO SAVED LINKS YET",
            fontFamily = SpaceGroteskFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            color = PureWhite
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = if (searchQuery.isNotBlank()) {
                "No saved items matched '$searchQuery'."
            } else {
                "Share links from any app via Android Share Sheet or tap + SAVE."
            },
            fontFamily = SpaceMonoFamily,
            fontSize = 11.sp,
            color = SecondaryMutedText
        )

        Spacer(modifier = Modifier.height(18.dp))

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(999.dp))
                .background(PureWhite)
                .clickable { onOpenAdd() }
                .padding(horizontal = 20.dp, vertical = 12.dp)
                .testTag("empty_state_add_button")
        ) {
            Text(
                text = "+ SAVE LINK",
                fontFamily = SpaceGroteskFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                letterSpacing = 1.sp,
                color = PureBlack
            )
        }
    }
}

private fun shareVaultSummary(context: Context, totalCount: Int) {
    try {
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(
                Intent.EXTRA_TEXT,
                "Across The Internet Bookmark Collection: $totalCount links saved."
            )
        }
        context.startActivity(Intent.createChooser(shareIntent, "Share Collection"))
    } catch (e: Exception) {
        Log.e("HomeScreen", "Failed to share collection summary", e)
    }
}

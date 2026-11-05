package com.kax.across.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.HelpOutline
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.DeleteSweep
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.kax.across.ui.theme.CardBorderActive
import com.kax.across.ui.theme.CardBorderSubtle
import com.kax.across.ui.theme.CardSurface
import com.kax.across.ui.theme.IconCircleSurface
import com.kax.across.ui.theme.PillSurface
import com.kax.across.ui.theme.PureBlack
import com.kax.across.ui.theme.PureWhite
import com.kax.across.ui.theme.SecondaryMutedText
import com.kax.across.ui.theme.SpaceGroteskFamily
import com.kax.across.ui.theme.SpaceMonoFamily

import androidx.compose.material.icons.outlined.Share

private data class TutorialPage(
    val title: String,
    val subtitle: String,
    val description: String,
    val icon: ImageVector,
    val badge: String
)

private val tutorialPages = listOf(
    TutorialPage(
        title = "Welcome to Across The Internet",
        subtitle = "YOUR BOOKMARK VAULT",
        description = "Easily save, organize, and search your favorite web links in one place. Your links are automatically categorized by platform with titles, images, and tags.",
        icon = Icons.Outlined.Folder,
        badge = "1. WELCOME"
    ),
    TutorialPage(
        title = "Save Links From Any App",
        subtitle = "ANDROID SHARE SHEET",
        description = "Share links directly from YouTube, X, Reddit, Chrome, Instagram, TikTok, or any browser using the standard Share menu. Links are saved instantly in the background.",
        icon = Icons.Outlined.Share,
        badge = "2. SHARE & SAVE"
    ),
    TutorialPage(
        title = "Categories & Fast Search",
        subtitle = "SEARCH & FILTER",
        description = "Filter by platform categories (X, YouTube, Reddit, GitHub, Chrome, Articles) or search keywords, URLs, and hashtags to find any link instantly.",
        icon = Icons.Outlined.Search,
        badge = "3. SEARCH & FILTER"
    ),
    TutorialPage(
        title = "Link Actions & Safe Delete",
        subtitle = "EASY MANAGEMENT",
        description = "Tap any link card to open, copy, or star your favorites. Tapping Trash or Clear All displays an 'Are you sure?' safety prompt before deleting.",
        icon = Icons.Outlined.DeleteSweep,
        badge = "4. LINK MANAGEMENT"
    ),
    TutorialPage(
        title = "Ready to Get Started?",
        subtitle = "INITIAL SETUP",
        description = "We included sample links so you could explore all features. Choose 'START FRESH' to clear them for your bookmarks, or 'KEEP SAMPLES' to explore.",
        icon = Icons.Outlined.BookmarkBorder,
        badge = "5. GET STARTED"
    )
)

@Composable
fun AppTutorialDialog(
    onDismiss: () -> Unit,
    onStartFresh: () -> Unit,
    onKeepDemo: () -> Unit,
    onPageChange: (Int) -> Unit = {},
    isReplayMode: Boolean = false
) {
    var currentPageIndex by remember { mutableIntStateOf(0) }
    val pagesList = if (isReplayMode) tutorialPages.take(4) else tutorialPages
    val currentPage = pagesList[currentPageIndex]
    val isLastPage = currentPageIndex == pagesList.lastIndex

    LaunchedEffect(currentPageIndex) {
        onPageChange(currentPageIndex)
    }

    Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(28.dp))
                .background(CardSurface)
                .border(1.dp, CardBorderActive, RoundedCornerShape(28.dp))
                .padding(22.dp)
                .testTag("app_tutorial_dialog")
        ) {
            // Header Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(999.dp))
                        .background(PillSurface)
                        .border(1.dp, CardBorderSubtle, RoundedCornerShape(999.dp))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = currentPage.badge,
                        fontFamily = SpaceMonoFamily,
                        fontSize = 10.sp,
                        letterSpacing = 1.2.sp,
                        color = PureWhite
                    )
                }

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(IconCircleSurface)
                        .testTag("tutorial_close_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close Guide",
                        tint = PureWhite,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Animated Slide Content
            AnimatedContent(
                targetState = currentPage,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "tutorial_slide"
            ) { page ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(IconCircleSurface)
                            .border(1.dp, CardBorderSubtle, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = page.icon,
                            contentDescription = null,
                            tint = PureWhite,
                            modifier = Modifier.size(30.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = page.subtitle,
                        fontFamily = SpaceMonoFamily,
                        fontSize = 11.sp,
                        letterSpacing = 1.8.sp,
                        color = SecondaryMutedText,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = page.title,
                        fontFamily = SpaceGroteskFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp,
                        color = PureWhite,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = page.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = SecondaryMutedText,
                        textAlign = TextAlign.Center,
                        lineHeight = 20.sp,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Step Indicator Dots
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                pagesList.indices.forEach { index ->
                    val isActive = index == currentPageIndex
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 3.dp)
                            .size(width = if (isActive) 18.dp else 6.dp, height = 6.dp)
                            .clip(RoundedCornerShape(999.dp))
                            .background(if (isActive) PureWhite else SecondaryMutedText)
                    )
                }
            }

            Spacer(modifier = Modifier.height(22.dp))

            // Action Buttons
            if (!isLastPage) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(999.dp))
                            .background(PillSurface)
                            .border(1.dp, CardBorderSubtle, RoundedCornerShape(999.dp))
                            .clickable { onDismiss() }
                            .padding(vertical = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "SKIP",
                            fontFamily = SpaceGroteskFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            letterSpacing = 1.sp,
                            color = SecondaryMutedText
                        )
                    }

                    Box(
                        modifier = Modifier
                            .weight(1.5f)
                            .clip(RoundedCornerShape(999.dp))
                            .background(PureWhite)
                            .clickable { currentPageIndex++ }
                            .padding(vertical = 12.dp)
                            .testTag("tutorial_next_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "NEXT",
                            fontFamily = SpaceGroteskFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            letterSpacing = 1.sp,
                            color = PureBlack
                        )
                    }
                }
            } else if (isReplayMode) {
                // Replay Mode Finish Button
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(999.dp))
                        .background(PureWhite)
                        .clickable { onDismiss() }
                        .padding(vertical = 14.dp)
                        .testTag("tutorial_finish_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "GOT IT! FINISH TOUR",
                        fontFamily = SpaceGroteskFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        letterSpacing = 1.sp,
                        color = PureBlack
                    )
                }
            } else {
                // Initial Setup: Fresh Start vs Keep Samples
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(999.dp))
                            .background(PureWhite)
                            .clickable { onStartFresh() }
                            .padding(vertical = 14.dp)
                            .testTag("tutorial_start_fresh_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "START FRESH (CLEAR SAMPLES)",
                            fontFamily = SpaceGroteskFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            letterSpacing = 1.sp,
                            color = PureBlack
                        )
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(999.dp))
                            .background(PillSurface)
                            .border(1.dp, CardBorderSubtle, RoundedCornerShape(999.dp))
                            .clickable { onKeepDemo() }
                            .padding(vertical = 14.dp)
                            .testTag("tutorial_keep_demo_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "KEEP SAMPLE LINKS",
                            fontFamily = SpaceGroteskFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            letterSpacing = 1.sp,
                            color = PureWhite
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ReplayTourConfirmationDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(28.dp))
                .background(CardSurface)
                .border(1.dp, CardBorderActive, RoundedCornerShape(28.dp))
                .padding(22.dp)
                .testTag("replay_tour_dialog")
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(999.dp))
                        .background(PillSurface)
                        .border(1.dp, CardBorderSubtle, RoundedCornerShape(999.dp))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "APP FEATURE TOUR",
                        fontFamily = SpaceMonoFamily,
                        fontSize = 10.sp,
                        letterSpacing = 1.2.sp,
                        color = PureWhite
                    )
                }

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(IconCircleSurface)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Cancel",
                        tint = PureWhite,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(IconCircleSurface)
                        .border(1.dp, CardBorderSubtle, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.HelpOutline,
                        contentDescription = null,
                        tint = PureWhite,
                        modifier = Modifier.size(26.dp)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "VIEW FEATURE TOUR?",
                    fontFamily = SpaceGroteskFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = PureWhite,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Would you like to view the guided tour highlighting Across The Internet's controls and features?",
                    style = MaterialTheme.typography.bodyMedium,
                    color = SecondaryMutedText,
                    textAlign = TextAlign.Center,
                    lineHeight = 20.sp
                )
            }

            Spacer(modifier = Modifier.height(22.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(999.dp))
                        .background(PillSurface)
                        .border(1.dp, CardBorderSubtle, RoundedCornerShape(999.dp))
                        .clickable { onDismiss() }
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "CANCEL",
                        fontFamily = SpaceGroteskFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        letterSpacing = 1.sp,
                        color = PureWhite
                    )
                }

                Box(
                    modifier = Modifier
                        .weight(1.2f)
                        .clip(RoundedCornerShape(999.dp))
                        .background(PureWhite)
                        .clickable { onConfirm() }
                        .padding(vertical = 12.dp)
                        .testTag("confirm_replay_tour_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "VIEW TOUR",
                        fontFamily = SpaceGroteskFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        letterSpacing = 1.sp,
                        color = PureBlack
                    )
                }
            }
        }
    }
}

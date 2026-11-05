package com.kax.across.ui.components

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
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

enum class PointerDirection {
    UP,
    DOWN,
    LEFT,
    RIGHT
}

data class GuideStepData(
    val stepTitle: String,
    val targetName: String,
    val description: String,
    val pointerDirection: PointerDirection,
    val badge: String
)

val guideStepsList = listOf(
    GuideStepData(
        stepTitle = "App Guide Icon",
        targetName = "HELP & GUIDE BUTTON (?)",
        description = "Tap this Guide button anytime to view features or replay this guided tour.",
        pointerDirection = PointerDirection.UP,
        badge = "1. APP GUIDE"
    ),
    GuideStepData(
        stepTitle = "Save Link Button",
        targetName = "+ SAVE BUTTON",
        description = "Tap + SAVE to manually paste or type any website, post, or video URL directly.",
        pointerDirection = PointerDirection.UP,
        badge = "2. + SAVE BUTTON"
    ),
    GuideStepData(
        stepTitle = "Platform Categories",
        targetName = "CATEGORY TILES",
        description = "Tap any category tile (X, YouTube, Reddit, GitHub, Chrome, Articles) to filter bookmarks.",
        pointerDirection = PointerDirection.DOWN,
        badge = "3. CATEGORIES"
    ),
    GuideStepData(
        stepTitle = "Search & Hashtags",
        targetName = "VAULT SEARCH BAR",
        description = "Type search keywords, URLs, or tap `#hashtags` to find saved links instantly.",
        pointerDirection = PointerDirection.DOWN,
        badge = "4. SEARCH BAR"
    ),
    GuideStepData(
        stepTitle = "Link Controls & Safe Delete",
        targetName = "LINK CARD TRASH ICON",
        description = "Tap Trash on any link card to delete. A safety prompt ('Are you sure?') will always confirm before deleting.",
        pointerDirection = PointerDirection.DOWN,
        badge = "5. SAFE DELETE"
    ),
    GuideStepData(
        stepTitle = "Ready to Get Started?",
        targetName = "INITIAL SETUP",
        description = "We included sample links so you could explore all features. Choose 'START FRESH' to clear them for your bookmarks, or 'KEEP SAMPLES' to explore.",
        pointerDirection = PointerDirection.UP,
        badge = "6. INITIAL SETUP"
    )
)

@Composable
fun SpotlightGuideOverlay(
    currentStepIndex: Int,
    isReplayMode: Boolean,
    onNextStep: () -> Unit,
    onDismiss: () -> Unit,
    onStartFresh: () -> Unit,
    onKeepDemo: () -> Unit,
    modifier: Modifier = Modifier
) {
    val stepData = guideStepsList.getOrElse(currentStepIndex) { guideStepsList.first() }
    val isLastStep = currentStepIndex == (if (isReplayMode) 4 else guideStepsList.lastIndex)

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnBackPress = true,
            dismissOnClickOutside = false
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.76f))
                .padding(20.dp),
            contentAlignment = when (stepData.pointerDirection) {
                PointerDirection.UP -> Alignment.TopCenter
                PointerDirection.DOWN -> Alignment.BottomCenter
                PointerDirection.LEFT -> Alignment.CenterStart
                PointerDirection.RIGHT -> Alignment.CenterEnd
            }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = if (stepData.pointerDirection == PointerDirection.UP) 48.dp else 0.dp)
                    .padding(bottom = if (stepData.pointerDirection == PointerDirection.DOWN) 48.dp else 0.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // If arrow points UP, draw the triangular nipple above the card pointing towards the target icon
                if (stepData.pointerDirection == PointerDirection.UP) {
                    PointerTriangleArrow(pointingUp = true)
                }

                // Floating Tooltip Popover Card
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(26.dp))
                        .background(CardSurface)
                        .border(1.5.dp, CardBorderActive, RoundedCornerShape(26.dp))
                        .padding(20.dp)
                        .testTag("spotlight_guide_card")
                ) {
                    // Header Bar with Target Badge and Step Counter
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
                                text = "POINTING AT: ${stepData.targetName}",
                                fontFamily = SpaceMonoFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp,
                                letterSpacing = 1.1.sp,
                                color = PureWhite
                            )
                        }

                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(IconCircleSurface)
                                .testTag("spotlight_close_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close Guide",
                                tint = PureWhite,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = stepData.stepTitle,
                        fontFamily = SpaceGroteskFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = PureWhite
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = stepData.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = SecondaryMutedText,
                        lineHeight = 20.sp
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    // Footer Row with Step Counter ("1 of 5") and Navigation Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${currentStepIndex + 1} of ${if (isReplayMode) 5 else 6}",
                            fontFamily = SpaceMonoFamily,
                            fontSize = 11.sp,
                            color = SecondaryMutedText
                        )

                        if (!isLastStep) {
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(999.dp))
                                        .background(PillSurface)
                                        .border(1.dp, CardBorderSubtle, RoundedCornerShape(999.dp))
                                        .clickable { onDismiss() }
                                        .padding(horizontal = 14.dp, vertical = 9.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "SKIP",
                                        fontFamily = SpaceGroteskFamily,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp,
                                        color = SecondaryMutedText
                                    )
                                }

                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(999.dp))
                                        .background(PureWhite)
                                        .clickable { onNextStep() }
                                        .padding(horizontal = 18.dp, vertical = 9.dp)
                                        .testTag("spotlight_next_button"),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "NEXT",
                                        fontFamily = SpaceGroteskFamily,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp,
                                        color = PureBlack
                                    )
                                }
                            }
                        } else if (isReplayMode) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(999.dp))
                                    .background(PureWhite)
                                    .clickable { onDismiss() }
                                    .padding(horizontal = 20.dp, vertical = 10.dp)
                                    .testTag("spotlight_done_button"),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "DONE",
                                    fontFamily = SpaceGroteskFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp,
                                    color = PureBlack
                                )
                            }
                        } else {
                            // Onboarding setup options
                            Column(
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                                horizontalAlignment = Alignment.End
                            ) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(999.dp))
                                        .background(PureWhite)
                                        .clickable { onStartFresh() }
                                        .padding(horizontal = 16.dp, vertical = 10.dp)
                                        .testTag("spotlight_start_fresh_button"),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "START FRESH",
                                        fontFamily = SpaceGroteskFamily,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp,
                                        color = PureBlack
                                    )
                                }

                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(999.dp))
                                        .background(PillSurface)
                                        .border(1.dp, CardBorderSubtle, RoundedCornerShape(999.dp))
                                        .clickable { onKeepDemo() }
                                        .padding(horizontal = 16.dp, vertical = 8.dp)
                                        .testTag("spotlight_keep_demo_button"),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "KEEP SAMPLES",
                                        fontFamily = SpaceGroteskFamily,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 10.sp,
                                        color = PureWhite
                                    )
                                }
                            }
                        }
                    }
                }

                // If arrow points DOWN, draw the triangular nipple below the card pointing towards the target icon
                if (stepData.pointerDirection == PointerDirection.DOWN) {
                    PointerTriangleArrow(pointingUp = false)
                }
            }
        }
    }
}

@Composable
private fun PointerTriangleArrow(pointingUp: Boolean) {
    Canvas(
        modifier = Modifier
            .size(width = 24.dp, height = 12.dp)
            .padding(vertical = 2.dp)
    ) {
        val path = Path().apply {
            if (pointingUp) {
                moveTo(size.width / 2f, 0f)
                lineTo(size.width, size.height)
                lineTo(0f, size.height)
            } else {
                moveTo(0f, 0f)
                lineTo(size.width, 0f)
                lineTo(size.width / 2f, size.height)
            }
            close()
        }
        drawPath(path = path, color = PureWhite)
    }
}

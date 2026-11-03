package com.kax.across.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.HelpOutline
import androidx.compose.material.icons.outlined.IosShare
import androidx.compose.material.icons.outlined.MenuBook
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kax.across.ui.VaultCategory
import com.kax.across.ui.theme.CardBorderActive
import com.kax.across.ui.theme.CardBorderSubtle
import com.kax.across.ui.theme.CardSurface
import com.kax.across.ui.theme.CardSurfaceElevated
import com.kax.across.ui.theme.IconCircleSurface
import com.kax.across.ui.theme.PureBlack
import com.kax.across.ui.theme.PureWhite
import com.kax.across.ui.theme.SecondaryMutedText
import com.kax.across.ui.theme.SpaceGroteskFamily
import com.kax.across.ui.theme.SpaceMonoFamily
import com.kax.across.ui.theme.TertiaryDimText
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
fun AppTopBar(
    title: String,
    onGridActionClick: () -> Unit,
    onHelpClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(CircleShape)
                .background(CardSurface)
                .border(1.dp, CardBorderSubtle, CircleShape)
                .clickable(onClick = onHelpClick)
                .testTag("top_help_button"),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Outlined.HelpOutline,
                contentDescription = "App Guide",
                tint = SecondaryMutedText,
                modifier = Modifier.size(20.dp)
            )
        }

        Text(
            text = title,
            fontFamily = SpaceGroteskFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp,
            letterSpacing = 2.8.sp,
            color = PureWhite
        )

        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(CircleShape)
                .background(CardSurface)
                .border(1.dp, CardBorderSubtle, CircleShape)
                .clickable(onClick = onGridActionClick)
                .testTag("top_grid_button"),
            contentAlignment = Alignment.Center
        ) {
            FourDotGridIcon()
        }
    }
}

@Composable
fun FourDotGridIcon() {
    Column(
        verticalArrangement = Arrangement.spacedBy(4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            Box(
                modifier = Modifier
                    .size(5.dp)
                    .clip(RoundedCornerShape(1.5.dp))
                    .background(SecondaryMutedText)
            )
            Box(
                modifier = Modifier
                    .size(5.dp)
                    .clip(RoundedCornerShape(1.5.dp))
                    .background(SecondaryMutedText)
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            Box(
                modifier = Modifier
                    .size(5.dp)
                    .clip(RoundedCornerShape(1.5.dp))
                    .background(SecondaryMutedText)
            )
            Box(
                modifier = Modifier
                    .size(5.dp)
                    .clip(RoundedCornerShape(1.5.dp))
                    .background(SecondaryMutedText)
            )
        }
    }
}

@Composable
fun SavedLinksHeroCard(
    totalCount: Int,
    onSearchClick: () -> Unit,
    onSaveClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(28.dp))
            .background(CardSurface)
            .border(width = 1.dp, color = CardBorderSubtle, shape = RoundedCornerShape(28.dp))
            .clickable { onSearchClick() }
            .padding(horizontal = 22.dp, vertical = 20.dp)
            .testTag("hero_indexed_card")
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Outlined.Search,
                    contentDescription = "Search Links",
                    tint = SecondaryMutedText,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "SAVED LINKS",
                    fontFamily = SpaceMonoFamily,
                    fontSize = 11.sp,
                    letterSpacing = 1.8.sp,
                    color = SecondaryMutedText
                )
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(5.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(PureWhite)
                )
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(TertiaryDimText)
                )
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(TertiaryDimText)
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        text = totalCount.toString(),
                        fontFamily = SpaceGroteskFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 44.sp,
                        lineHeight = 44.sp,
                        letterSpacing = (-1.5).sp,
                        color = PureWhite
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "LINKS",
                        fontFamily = SpaceMonoFamily,
                        fontSize = 11.sp,
                        letterSpacing = 1.4.sp,
                        color = SecondaryMutedText,
                        modifier = Modifier.padding(bottom = 6.dp)
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "SEARCH OR PASTE URL",
                    fontFamily = SpaceMonoFamily,
                    fontSize = 10.sp,
                    letterSpacing = 1.2.sp,
                    color = SecondaryMutedText
                )
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(999.dp))
                    .background(PureWhite)
                    .clickable { onSaveClick() }
                    .padding(horizontal = 20.dp, vertical = 13.dp)
                    .testTag("add_url_button"),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Save Link",
                        tint = PureBlack,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "SAVE",
                        fontFamily = SpaceGroteskFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        letterSpacing = 1.2.sp,
                        color = PureBlack
                    )
                }
            }
        }
    }
}

@Composable
fun CategorySquareTile(
    category: VaultCategory,
    count: Int,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val borderColor = if (isSelected) CardBorderActive else CardBorderSubtle
    val containerColor = if (isSelected) CardSurfaceElevated else CardSurface

    Column(
        modifier = modifier
            .height(154.dp)
            .clip(RoundedCornerShape(26.dp))
            .background(containerColor)
            .border(width = 1.dp, color = borderColor, shape = RoundedCornerShape(26.dp))
            .clickable(onClick = onClick)
            .padding(18.dp)
            .testTag("category_tile_${category.name}"),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = category.title,
                fontFamily = SpaceMonoFamily,
                fontSize = 11.sp,
                letterSpacing = 1.5.sp,
                color = SecondaryMutedText
            )

            SegmentedIndicatorDots(activeSegments = category.activeSegments)
        }

        Column {
            Text(
                text = count.toString(),
                fontFamily = SpaceGroteskFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 38.sp,
                lineHeight = 40.sp,
                letterSpacing = (-1).sp,
                color = PureWhite
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = category.subtitle,
                fontFamily = SpaceMonoFamily,
                fontSize = 10.sp,
                letterSpacing = 1.3.sp,
                color = SecondaryMutedText
            )
        }
    }
}

@Composable
private fun SegmentedIndicatorDots(activeSegments: Int) {
    Row(horizontalArrangement = Arrangement.spacedBy(3.dp)) {
        for (index in 1..4) {
            val isLit = index <= activeSegments
            Box(
                modifier = Modifier
                    .size(width = 6.dp, height = 6.dp)
                    .clip(RoundedCornerShape(1.5.dp))
                    .background(if (isLit) PureWhite else TertiaryDimText)
            )
        }
    }
}

@Composable
fun ArticlesReadsHorizontalCard(
    count: Int,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val borderColor = if (isSelected) CardBorderActive else CardBorderSubtle
    val containerColor = if (isSelected) CardSurfaceElevated else CardSurface

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(containerColor)
            .border(width = 1.dp, color = borderColor, shape = RoundedCornerShape(24.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 18.dp, vertical = 16.dp)
            .testTag("articles_reads_card"),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(IconCircleSurface),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.MenuBook,
                    contentDescription = "Articles and Reads",
                    tint = PureWhite,
                    modifier = Modifier.size(18.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column {
                Text(
                    text = "ARTICLES & READS",
                    fontFamily = SpaceGroteskFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    letterSpacing = 0.8.sp,
                    color = PureWhite
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "OFFLINE READS",
                    fontFamily = SpaceMonoFamily,
                    fontSize = 10.sp,
                    letterSpacing = 1.3.sp,
                    color = SecondaryMutedText
                )
            }
        }

        Text(
            text = count.toString(),
            fontFamily = SpaceGroteskFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 26.sp,
            color = PureWhite
        )
    }
}

/**
 * Interactive Slide-to-Open Track Row:
 * Users drag the circular chevron thumb across the track to open all saved links.
 * Includes spring physics snap-back if released early.
 */
@Composable
fun VaultsAndSharesActionRow(
    onOpenVaultsClick: () -> Unit,
    onShareExportClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val density = LocalDensity.current
    val coroutineScope = rememberCoroutineScope()

    var trackWidthPx by remember { mutableFloatStateOf(0f) }
    val thumbSizeDp = 40.dp
    val horizontalPadDp = 7.dp
    val thumbSizePx = with(density) { thumbSizeDp.toPx() }
    val horizontalPadPx = with(density) { horizontalPadDp.toPx() }

    val maxDragPx = (trackWidthPx - thumbSizePx - (horizontalPadPx * 2f)).coerceAtLeast(1f)
    val thumbOffsetX = remember { Animatable(0f) }

    val dragProgress = (thumbOffsetX.value / maxDragPx).coerceIn(0f, 1f)

    val draggableState = rememberDraggableState { delta ->
        val target = (thumbOffsetX.value + delta).coerceIn(0f, maxDragPx)
        coroutineScope.launch {
            thumbOffsetX.snapTo(target)
        }
    }

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .height(54.dp)
                .clip(RoundedCornerShape(999.dp))
                .background(CardSurface)
                .border(
                    width = 1.dp,
                    color = if (dragProgress > 0.1f) CardBorderActive else CardBorderSubtle,
                    shape = RoundedCornerShape(999.dp)
                )
                .clickable { onOpenVaultsClick() }
                .onSizeChanged { size ->
                    trackWidthPx = size.width.toFloat()
                }
                .draggable(
                    state = draggableState,
                    orientation = Orientation.Horizontal,
                    onDragStopped = {
                        if (thumbOffsetX.value >= maxDragPx * 0.68f) {
                            coroutineScope.launch {
                                thumbOffsetX.animateTo(
                                    targetValue = maxDragPx,
                                    animationSpec = spring(stiffness = Spring.StiffnessMediumLow)
                                )
                                onOpenVaultsClick()
                                thumbOffsetX.snapTo(0f)
                            }
                        } else {
                            coroutineScope.launch {
                                thumbOffsetX.animateTo(
                                    targetValue = 0f,
                                    animationSpec = spring(
                                        dampingRatio = Spring.DampingRatioMediumBouncy,
                                        stiffness = Spring.StiffnessMedium
                                    )
                                )
                            }
                        }
                    }
                )
                .padding(horizontal = horizontalPadDp)
                .testTag("vaults_slide_bar"),
            contentAlignment = Alignment.CenterStart
        ) {
            // Centered label that fades out smoothly as the user slides the thumb across
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .alpha((1f - dragProgress * 1.35f).coerceIn(0f, 1f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "SLIDE FOR ALL LINKS",
                    fontFamily = SpaceMonoFamily,
                    fontSize = 10.sp,
                    letterSpacing = 1.8.sp,
                    color = SecondaryMutedText,
                    modifier = Modifier.padding(start = 28.dp)
                )
            }

            // Draggable Circular Thumb
            Box(
                modifier = Modifier
                    .offset { IntOffset(thumbOffsetX.value.roundToInt(), 0) }
                    .size(thumbSizeDp)
                    .clip(CircleShape)
                    .background(if (dragProgress > 0.5f) PureWhite else IconCircleSurface),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = "Slide right to open all saved links",
                    tint = if (dragProgress > 0.5f) PureBlack else PureWhite,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        Box(
            modifier = Modifier
                .size(54.dp)
                .clip(CircleShape)
                .background(CardSurface)
                .border(width = 1.dp, color = CardBorderSubtle, shape = CircleShape)
                .clickable(onClick = onShareExportClick)
                .testTag("export_share_button"),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Outlined.IosShare,
                contentDescription = "Share Collection Summary",
                tint = PureWhite,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

package com.kax.across.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp

private val MonochromeObsidianScheme = darkColorScheme(
    primary = PureWhite,
    onPrimary = PureBlack,
    primaryContainer = CardSurfaceElevated,
    onPrimaryContainer = PureWhite,
    secondary = SecondaryMutedText,
    onSecondary = PureWhite,
    secondaryContainer = PillSurface,
    onSecondaryContainer = PrimaryText,
    tertiary = PureWhite,
    onTertiary = PureBlack,
    background = ObsidianBackground,
    onBackground = PrimaryText,
    surface = CardSurface,
    onSurface = PrimaryText,
    surfaceVariant = CardSurfaceElevated,
    onSurfaceVariant = SecondaryMutedText,
    outline = CardBorderSubtle,
    outlineVariant = CardBorderActive,
    error = AlertRed,
    onError = PureWhite,
    errorContainer = AlertRedContainer,
    onErrorContainer = PureWhite
)

val CleanRoundedShapes = Shapes(
    extraSmall = RoundedCornerShape(12.dp),
    small = RoundedCornerShape(16.dp),
    medium = RoundedCornerShape(24.dp),
    large = RoundedCornerShape(28.dp),
    extraLarge = RoundedCornerShape(999.dp)
)

@Composable
fun AcrossTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = MonochromeObsidianScheme,
        typography = CyberBlockTypography,
        shapes = CleanRoundedShapes,
        content = content
    )
}

@Composable
fun OmniSaveTheme(
    content: @Composable () -> Unit
) {
    AcrossTheme(content = content)
}

@Composable
fun MyApplicationTheme(
    content: @Composable () -> Unit
) {
    AcrossTheme(content = content)
}

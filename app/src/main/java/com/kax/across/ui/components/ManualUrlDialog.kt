package com.kax.across.ui.components

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.kax.across.ui.theme.CardBorderActive
import com.kax.across.ui.theme.CardBorderSubtle
import com.kax.across.ui.theme.CardSurface
import com.kax.across.ui.theme.PillSurface
import com.kax.across.ui.theme.PureBlack
import com.kax.across.ui.theme.PureWhite
import com.kax.across.ui.theme.SecondaryMutedText
import com.kax.across.ui.theme.SpaceGroteskFamily
import com.kax.across.ui.theme.SpaceMonoFamily

@Composable
fun ManualUrlDialog(
    onDismiss: () -> Unit,
    onSubmit: (String) -> Unit
) {
    var urlInput by remember { mutableStateOf("") }
    val clipboardManager = LocalClipboardManager.current

    Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(28.dp))
                .background(CardSurface)
                .border(1.dp, CardBorderActive, RoundedCornerShape(28.dp))
                .padding(22.dp)
                .testTag("manual_url_dialog")
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "SAVE LINK",
                    fontFamily = SpaceMonoFamily,
                    fontSize = 11.sp,
                    letterSpacing = 1.6.sp,
                    color = SecondaryMutedText
                )
                Text(
                    text = "CLOSE",
                    fontFamily = SpaceMonoFamily,
                    fontSize = 11.sp,
                    letterSpacing = 1.2.sp,
                    color = PureWhite,
                    modifier = Modifier.clickable { onDismiss() }
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Add Link or Paste URL",
                fontFamily = SpaceGroteskFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp,
                color = PureWhite
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Fetches the webpage title, description, and preview image automatically.",
                style = MaterialTheme.typography.bodySmall,
                color = SecondaryMutedText
            )

            Spacer(modifier = Modifier.height(16.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(PillSurface)
                    .border(1.dp, CardBorderSubtle, RoundedCornerShape(16.dp))
                    .padding(14.dp)
            ) {
                if (urlInput.isEmpty()) {
                    Text(
                        text = "https://x.com/... or https://youtube.com/...",
                        fontFamily = SpaceMonoFamily,
                        fontSize = 12.sp,
                        color = SecondaryMutedText
                    )
                }

                BasicTextField(
                    value = urlInput,
                    onValueChange = { urlInput = it },
                    textStyle = MaterialTheme.typography.bodyMedium.copy(
                        color = PureWhite,
                        fontFamily = SpaceMonoFamily
                    ),
                    cursorBrush = SolidColor(PureWhite),
                    singleLine = false,
                    maxLines = 3,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("manual_url_input")
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(999.dp))
                        .background(PillSurface)
                        .border(1.dp, CardBorderSubtle, RoundedCornerShape(999.dp))
                        .clickable {
                            val clip = clipboardManager.getText()?.text
                            if (!clip.isNullOrBlank()) {
                                urlInput = clip
                            }
                        }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "PASTE CLIPBOARD",
                        fontFamily = SpaceMonoFamily,
                        fontSize = 10.sp,
                        letterSpacing = 1.sp,
                        color = PureWhite
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

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
                        .padding(vertical = 14.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "CANCEL",
                        fontFamily = SpaceGroteskFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        letterSpacing = 1.sp,
                        color = PureWhite
                    )
                }

                Box(
                    modifier = Modifier
                        .weight(1.4f)
                        .clip(RoundedCornerShape(999.dp))
                        .background(PureWhite)
                        .clickable {
                            if (urlInput.isNotBlank()) {
                                onSubmit(urlInput.trim())
                            }
                        }
                        .padding(vertical = 14.dp)
                        .testTag("submit_manual_url_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "+ SAVE LINK",
                        fontFamily = SpaceGroteskFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        letterSpacing = 1.sp,
                        color = PureBlack
                    )
                }
            }
        }
    }
}

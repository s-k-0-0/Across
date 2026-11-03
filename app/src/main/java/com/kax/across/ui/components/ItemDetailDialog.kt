package com.kax.across.ui.components

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.Toast
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.kax.across.data.local.SavedItem
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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ItemDetailDialog(
    item: SavedItem,
    onDismiss: () -> Unit,
    onRescrape: (String) -> Unit
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val scrollState = rememberScrollState()
    val formattedDate = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US).format(Date(item.timestamp))

    Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(28.dp))
                .background(CardSurface)
                .border(1.dp, CardBorderActive, RoundedCornerShape(28.dp))
                .padding(22.dp)
                .verticalScroll(scrollState)
                .testTag("item_detail_dialog")
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = item.siteName.ifBlank { "SAVED ITEM" }.uppercase(Locale.US),
                    fontFamily = SpaceMonoFamily,
                    fontSize = 11.sp,
                    letterSpacing = 1.6.sp,
                    color = SecondaryMutedText
                )

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(IconCircleSurface)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = PureWhite,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            if (!item.imageUrl.isNullOrBlank()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .background(PureBlack)
                        .border(1.dp, CardBorderSubtle, RoundedCornerShape(18.dp))
                ) {
                    AsyncImage(
                        model = ImageRequest.Builder(context)
                            .data(item.imageUrl)
                            .crossfade(true)
                            .build(),
                        contentDescription = item.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(160.dp)
                    )
                }
                Spacer(modifier = Modifier.height(14.dp))
            }

            Text(
                text = item.title,
                fontFamily = SpaceGroteskFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                lineHeight = 25.sp,
                color = PureWhite
            )

            Spacer(modifier = Modifier.height(12.dp))

            MetadataField(label = "URL", value = item.url)
            MetadataField(label = "TAGS", value = item.tags.ifEmpty { "NONE" })
            MetadataField(label = "SAVED", value = formattedDate)

            if (item.description.isNotBlank()) {
                Spacer(modifier = Modifier.height(10.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(PillSurface)
                        .border(1.dp, CardBorderSubtle, RoundedCornerShape(16.dp))
                        .padding(12.dp)
                ) {
                    Text(
                        text = item.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = PureWhite
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(999.dp))
                        .background(PillSurface)
                        .border(1.dp, CardBorderSubtle, RoundedCornerShape(999.dp))
                        .clickable { launchBrowser(context, item.url) }
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.OpenInBrowser,
                            contentDescription = null,
                            tint = PureWhite,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "OPEN",
                            fontFamily = SpaceMonoFamily,
                            fontSize = 11.sp,
                            color = PureWhite
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(999.dp))
                        .background(PillSurface)
                        .border(1.dp, CardBorderSubtle, RoundedCornerShape(999.dp))
                        .clickable {
                            clipboardManager.setText(AnnotatedString(item.url))
                            Toast.makeText(context, "Copied to clipboard", Toast.LENGTH_SHORT).show()
                        }
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = null,
                            tint = PureWhite,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "COPY",
                            fontFamily = SpaceMonoFamily,
                            fontSize = 11.sp,
                            color = PureWhite
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(999.dp))
                        .background(PureWhite)
                        .clickable {
                            onRescrape(item.url)
                            onDismiss()
                        }
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = null,
                            tint = PureBlack,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "SYNC",
                            fontFamily = SpaceMonoFamily,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = PureBlack
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MetadataField(label: String, value: String) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Text(
            text = label,
            fontFamily = SpaceMonoFamily,
            fontSize = 10.sp,
            letterSpacing = 1.2.sp,
            color = SecondaryMutedText
        )
        Text(
            text = value,
            fontFamily = SpaceMonoFamily,
            fontSize = 11.sp,
            color = PureWhite
        )
    }
}

private fun launchBrowser(context: Context, url: String) {
    try {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    } catch (e: Exception) {
        Log.e("ItemDetailDialog", "Failed to open browser for $url", e)
        Toast.makeText(context, "Cannot open browser", Toast.LENGTH_SHORT).show()
    }
}

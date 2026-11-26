package com.example.ui.screens

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.OpenInNew
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ui.theme.CobaltBlue
import com.example.ui.theme.ElevatedNavy
import com.example.ui.theme.ErrorRed
import com.example.ui.theme.MidnightCard
import com.example.ui.theme.NeonPink
import com.example.ui.theme.NeonPinkText
import com.example.ui.theme.ObsidianBg
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary

enum class MediaType {
  VIDEO,
  PDF,
}

data class MediaViewerRequest(
  val title: String,
  val rawUrl: String,
  val type: MediaType,
)

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun MediaViewerDialog(
  request: MediaViewerRequest,
  onDismiss: () -> Unit,
  onExternalLaunchError: (String) -> Unit,
) {
  val context = LocalContext.current
  val resolvedUrl = remember(request) { resolveViewerUrl(request.rawUrl, request.type) }
  val isSafeHttpUrl = remember(resolvedUrl) {
    resolvedUrl.startsWith("https://", ignoreCase = true) ||
      resolvedUrl.startsWith("http://", ignoreCase = true)
  }
  var isPageLoading by remember { mutableStateOf(true) }

  Dialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(usePlatformDefaultWidth = false),
  ) {
    Surface(
      shape = RoundedCornerShape(20.dp),
      color = MidnightCard,
      border = BorderStroke(1.5.dp, NeonPink),
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 14.dp, vertical = 24.dp),
    ) {
      Column(modifier = Modifier.padding(14.dp)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween,
        ) {
          Text(
            text = request.title,
            style = MaterialTheme.typography.titleMedium,
            color = NeonPinkText,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f),
          )
          Spacer(modifier = Modifier.width(8.dp))
          IconButton(
            onClick = onDismiss,
            modifier = Modifier
              .minimumInteractiveComponentSize()
              .size(48.dp)
              .testTag("close_media_viewer_button"),
          ) {
            Box(
              modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(ErrorRed),
              contentAlignment = Alignment.Center,
            ) {
              Icon(
                imageVector = Icons.Rounded.Close,
                contentDescription = "Close viewer",
                tint = Color.White,
                modifier = Modifier.size(18.dp),
              )
            }
          }
        }

        if (isPageLoading && isSafeHttpUrl) {
          LinearProgressIndicator(
            color = NeonPink,
            trackColor = ElevatedNavy,
            modifier = Modifier
              .fillMaxWidth()
              .padding(vertical = 6.dp),
          )
        } else {
          Spacer(modifier = Modifier.height(8.dp))
        }

        if (!isSafeHttpUrl) {
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .height(180.dp)
              .clip(RoundedCornerShape(12.dp))
              .background(ObsidianBg)
              .padding(16.dp),
            contentAlignment = Alignment.Center,
          ) {
            Text(
              text = "Invalid or unsupported link URL. Please provide an https:// link.",
              style = MaterialTheme.typography.bodyMedium,
              color = TextMuted,
            )
          }
        } else {
          val viewerHeight = if (request.type == MediaType.VIDEO) 250.dp else 440.dp
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .height(viewerHeight)
              .clip(RoundedCornerShape(12.dp))
              .background(if (request.type == MediaType.VIDEO) ObsidianBg else Color.White),
          ) {
            AndroidView(
              modifier = Modifier.matchParentSize(),
              factory = { ctx ->
                WebView(ctx).apply {
                  settings.javaScriptEnabled = true
                  settings.domStorageEnabled = true
                  settings.loadWithOverviewMode = true
                  settings.useWideViewPort = true
                  settings.allowFileAccess = false
                  settings.allowContentAccess = false
                  settings.mediaPlaybackRequiresUserGesture = false
                  webViewClient = object : WebViewClient() {
                    override fun shouldOverrideUrlLoading(
                      view: WebView?,
                      req: WebResourceRequest?,
                    ): Boolean {
                      val uri = req?.url ?: return false
                      val scheme = uri.scheme?.lowercase()
                      return scheme != "http" && scheme != "https"
                    }

                    override fun onPageFinished(view: WebView?, url: String?) {
                      isPageLoading = false
                    }
                  }
                  loadUrl(resolvedUrl)
                }
              },
            )
          }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Button(
          onClick = {
            try {
              val intent = Intent(Intent.ACTION_VIEW, Uri.parse(request.rawUrl.trim()))
              context.startActivity(intent)
            } catch (_: ActivityNotFoundException) {
              onExternalLaunchError("No app available to open this link.")
            } catch (_: Exception) {
              onExternalLaunchError("Unable to open link externally.")
            }
          },
          colors = ButtonDefaults.buttonColors(containerColor = CobaltBlue),
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier
            .fillMaxWidth()
            .minimumInteractiveComponentSize()
            .testTag("open_media_external_button"),
        ) {
          Icon(
            imageVector = Icons.AutoMirrored.Rounded.OpenInNew,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(18.dp),
          )
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = if (request.type == MediaType.VIDEO) "Open in YouTube / Browser" else "Open in Browser / Drive",
            style = MaterialTheme.typography.labelLarge,
            color = TextPrimary,
          )
        }
      }
    }
  }
}

private fun resolveViewerUrl(rawUrl: String, type: MediaType): String {
  val url = rawUrl.trim()
  if (url.isBlank()) return ""
  return if (type == MediaType.VIDEO) {
    val videoId = extractYouTubeId(url)
    if (videoId != null) {
      "https://www.youtube.com/embed/$videoId?rel=0"
    } else {
      url
    }
  } else {
    if (url.contains("drive.google.com", ignoreCase = true) && url.contains("/view")) {
      url.replace("/view", "/preview")
    } else {
      url
    }
  }
}

private fun extractYouTubeId(url: String): String? {
  val uri = try {
    Uri.parse(url)
  } catch (_: Exception) {
    return null
  }
  val host = uri.host?.lowercase() ?: return null
  return when {
    host.contains("youtu.be") -> uri.pathSegments.firstOrNull()?.takeIf { it.isNotBlank() }
    host.contains("youtube.com") -> {
      uri.getQueryParameter("v")?.takeIf { it.isNotBlank() }
        ?: run {
          val segments = uri.pathSegments
          val embedIdx = segments.indexOf("embed")
          val shortsIdx = segments.indexOf("shorts")
          val liveIdx = segments.indexOf("live")
          when {
            embedIdx >= 0 && embedIdx + 1 < segments.size -> segments[embedIdx + 1]
            shortsIdx >= 0 && shortsIdx + 1 < segments.size -> segments[shortsIdx + 1]
            liveIdx >= 0 && liveIdx + 1 < segments.size -> segments[liveIdx + 1]
            else -> null
          }
        }
    }
    else -> null
  }
}

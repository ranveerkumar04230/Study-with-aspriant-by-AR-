package com.example.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.CobaltBlue
import com.example.ui.theme.DarkPink
import com.example.ui.theme.DarkPinkDeep
import com.example.ui.theme.GoldBase
import com.example.ui.theme.GoldLight
import com.example.ui.theme.MidnightIndigo
import com.example.ui.theme.NeonPink
import com.example.ui.theme.NeonPinkSoft
import com.example.ui.theme.ObsidianBg
import com.example.ui.theme.RoyalBlueDeep
import com.example.ui.theme.SkyAccent
import com.example.ui.theme.TextPrimary
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

const val SPLASH_DURATION_MILLIS = 2000L

@Composable
fun SplashScreen(
  onSplashFinished: () -> Unit,
) {
  val latestOnSplashFinished by rememberUpdatedState(onSplashFinished)
  val entranceAlpha = remember { Animatable(0f) }
  val entranceScale = remember { Animatable(0.92f) }

  val infiniteTransition = rememberInfiniteTransition(label = "splash_halo")
  val haloPulse by infiniteTransition.animateFloat(
    initialValue = 0.96f,
    targetValue = 1.05f,
    animationSpec = infiniteRepeatable(
      animation = tween(durationMillis = 1200, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse,
    ),
    label = "halo_scale",
  )

  LaunchedEffect(Unit) {
    launch {
      entranceAlpha.animateTo(
        targetValue = 1f,
        animationSpec = tween(durationMillis = 450, easing = FastOutSlowInEasing),
      )
    }
    launch {
      entranceScale.animateTo(
        targetValue = 1f,
        animationSpec = tween(durationMillis = 500, easing = FastOutSlowInEasing),
      )
    }
    delay(SPLASH_DURATION_MILLIS)
    latestOnSplashFinished()
  }

  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(
        Brush.verticalGradient(
          colors = listOf(
            RoyalBlueDeep,
            MidnightIndigo,
            DarkPinkDeep.copy(alpha = 0.92f),
            ObsidianBg,
          )
        )
      )
      .windowInsetsPadding(WindowInsets.safeDrawing)
      .padding(24.dp)
      .testTag("splash_screen"),
    contentAlignment = Alignment.Center,
  ) {
    Canvas(modifier = Modifier.fillMaxSize()) {
      val center = Offset(size.width / 2f, size.height * 0.44f)
      drawCircle(
        brush = Brush.radialGradient(
          colors = listOf(
            GoldLight.copy(alpha = 0.24f),
            NeonPink.copy(alpha = 0.14f),
            CobaltBlue.copy(alpha = 0.08f),
            Color.Transparent,
          ),
          center = center,
          radius = size.minDimension * 0.56f * haloPulse,
        ),
        radius = size.minDimension * 0.56f * haloPulse,
        center = center,
      )
      drawCircle(
        color = GoldLight.copy(alpha = 0.12f),
        radius = size.minDimension * 0.34f * haloPulse,
        center = center,
      )
    }

    Column(
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center,
      modifier = Modifier
        .alpha(entranceAlpha.value)
        .scale(entranceScale.value),
    ) {
      Text(
        text = stringResource(R.string.saraswati_mantra),
        style = MaterialTheme.typography.titleMedium.copy(
          fontWeight = FontWeight.Bold,
          letterSpacing = 1.sp,
        ),
        color = GoldLight,
        textAlign = TextAlign.Center,
      )

      Spacer(modifier = Modifier.height(18.dp))

      Box(
        modifier = Modifier
          .size(220.dp)
          .shadow(
            elevation = 32.dp,
            shape = CircleShape,
            ambientColor = GoldBase,
            spotColor = NeonPink,
          )
          .clip(CircleShape)
          .border(
            width = 3.dp,
            brush = Brush.linearGradient(listOf(GoldLight, NeonPink, CobaltBlue, GoldBase)),
            shape = CircleShape,
          ),
        contentAlignment = Alignment.Center,
      ) {
        Image(
          painter = painterResource(id = R.drawable.img_saraswati_maa),
          contentDescription = stringResource(R.string.saraswati_image_desc),
          contentScale = ContentScale.Crop,
          modifier = Modifier.fillMaxSize(),
        )
      }

      Spacer(modifier = Modifier.height(26.dp))

      Text(
        text = buildAnnotatedString {
          append("Study With ")
          withStyle(
            SpanStyle(
              brush = Brush.horizontalGradient(listOf(GoldLight, NeonPinkSoft, SkyAccent))
            )
          ) {
            append("Aspirant")
          }
        },
        style = MaterialTheme.typography.displaySmall.copy(
          fontSize = 29.sp,
          fontWeight = FontWeight.Black,
          letterSpacing = 0.4.sp,
        ),
        color = TextPrimary,
        textAlign = TextAlign.Center,
        modifier = Modifier.testTag("splash_title"),
      )

      Spacer(modifier = Modifier.height(10.dp))

      Text(
        text = stringResource(R.string.splash_quote),
        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
        color = GoldLight,
        textAlign = TextAlign.Center,
      )

      Spacer(modifier = Modifier.height(18.dp))

      Box(
        modifier = Modifier
          .clip(CircleShape)
          .background(
            Brush.horizontalGradient(
              listOf(DarkPink.copy(alpha = 0.55f), CobaltBlue.copy(alpha = 0.45f))
            )
          )
          .border(1.dp, GoldLight.copy(alpha = 0.45f), CircleShape)
          .padding(horizontal = 16.dp, vertical = 6.dp),
      ) {
        Text(
          text = "🙏 विद्या ददाति विनयम् • Welcome Aspirant",
          style = MaterialTheme.typography.labelSmall,
          color = NeonPinkSoft,
        )
      }

      Spacer(modifier = Modifier.height(22.dp))

      LinearProgressIndicator(
        color = GoldLight,
        trackColor = Color.White.copy(alpha = 0.16f),
        strokeCap = StrokeCap.Round,
        modifier = Modifier
          .width(140.dp)
          .height(4.dp)
          .clip(CircleShape),
      )
    }
  }
}

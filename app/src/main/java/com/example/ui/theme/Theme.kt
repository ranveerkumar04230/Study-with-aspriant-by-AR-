package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val StudyAspirantDarkScheme = darkColorScheme(
  primary = NeonPink,
  onPrimary = Color.White,
  primaryContainer = ElevatedNavy,
  onPrimaryContainer = NeonPinkSoft,
  secondary = CobaltBlue,
  onSecondary = Color.White,
  secondaryContainer = MidnightCard,
  onSecondaryContainer = TextPrimary,
  tertiary = GoldBase,
  onTertiary = Color.Black,
  background = ObsidianBg,
  onBackground = TextPrimary,
  surface = MidnightCard,
  onSurface = TextPrimary,
  surfaceVariant = ElevatedNavy,
  onSurfaceVariant = TextMuted,
  outline = SlateBorder,
  error = ErrorRed,
  onError = Color.White,
)

@Composable
fun StudyAspirantTheme(
  content: @Composable () -> Unit,
) {
  MaterialTheme(
    colorScheme = StudyAspirantDarkScheme,
    typography = AppTypography,
    content = content,
  )
}

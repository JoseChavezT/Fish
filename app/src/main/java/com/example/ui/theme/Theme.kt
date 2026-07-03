package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme =
  darkColorScheme(
    primary = MainGreen,
    secondary = SecondaryText,
    tertiary = DarkGreen,
    background = AmoledBackground,
    surface = CardBackground,
    onPrimary = AmoledBackground,
    onSecondary = WhiteText,
    onTertiary = WhiteText,
    onBackground = WhiteText,
    onSurface = WhiteText,
    surfaceVariant = CardBackground,
    onSurfaceVariant = SecondaryText,
    outline = SubtleBorder
  )

private val LightColorScheme = DarkColorScheme // Always AMOLED dark as per user requirement "Modo AMOLED verdadero"

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = true, // Force dark mode/AMOLED
  dynamicColor: Boolean = false, // Disable dynamic colors to preserve exact custom branding
  content: @Composable () -> Unit,
) {
  val colorScheme = DarkColorScheme

  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}

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
    primary = BrandTiffany,
    onPrimary = Black,
    secondary = LightBrandTiffany,
    onSecondary = Black,
    tertiary = White,
    background = DarkCharcoal,
    onBackground = White,
    surface = Charcol,
    onSurface = White
  )

private val LightColorScheme =
  lightColorScheme(
    primary = Black,
    onPrimary = BrandTiffany,
    secondary = Charcol,
    onSecondary = BrandTiffany,
    tertiary = DarkBrandTiffany,
    background = BrandTiffany,
    onBackground = Black,
    surface = White,
    onSurface = Black
  )

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  // Dynamic color is available on Android 12+
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  val colorScheme =
    when {
      dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
        val context = LocalContext.current
        if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
      }

      darkTheme -> DarkColorScheme
      else -> LightColorScheme
    }

  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}

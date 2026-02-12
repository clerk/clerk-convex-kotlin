package com.clerk.workouttracker.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme =
  darkColorScheme(
    primary = WorkoutAccentDark,
    background = WorkoutBackgroundDark,
    surface = WorkoutForegroundDark,
    surfaceContainer = WorkoutForegroundDark,
    onPrimary = WorkoutBackgroundDark,
    onBackground = WorkoutAccentDark,
    onSurface = WorkoutAccentDark,
  )

private val LightColorScheme =
  lightColorScheme(
    primary = WorkoutAccentLight,
    background = WorkoutBackgroundLight,
    surface = WorkoutForegroundLight,
    surfaceContainer = WorkoutForegroundLight,
    onPrimary = WorkoutBackgroundLight,
    onBackground = WorkoutAccentLight,
    onSurface = WorkoutAccentLight,
  )

@Composable
fun ClerkConvexTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
  val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}

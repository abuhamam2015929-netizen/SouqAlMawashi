package com.ahmed.souqalmawashi.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColors = lightColorScheme(
    primary = BrandGreen,
    onPrimary = SurfaceWhite,
    secondary = BrandGold,
    onSecondary = BrandGreenDark,
    background = BackgroundLight,
    surface = SurfaceWhite,
    onBackground = TextDark,
    onSurface = TextDark
)

@Composable
fun SouqAlMawashiTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = LightColors,
        typography = AppTypography,
        content = content
    )
}

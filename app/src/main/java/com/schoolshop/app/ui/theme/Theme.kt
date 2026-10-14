package com.schoolshop.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val Blue = Color(0xFF0B57E3)
val BlueLight = Color(0xFFEAF1FF)
val Ink = Color(0xFF1B2437)
val Muted = Color(0xFF6B7488)
val Line = Color(0xFFE3E8F2)
val Green = Color(0xFF22B14C)

private val Scheme = lightColorScheme(
    primary = Blue,
    onPrimary = Color.White,
    background = Color.White,
    surface = Color.White,
    onSurface = Ink,
    secondaryContainer = BlueLight,
)

@Composable
fun SchoolShopTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = Scheme, content = content)
}

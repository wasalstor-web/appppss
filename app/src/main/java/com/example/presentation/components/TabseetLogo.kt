package com.example.presentation.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke

@Composable
fun TabseetLogo(
    modifier: Modifier = Modifier, 
    color: Color = MaterialTheme.colorScheme.primary
) {
  Canvas(modifier = modifier.aspectRatio(1.2f)) {
    val strokeWidth = size.width * 0.18f
    val dotRadius = size.width * 0.1f

    drawLine(
      color = color,
      start = Offset(0f, size.height * 0.2f),
      end = Offset(size.width, size.height * 0.2f),
      strokeWidth = strokeWidth,
      cap = StrokeCap.Square
    )

    drawCircle(
      color = color,
      radius = dotRadius * 0.7f,
      center = Offset(size.width * 0.45f, size.height * 0.4f)
    )

    drawArc(
      color = color,
      startAngle = 0f,
      sweepAngle = 180f,
      useCenter = false,
      topLeft = Offset(size.width * 0.1f, size.height * 0.2f),
      size = Size(size.width * 0.8f, size.height * 0.7f),
      style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
    )

    drawCircle(
      color = color,
      radius = dotRadius * 0.9f,
      center = Offset(size.width * 0.85f, size.height * 0.65f)
    )
  }
}

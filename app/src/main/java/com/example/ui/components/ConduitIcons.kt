package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.example.ui.theme.BlueprintBlue

@Composable
fun ConduitIconContainer(
    modifier: Modifier = Modifier,
    backgroundColor: Color = Color(0xFFEBF3FC),
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .size(52.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(backgroundColor),
        contentAlignment = Alignment.Center
    ) {
        content()
    }
}

// 1. Rolling Offset Icon
@Composable
fun RollingOffsetIcon(
    modifier: Modifier = Modifier.size(34.dp),
    color: Color = BlueprintBlue
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val stroke = Stroke(width = 3.5f, cap = StrokeCap.Round, join = StrokeJoin.Round)

        // S-curve offset with rolling indicator
        val path = Path().apply {
            moveTo(w * 0.12f, h * 0.72f)
            lineTo(w * 0.35f, h * 0.72f)
            lineTo(w * 0.65f, h * 0.28f)
            lineTo(w * 0.88f, h * 0.28f)
        }
        drawPath(path, color = color, style = stroke)

        // Rolling ring / circle in the middle
        drawCircle(
            color = color,
            radius = 4.5f,
            center = Offset(w * 0.5f, h * 0.5f),
            style = Stroke(width = 2.5f)
        )
    }
}

// 2. Parallel Offset Icon
@Composable
fun ParallelOffsetIcon(
    modifier: Modifier = Modifier.size(34.dp),
    color: Color = BlueprintBlue
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val stroke = Stroke(width = 3.2f, cap = StrokeCap.Round, join = StrokeJoin.Round)

        // Top parallel pipe
        val path1 = Path().apply {
            moveTo(w * 0.12f, h * 0.62f)
            lineTo(w * 0.36f, h * 0.62f)
            lineTo(w * 0.64f, h * 0.24f)
            lineTo(w * 0.88f, h * 0.24f)
        }
        drawPath(path1, color = color, style = stroke)

        // Bottom parallel pipe
        val path2 = Path().apply {
            moveTo(w * 0.12f, h * 0.82f)
            lineTo(w * 0.36f, h * 0.82f)
            lineTo(w * 0.64f, h * 0.44f)
            lineTo(w * 0.88f, h * 0.44f)
        }
        drawPath(path2, color = color, style = stroke)
    }
}

// 3. Matching Centers Offset Icon
@Composable
fun MatchingCentersIcon(
    modifier: Modifier = Modifier.size(34.dp),
    color: Color = BlueprintBlue
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val stroke = Stroke(width = 3.5f, cap = StrokeCap.Round, join = StrokeJoin.Round)

        val path = Path().apply {
            moveTo(w * 0.12f, h * 0.70f)
            lineTo(w * 0.38f, h * 0.70f)
            lineTo(w * 0.68f, h * 0.30f)
            lineTo(w * 0.88f, h * 0.30f)
        }
        drawPath(path, color = color, style = stroke)

        // Center tick line
        drawLine(
            color = color.copy(alpha = 0.85f),
            start = Offset(w * 0.53f, h * 0.38f),
            end = Offset(w * 0.53f, h * 0.62f),
            strokeWidth = 2.2f,
            cap = StrokeCap.Round
        )
    }
}

// 4. Matching Bends Offset Icon
@Composable
fun MatchingBendsIcon(
    modifier: Modifier = Modifier.size(34.dp),
    color: Color = BlueprintBlue
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val stroke = Stroke(width = 3.5f, cap = StrokeCap.Round, join = StrokeJoin.Round)

        val path = Path().apply {
            moveTo(w * 0.12f, h * 0.75f)
            lineTo(w * 0.32f, h * 0.75f)
            lineTo(w * 0.68f, h * 0.25f)
            lineTo(w * 0.88f, h * 0.25f)
        }
        drawPath(path, color = color, style = stroke)

        // Angle arc at first bend
        drawCircle(
            color = color,
            radius = 3.5f,
            center = Offset(w * 0.32f, h * 0.75f)
        )
        drawCircle(
            color = color,
            radius = 3.5f,
            center = Offset(w * 0.68f, h * 0.25f)
        )
    }
}

// 5. Three-Point Saddle Icon
@Composable
fun ThreePointSaddleIcon(
    modifier: Modifier = Modifier.size(34.dp),
    color: Color = BlueprintBlue
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val stroke = Stroke(width = 3.5f, cap = StrokeCap.Round, join = StrokeJoin.Round)

        // Saddle bridging over obstruction
        val path = Path().apply {
            moveTo(w * 0.10f, h * 0.75f)
            lineTo(w * 0.30f, h * 0.75f)
            lineTo(w * 0.50f, h * 0.35f)
            lineTo(w * 0.70f, h * 0.75f)
            lineTo(w * 0.90f, h * 0.75f)
        }
        drawPath(path, color = color, style = stroke)

        // Obstruction circle underneath saddle peak
        drawCircle(
            color = color.copy(alpha = 0.9f),
            radius = 4.5f,
            center = Offset(w * 0.50f, h * 0.65f),
            style = Stroke(width = 2.5f)
        )
    }
}

package com.supikashi.recharge.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.supikashi.recharge.viewmodels.DailyBreakStats

@Composable
fun BreakProgressChart(
    stats: DailyBreakStats,
    modifier: Modifier = Modifier,
    chartSize: Dp = 120.dp,
    strokeWidth: Dp = 12.dp
) {
    val completedColor = MaterialTheme.colorScheme.onBackground
    val remainingColor = MaterialTheme.colorScheme.secondary

    val animatedProgress = remember { Animatable(0f) }
    
    LaunchedEffect(stats.completionPercentage) {
        animatedProgress.animateTo(
            targetValue = stats.completionPercentage,
            animationSpec = tween(
                durationMillis = 400,
                easing = FastOutSlowInEasing
            )
        )
    }
    
    Row(
        modifier = modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(chartSize).weight(1f)
        ) {
            Canvas(modifier = Modifier.size(chartSize)) {
                val sweepAngle = animatedProgress.value * 360f
                val stroke = Stroke(
                    width = strokeWidth.toPx(),
                    cap = StrokeCap.Round
                )

                drawArc(
                    color = remainingColor,
                    startAngle = -90f,
                    sweepAngle = 360f,
                    useCenter = false,
                    style = stroke
                )

                if (sweepAngle > 0) {
                    drawArc(
                        color = completedColor,
                        startAngle = -90f,
                        sweepAngle = sweepAngle,
                        useCenter = false,
                        style = stroke
                    )
                }
            }

            Text(
                text = "${(animatedProgress.value * 100).toInt()}%",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        Spacer(modifier = Modifier.width(20.dp))

        Column(
            horizontalAlignment = Alignment.Start,
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = "Ты отдохнул",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "${stats.completedBreaks}/${stats.totalBreaks}",
                style = MaterialTheme.typography.headlineMedium,
            )
            Text(
                text = "раз",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

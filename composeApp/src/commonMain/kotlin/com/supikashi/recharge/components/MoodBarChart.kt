package com.supikashi.recharge.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.supikashi.recharge.theme.AppTheme
import com.supikashi.recharge.viewmodels.DayMoodStats
import com.supikashi.recharge.viewmodels.MoodTrendStats
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus
import kotlinx.datetime.todayIn
import org.jetbrains.compose.ui.tooling.preview.Preview
import kotlin.collections.flatMap
import kotlin.collections.map
import kotlin.random.Random
import kotlin.random.nextInt
import kotlin.time.Clock

val n = 30
@Composable
fun MoodBarChart(
    stats: MoodTrendStats,
    modifier: Modifier = Modifier
) {
    

    val colorList = listOf(
        MaterialTheme.colorScheme.primary,
        MaterialTheme.colorScheme.secondary,
        MaterialTheme.colorScheme.tertiary,
    )
    val dayColorsSequence = remember(colorList) {
        val seq = mutableListOf<Color>()
        var lastColor: Color? = null
        for (i in 0 until n) {
            var available = colorList.filter { it != lastColor }
            
            if (i == n - 1) {
                val firstColor = seq.first()
                available = available.filter { it != firstColor }
            }
            if (available.isEmpty()) available = colorList

            val c1 = available.random()
            
            lastColor = c1
            seq.add(c1)
        }
        seq
    }

    val barColors = remember(stats, dayColorsSequence) {
        stats.days.map { dayStats ->
            val epochDays = dayStats.date.toEpochDays()
            val index = (epochDays % n).toInt()
            dayColorsSequence[index]
        }
    }


    val textMeasurer = rememberTextMeasurer()
    val labelStyle = MaterialTheme.typography.labelMedium.copy(color = MaterialTheme.colorScheme.onBackground)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "График самочувствия",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 20.dp),
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center
        )

        Box(
            modifier = Modifier.fillMaxWidth().height(250.dp),
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val h = size.height
                val textOffset = 20.dp.toPx()
                for (i in 0..5) {
                    val y = h - (i / 5f) * h

                    val textLayoutResult = textMeasurer.measure(
                        text = i.toString(),
                        style = labelStyle
                    )
                    val textHeight = textLayoutResult.size.height
                    val textWidth = textLayoutResult.size.width
                    drawText(
                        textLayoutResult = textLayoutResult,
                        topLeft = Offset((textOffset - textWidth) / 2, y - textHeight / 2f)
                    )

                    drawLine(
                        color = Color.LightGray.copy(alpha = 0.5f),
                        start = Offset(textOffset, y),
                        end = Offset(size.width, y),
                        strokeWidth = 1.dp.toPx()
                    )

                }
            }

            Row(
                modifier = Modifier.fillMaxWidth().padding(start = 25.dp, end = 5.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                stats.days.forEachIndexed { dayIdx, dayStats ->
                    DayMoodGroup(
                        avg = dayStats.avg,
                        morning = dayStats.morningAvg,
                        day = dayStats.dayAvg,
                        evening = dayStats.eveningAvg,
                        color1 = barColors[dayIdx],
                        color2 = barColors[dayIdx],
                        color3 = barColors[dayIdx],
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth().padding(start = 25.dp, end = 5.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            stats.days.forEachIndexed { dayIdx, dayStats ->
                val label = "${dayStats.date.dayOfMonth}.${dayStats.date.monthNumber.toString().padStart(2, '0')}"
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onBackground,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f).padding(top = 5.dp),
                    maxLines = 1
                )
            }
        }
    }
}

@Composable
private fun DayMoodGroup(
    avg: Float,
    morning: Float,
    day: Float,
    evening: Float,
    color1: Color,
    color2: Color,
    color3: Color,
    modifier: Modifier = Modifier
) {
    val morningAnim = remember { Animatable(0f) }
    val dayAnim = remember { Animatable(0f) }
    val eveningAnim = remember { Animatable(0f) }

    LaunchedEffect(morning) {
        morningAnim.animateTo(morning, animationSpec = tween(500, easing = FastOutSlowInEasing))
    }
    LaunchedEffect(day) {
        dayAnim.animateTo(day, animationSpec = tween(500, easing = FastOutSlowInEasing))
    }
    LaunchedEffect(evening) {
        eveningAnim.animateTo(evening, animationSpec = tween(500, easing = FastOutSlowInEasing))
    }

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.BottomCenter
    ) {
        Canvas(modifier = Modifier.matchParentSize()) {
            val w = size.width
            val h = size.height

            val spacing = 0f
            val totalWidth = w
            val barWidth = totalWidth 
            val startX = (w - totalWidth) / 2

            val cornerRadius = CornerRadius(barWidth / 5, barWidth / 5)

            val values = listOf(avg) 
            val colors = listOf(color1, color2, color3)

            values.forEachIndexed { index, value ->
                val x = startX + index * barWidth

                val mappedHeight = (value / 5f) * h
                if (mappedHeight > 0f) {
                    val y = h - mappedHeight
                    drawRoundRect(
                        color = colors[index],
                        topLeft = Offset(x, y),
                        size = Size(barWidth, mappedHeight),
                        cornerRadius = cornerRadius
                    )
                }
            }
        }
    }
}

@Preview()
@Composable
fun MoodBarChartPreview() {
    AppTheme {
        val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
        val fakeDays = (0..6).map { offset ->
            val date = today.plus(offset - 6, DateTimeUnit.DAY)
            DayMoodStats(
                date = date,
                morningCount = 1, morningSum = listOf(2, 3, 4, 5).random(),
                dayCount = 1, daySum = listOf(3, 4, 5).random(),
                eveningCount = 1, eveningSum = listOf(2, 3, 4, 5).random()
            )
        }
        MoodBarChart(stats = MoodTrendStats(fakeDays))
    }
}











package com.supikashi.recharge.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.supikashi.recharge.database.Break
import com.supikashi.recharge.database.Task
import com.supikashi.recharge.database.TaskWithBreaks
import com.supikashi.recharge.utils.formatMinutesToTime
import com.supikashi.recharge.theme.AppTheme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import recharge.composeapp.generated.resources.Res
import recharge.composeapp.generated.resources.clock
import recharge.composeapp.generated.resources.trash
import kotlin.math.roundToInt

@Composable
fun TaskCard(
    taskWithBreaks: TaskWithBreaks,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    onDelete: () -> Unit = {}
) {
    val task = taskWithBreaks.task
    val container = if (task.isWork)
        MaterialTheme.colorScheme.primary
    else
        MaterialTheme.colorScheme.tertiary

    val content = if (task.isWork)
        MaterialTheme.colorScheme.background
    else
        MaterialTheme.colorScheme.onBackground

    var offsetX by remember { mutableStateOf(0f) }
    val animatedOffsetX by animateFloatAsState(targetValue = offsetX)
    
    Box(
        modifier = modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .matchParentSize()
                .padding(start = 65.dp)
                .background(MaterialTheme.colorScheme.onBackground, RoundedCornerShape(10.dp)),
            contentAlignment = Alignment.CenterEnd
        ) {
            IconButton(
                onClick = {
                    onDelete()
                    offsetX = 0f
                },
                modifier = Modifier
            ) {
                Icon(
                    painter = painterResource(Res.drawable.trash),
                    contentDescription = "Удалить",
                    tint = MaterialTheme.colorScheme.background,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .offset { IntOffset(animatedOffsetX.roundToInt(), 0) }
                .pointerInput(Unit) {
                    detectHorizontalDragGestures(
                        onDragEnd = {
                            offsetX = if (offsetX < -50f) -150f else 0f
                        },
                        onHorizontalDrag = { _, dragAmount ->
                            val newOffset = offsetX + dragAmount
                            offsetX = newOffset.coerceIn(-150f, 0f)
                        }
                    )
                },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(Res.drawable.clock),
                contentDescription = null,
                tint = Color.Unspecified,
                modifier = Modifier.size(15.dp).align(Alignment.Top)
            )
            Spacer(modifier = Modifier.width(5.dp))
            Column(
                modifier = Modifier.align(Alignment.Top).width(35.dp),
            ) {
                Text(
                    text = formatMinutesToTime(task.startTime),
                    style = MaterialTheme.typography.labelMedium,
                    color = Color(0xFFAEAEAC),
                )
                Text(
                    text = formatMinutesToTime(task.endTime),
                    style = MaterialTheme.typography.labelMedium,
                    color = Color(0xFFAEAEAC),
                )
            }

            Spacer(Modifier.width(10.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth(),
                colors = CardDefaults.cardColors().copy(
                    containerColor = container,
                    contentColor = content
                ),
                shape = RoundedCornerShape(10.dp)
            ) {
                Column(
                    Modifier
                        .fillMaxWidth()
                        .clickable { onClick() }
                        .padding(vertical = 10.dp),
                ) {
                    Text(
                        text = task.name.ifEmpty { "Без названия" },
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(horizontal = 10.dp)
                    )
                    if (task.isSplittable) {
                        Spacer(Modifier.height(5.dp))
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp)
                        ) {
                            items(taskWithBreaks.breaks) {
                                Text(
                                    text = formatMinutesToTime(it.time),
                                    color = MaterialTheme.colorScheme.onBackground,
                                    style = MaterialTheme.typography.labelMedium,
                                    modifier = Modifier
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.tertiary)
                                        .padding(vertical = 2.dp, horizontal = 5.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview()
@Composable
fun TaskCardPreview() {
    AppTheme {
        TaskCard(
            taskWithBreaks = TaskWithBreaks(
                task = Task(
                    name = "name",
                    isWork = true,
                    isSplittable = true,
                ),
                breaks = listOf(Break(time = 100), Break(time = 111), Break(time = 250),)
            )
        )
    }
}
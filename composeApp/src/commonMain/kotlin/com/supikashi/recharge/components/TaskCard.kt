package com.supikashi.recharge.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import recharge.composeapp.generated.resources.Res
import recharge.composeapp.generated.resources.clock
import recharge.composeapp.generated.resources.task_card_untitled
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

    Row(
        modifier = Modifier
            .fillMaxWidth(),
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

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(container)
                .clickable { onClick() }
                .padding(vertical = 10.dp)
        ) {
            Text(
                text = task.name.ifEmpty { stringResource(Res.string.task_card_untitled) },
                style = MaterialTheme.typography.bodyMedium,
                color = content,
                modifier = Modifier.padding(horizontal = 10.dp)
            )
            if (task.isSplittable) {
                Spacer(Modifier.height(5.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = 10.dp)
                ) {
                    taskWithBreaks.breaks.forEach { breakItem ->
                        Text(
                            text = formatMinutesToTime(breakItem.time),
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
package com.supikashi.recharge.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.foundation.lazy.LazyListState
import org.jetbrains.compose.ui.tooling.preview.Preview
import kotlin.math.abs

fun LazyListState.getCenteredItemIndex(): Int {
    val layoutInfo = this.layoutInfo
    val visibleItemsInfo = layoutInfo.visibleItemsInfo

    val center = (layoutInfo.viewportStartOffset + layoutInfo.viewportEndOffset) / 2
    return visibleItemsInfo.minByOrNull {
        abs(it.offset + it.size / 2 - center)
    }?.index ?: firstVisibleItemIndex
}


@Composable
fun TimeRangeInputManual(
    modifier: Modifier = Modifier,
    from: String = "",
    to: String = "",
    onFromTimeChanged: (String) -> Unit = {},
    onToTimeChanged: (String) -> Unit = {}
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        TimeColumn(
            label = "от",
            timeString = from,
            onTimeChanged = onFromTimeChanged,
            modifier = Modifier.weight(1f)
        )
        TimeColumn(
            label = "до",
            timeString = to,
            onTimeChanged = onToTimeChanged,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun TimeColumn(
    label: String,
    timeString: String,
    onTimeChanged: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var showDialog by remember { mutableStateOf(false) }

    // Format the text to always display as HH:MM if valid, or --:-- otherwise
    val displayTime = remember(timeString) {
        val digits = timeString.filter { it.isDigit() }
        if (digits.length >= 4) {
            "${digits.substring(0, 2)}:${digits.substring(2, 4)}"
        } else timeString.ifEmpty {
            "--:--"
        }
    }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier.height(40.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
            )
        }
        Spacer(modifier = Modifier.height(10.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(Color(0xFFE8E8E8))
                .clickable { showDialog = true },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = displayTime,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = if (displayTime == "--:--") MaterialTheme.colorScheme.onBackground.copy(alpha = 0.3f)
                            else MaterialTheme.colorScheme.onBackground,
                    textAlign = TextAlign.Center
                )
            )
        }
    }

    if (showDialog) {
        WheelTimePickerDialog(
            initialTime = timeString,
            onDismiss = { showDialog = false },
            onConfirm = { newTime ->
                onTimeChanged(newTime)
                showDialog = false
            }
        )
    }
}

@Composable
fun WheelTimePickerDialog(
    initialTime: String,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    val midIndexHours = Int.MAX_VALUE / 2 - ((Int.MAX_VALUE / 2) % 24)
    val midIndexMinutes = Int.MAX_VALUE / 2 - ((Int.MAX_VALUE / 2) % 60)
    val digits = initialTime.filter { it.isDigit() }
    val initialHour = if (digits.length >= 2) digits.substring(0, 2).toIntOrNull()?.coerceIn(0, 23) ?: 0 else 0
    val initialMinute = if (digits.length >= 4) digits.substring(2, 4).toIntOrNull()?.coerceIn(0, 59) ?: 0 else 0

    val hoursList = remember { (0..23).map { it.toString().padStart(2, '0') } }
    val minutesList = remember { (0..59).map { it.toString().padStart(2, '0') } }

    val hoursListState = rememberLazyListState(initialFirstVisibleItemIndex = initialHour + midIndexHours)
    val minutesListState = rememberLazyListState(initialFirstVisibleItemIndex = initialMinute + midIndexMinutes)

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.background,
            contentColor = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Выберите время",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 24.dp)
                )
                Box(modifier = Modifier.clip(RoundedCornerShape(24.dp))) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.secondary, RoundedCornerShape(20.dp)),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        WheelPicker(
                            items = hoursList,
                            listState = hoursListState,
                            modifier = Modifier.weight(1f),
                            visibleItemsCount = 5
                        )
                        Box(
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = ":",
                                style = MaterialTheme.typography.headlineMedium,
                                textAlign = TextAlign.Center,
                                modifier = Modifier
                                    .padding(bottom = 4.dp)
                            )
                        }
                        WheelPicker(
                            items = minutesList,
                            listState = minutesListState,
                            modifier = Modifier.weight(1f),
                            visibleItemsCount = 5
                        )
                    }
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .fillMaxWidth()
                            .height(80.dp)
                            .background(
                                brush = Brush.verticalGradient(
                                    colors = listOf(
                                        MaterialTheme.colorScheme.secondary,
                                        MaterialTheme.colorScheme.secondary.copy(alpha = 0.8f),
                                        Color.Transparent
                                    )
                                )
                            )
                    )

                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .fillMaxWidth()
                            .height(80.dp)
                            .background(
                                brush = Brush.verticalGradient(
                                    colors = listOf(
                                        Color.Transparent,
                                        MaterialTheme.colorScheme.secondary.copy(alpha = 0.8f),
                                        MaterialTheme.colorScheme.secondary
                                    )
                                )
                            )
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    TextButton(
                        colors = ButtonDefaults.buttonColors().copy(contentColor = MaterialTheme.colorScheme.onBackground, containerColor = Color.Transparent),
                        onClick = onDismiss
                    ) {
                        Text(
                            text = "Отмена",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        colors = ButtonDefaults.buttonColors().copy(contentColor = MaterialTheme.colorScheme.background, containerColor = MaterialTheme.colorScheme.onBackground),
                        onClick = {
                            val selHourIndex = hoursListState.getCenteredItemIndex() % 24
                            val selMinuteIndex = minutesListState.getCenteredItemIndex() % 60

                            val formattedTime = "${hoursList[selHourIndex]}:${minutesList[selMinuteIndex]}"
                            onConfirm(formattedTime)
                        }
                    ) {
                        Text(
                            text = "Сохранить",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun TimeRangeInputManualPreview() {
    TimeRangeInputManual(
        from = "0800",
        to = "1730"
    )
}

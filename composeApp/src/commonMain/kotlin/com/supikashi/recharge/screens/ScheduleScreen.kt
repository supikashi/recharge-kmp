package com.supikashi.recharge.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.supikashi.recharge.database.Task
import com.supikashi.recharge.components.TaskCard
import com.supikashi.recharge.components.TimeRangeInputManual
import com.supikashi.recharge.components.TopBar
import com.supikashi.recharge.components.WorkRestSwitch
import com.supikashi.recharge.theme.AppTheme
import com.supikashi.recharge.theme.mascotPrimary
import com.supikashi.recharge.utils.formatDate
import com.supikashi.recharge.utils.formatDayOfWeek
import com.supikashi.recharge.utils.formatMinutesToTime
import com.supikashi.recharge.utils.parseTimeToMinutes
import com.supikashi.recharge.viewmodels.SlotViewModel
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus
import kotlinx.datetime.todayIn
import org.jetbrains.compose.resources.InternalResourceApi
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel
import recharge.composeapp.generated.resources.Res
import recharge.composeapp.generated.resources.arrow_left
import recharge.composeapp.generated.resources.calendar
import recharge.composeapp.generated.resources.close_circle
import recharge.composeapp.generated.resources.home
import recharge.composeapp.generated.resources.mascot
import recharge.composeapp.generated.resources.tick_circle
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalMaterial3Api::class, ExperimentalTime::class, InternalResourceApi::class)
@Composable
fun ScheduleScreen(
    onNavigateHome: () -> Unit = {},
) {
    val viewModel: SlotViewModel = koinViewModel()
    val slots by viewModel.tasks.collectAsStateWithLifecycle()
    var newSlot by remember { mutableStateOf(Task()) }
    var editedSlot by remember { mutableStateOf<Task?>(null) }
    var isSlotCard by remember { mutableStateOf(false) }
    var selectedDate by remember { mutableStateOf(Clock.System.todayIn(TimeZone.currentSystemDefault())) }
    Scaffold { paddingValues ->
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.background(MaterialTheme.colorScheme.mascotPrimary)
                .padding(top = WindowInsets.safeDrawing.asPaddingValues().calculateTopPadding())
                .fillMaxSize()
        ) {
            TopBar(
                leftAction = { viewModel.upsertTask(Task()) },
                leftIcon = Res.drawable.calendar,
                rightAction = onNavigateHome,
                rightIcon = Res.drawable.home,
                modifier = Modifier.padding(horizontal = 20.dp)
            )

            Text(
                text = formatDate(selectedDate),
                style = MaterialTheme.typography.headlineMedium
            )

            Text(
                text = formatDayOfWeek(selectedDate),
                style = MaterialTheme.typography.bodyMedium
            )

            Row {
                IconButton(
                    onClick = {
                        selectedDate = selectedDate.plus(-1, DateTimeUnit.DAY)
                        newSlot = newSlot.copy(date = selectedDate)
                    }
                ) {
                    Icon(
                        painter = painterResource(Res.drawable.arrow_left),
                        contentDescription = null,
                    )
                }

                IconButton(
                    onClick = {
                        selectedDate = selectedDate.plus(1, DateTimeUnit.DAY)
                        newSlot = newSlot.copy(date = selectedDate)
                    }
                ) {
                    Icon(
                        painter = painterResource(Res.drawable.arrow_left),
                        contentDescription = null,
                        modifier = Modifier.rotate(180f)
                    )
                }
            }

            Column(
                modifier = Modifier
                    .clip(
                        RoundedCornerShape(
                            topStart = 30.dp,
                            topEnd = 30.dp
                        )
                    )
                    .background(MaterialTheme.colorScheme.background)
                    .fillMaxWidth()
                    .padding(vertical = 40.dp, horizontal = 20.dp)
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(20.dp, Alignment.Bottom),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                if (isSlotCard) {
                    NewSlot(
                        modifier = Modifier.fillMaxSize(),
                        slots = slots.filter { it.task.date == selectedDate }.map { it.task },
                        newSlot = newSlot,
                        onChange = { newSlot = it },
                        onBack = {
                            isSlotCard = false
                            newSlot = Task()
                        },
                        onSave = {
                            viewModel.upsertTask(newSlot)
                            isSlotCard = false
                            newSlot = Task()
                        }
                    )
                } else {
                    if (slots.none { it.task.date == selectedDate }) {
                        Text(
                            text = "В расписании на этот день пока\nничего нет, давай его дополним",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Icon(
                            painter = painterResource(Res.drawable.mascot),
                            contentDescription = null,
                            tint = Color.Unspecified,
                        )
                    } else {
                        LazyColumn(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(5.dp)
                        ) {
                            items(key = {it.task.id}, items = slots.filter { it.task.date == selectedDate }) { slot ->
                                TaskCard(
                                    taskWithBreaks = slot,
                                    onClick = {
                                        newSlot = slot.task
                                        isSlotCard = true
                                    },
                                    onDelete = {
                                        viewModel.deleteTask(slot.task)
                                    }
                                )
                            }
                        }
                    }
                    Button(onClick = { isSlotCard = true }) {
                        Text("Добавить задачу")
                    }
                }
            }
        }
    }
}

@Composable
private fun NewSlot(
    modifier: Modifier = Modifier,
    slots: List<Task> = emptyList(),
    onBack: () -> Unit = {},
    newSlot: Task = Task(),
    onChange: (Task) -> Unit = {},
    onSave: () -> Unit = {}
) {
    var fromTime by remember(newSlot.id) { 
        mutableStateOf(if (newSlot.id != 0) formatMinutesToTime(newSlot.startTime) else "")
    }
    var toTime by remember(newSlot.id) { 
        mutableStateOf(if (newSlot.id != 0) formatMinutesToTime(newSlot.endTime) else "")
    }

    fun hasTimeOverlap(task: Task): Boolean {
        val fromMinutes = parseTimeToMinutes(fromTime) ?: return false
        val toMinutes = parseTimeToMinutes(toTime) ?: return false

        if (task.id == newSlot.id) return false

        return fromMinutes < task.endTime && toMinutes > task.startTime
    }
    
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(30.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondary,
            contentColor = MaterialTheme.colorScheme.onBackground
        )
    ) {
        Column(
            modifier = Modifier.padding(vertical = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Text(
                text = "Новый слот",
                style = MaterialTheme.typography.titleMedium
            )

            if (slots.isNotEmpty()) {
                LazyRow(
                    modifier = Modifier,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(horizontal = 20.dp)
                ) {
                    items(slots) { slot ->
                        val hasOverlap = hasTimeOverlap(slot)
                        Box(
                            modifier = Modifier
                                .height(20.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(
                                    if (hasOverlap) Color.Red.copy(alpha = 0.7f)
                                    else MaterialTheme.colorScheme.tertiary
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "${slot.name} " +
                                        "${formatMinutesToTime(slot.startTime)}-" +
                                        "${formatMinutesToTime(slot.endTime)}",
                                style = MaterialTheme.typography.labelMedium,
                                color = if (hasOverlap) Color.White else MaterialTheme.colorScheme.onBackground,
                                modifier = Modifier.padding(horizontal = 5.dp)
                            )
                        }
                    }
                }
            }

            WorkRestSwitch(
                modifier = Modifier.padding(horizontal = 20.dp),
                isWork = newSlot.isWork,
                onChanged = {
                    onChange(newSlot.copy(
                        isWork = it,
                        isSplittable = if (it) newSlot.isSplittable else false
                    ))
                }
            )

            BasicTextField(
                value = newSlot.name,
                onValueChange = { onChange(newSlot.copy(name = it)) },
                singleLine = true,
                textStyle = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.onBackground,
                    textAlign = TextAlign.Start
                ),
                modifier = Modifier
                    .padding(horizontal = 20.dp)
                    .fillMaxWidth()
                    .height(40.dp)
                    .background(Color(0xFFE8E8E8), RoundedCornerShape(20.dp)),
                decorationBox = { innerTextField ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 20.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        if (newSlot.name.isEmpty()) {
                            Text(
                                text = "Название задачи",
                                style = LocalTextStyle.current.copy(
                                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                                    textAlign = TextAlign.Start
                                )
                            )
                        }
                        innerTextField()
                    }
                }
            )


            TimeRangeInputManual(
                modifier = Modifier.padding(horizontal = 20.dp),
                from = fromTime,
                to = toTime,
                onFromTimeChanged = { 
                    fromTime = it
                    parseTimeToMinutes(it)?.let { minutes ->
                        onChange(newSlot.copy(startTime = minutes))
                    }
                },
                onToTimeChanged = { 
                    toTime = it
                    parseTimeToMinutes(it)?.let { minutes ->
                        onChange(newSlot.copy(endTime = minutes))
                    }
                },
            )

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 30.dp)
            ) {
                Text(
                    text = "Можно встроить перерыв",
                    style = MaterialTheme.typography.bodyMedium
                )
                Checkbox(
                    checked = newSlot.isSplittable,
                    onCheckedChange = { onChange(newSlot.copy(isSplittable = it)) },
                    enabled = newSlot.isWork,
                    colors = CheckboxDefaults.colors().copy(
                        checkedCheckmarkColor = MaterialTheme.colorScheme.onBackground,
                        uncheckedCheckmarkColor = Color.Transparent,
                        checkedBoxColor = Color.Transparent,
                        uncheckedBoxColor = Color.Transparent,
                        checkedBorderColor = MaterialTheme.colorScheme.onBackground,
                        uncheckedBorderColor = MaterialTheme.colorScheme.onBackground,
                        disabledCheckedBoxColor = Color.Transparent,
                        disabledUncheckedBoxColor = Color.Transparent,
                        disabledBorderColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.3f)
                    )
                )
            }
            Spacer(
                modifier = Modifier.weight(1f)
            )

            val fromMinutes = parseTimeToMinutes(fromTime)
            val toMinutes = parseTimeToMinutes(toTime)

            val hasAnyOverlap = slots.any { hasTimeOverlap(it) }
            
            val isValid = newSlot.name.isNotBlank() && 
                          fromMinutes != null && 
                          toMinutes != null &&
                          fromMinutes < toMinutes &&
                          !hasAnyOverlap

            val hasAnyInput = newSlot.name.isNotBlank() && fromTime.length == 5 && toTime.length == 5
            
            if (hasAnyInput && !isValid) {
                val errors = buildList {
                    if (newSlot.name.isBlank()) {
                        add("Введите название задачи")
                    }
                    if (fromMinutes == null && fromTime.isNotBlank()) {
                        add("Некорректное время начала")
                    } else if (fromMinutes == null) {
                        add("Укажите время начала")
                    }
                    if (toMinutes == null && toTime.isNotBlank()) {
                        add("Некорректное время окончания")
                    } else if (toMinutes == null) {
                        add("Укажите время окончания")
                    }
                    if (fromMinutes != null && toMinutes != null && fromMinutes >= toMinutes) {
                        add("Время начала должно быть раньше времени окончания")
                    }
                    if (fromMinutes != null && toMinutes != null && fromMinutes < toMinutes && hasAnyOverlap) {
                        add("Задача пересекается с другими задачами")
                    }
                }
                
                Column(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    errors.forEach { error ->
                        Text(
                            text = error,
                            style = MaterialTheme.typography.labelMedium,
                            color = Color.Red.copy(alpha = 0.8f),
                            textAlign = TextAlign.Center,
                        )
                    }
                }
            }
            
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp)
            ) {
                
                IconButton(
                    onClick = onSave,
                    enabled = isValid
                ) {
                    Icon(
                        painter = painterResource(Res.drawable.tick_circle),
                        contentDescription = null,
                        tint = if (isValid) MaterialTheme.colorScheme.onBackground 
                               else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.3f)
                    )
                }
                IconButton(
                    onClick = onBack
                ) {
                    Icon(
                        painter = painterResource(Res.drawable.close_circle),
                        contentDescription = null
                    )
                }
            }
        }

    }
}

@Preview()
@Composable
fun ScheduleScreenPreview() {
    AppTheme {
        NewSlot()
    }
}
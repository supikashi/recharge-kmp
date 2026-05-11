package com.supikashi.recharge.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.ui.input.pointer.pointerInput
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.key
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.supikashi.recharge.analytics.AnalyticsLogger
import com.supikashi.recharge.database.Task
import com.supikashi.recharge.components.TaskCard
import com.supikashi.recharge.components.TimeRangeInputManual
import com.supikashi.recharge.components.TopBar
import com.supikashi.recharge.components.WorkRestSwitch
import com.supikashi.recharge.components.OverlapWarningDialog
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
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel
import recharge.composeapp.generated.resources.Res
import recharge.composeapp.generated.resources.arrow_left
import recharge.composeapp.generated.resources.calendar
import recharge.composeapp.generated.resources.close_circle
import recharge.composeapp.generated.resources.home
import recharge.composeapp.generated.resources.mascot
import recharge.composeapp.generated.resources.schedule_add_task
import recharge.composeapp.generated.resources.schedule_allow_break
import recharge.composeapp.generated.resources.schedule_close_desc
import recharge.composeapp.generated.resources.schedule_delete_desc
import recharge.composeapp.generated.resources.schedule_edit_slot
import recharge.composeapp.generated.resources.schedule_empty_state
import recharge.composeapp.generated.resources.schedule_error_empty_end
import recharge.composeapp.generated.resources.schedule_error_empty_name
import recharge.composeapp.generated.resources.schedule_error_empty_start
import recharge.composeapp.generated.resources.schedule_error_invalid_end
import recharge.composeapp.generated.resources.schedule_error_invalid_start
import recharge.composeapp.generated.resources.schedule_error_overlap
import recharge.composeapp.generated.resources.schedule_error_start_after_end
import recharge.composeapp.generated.resources.schedule_new_slot
import recharge.composeapp.generated.resources.schedule_task_name_hint
import recharge.composeapp.generated.resources.tick_circle
import recharge.composeapp.generated.resources.trash
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalMaterial3Api::class, ExperimentalTime::class, InternalResourceApi::class)
@Composable
fun ScheduleScreen(
    onNavigateHome: () -> Unit = {},
    calendarResult: LocalDate? = null,
    onNavigateToCalendar: (LocalDate) -> Unit = {}
) {
    val viewModel: SlotViewModel = koinViewModel()
    val filteredTasks by viewModel.filteredTasks.collectAsStateWithLifecycle()
    val selectedDate by viewModel.selectedDate.collectAsStateWithLifecycle()

    val taskSaver = remember {
        listSaver<Task, Any>(
            save = { listOf(it.id, it.name, it.date.toEpochDays(), it.startTime, it.endTime, it.isWork, it.isSplittable) },
            restore = { Task(
                id = it[0] as Int,
                name = it[1] as String,
                date = LocalDate.fromEpochDays((it[2] as Number).toInt()),
                startTime = it[3] as Int,
                endTime = it[4] as Int,
                isWork = it[5] as Boolean,
                isSplittable = it[6] as Boolean
            ) }
        )
    }

    var newSlot by rememberSaveable(stateSaver = taskSaver) { mutableStateOf(Task()) }
    var isSlotCard by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(calendarResult) {
        calendarResult?.let {
            viewModel.updateSelectedDate(it)
            newSlot = newSlot.copy(date = it)
        }
    }

    Scaffold { paddingValues ->
        val focusManager = LocalFocusManager.current
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.background(MaterialTheme.colorScheme.mascotPrimary)
                .pointerInput(Unit) {
                    detectTapGestures(onTap = { focusManager.clearFocus() })
                }
                .padding(top = WindowInsets.safeDrawing.asPaddingValues().calculateTopPadding())
                .fillMaxSize()
        ) {
            TopBar(
                leftAction = { onNavigateToCalendar(selectedDate) },
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
                        AnalyticsLogger.logEvent("schedule_prev_day_clicked")
                        viewModel.updateSelectedDate(selectedDate.plus(-1, DateTimeUnit.DAY))
                        newSlot = newSlot.copy(date = selectedDate.plus(-1, DateTimeUnit.DAY))
                    }
                ) {
                    Icon(
                        painter = painterResource(Res.drawable.arrow_left),
                        contentDescription = null,
                    )
                }

                IconButton(
                    onClick = {
                        AnalyticsLogger.logEvent("schedule_next_day_clicked")
                        viewModel.updateSelectedDate(selectedDate.plus(1, DateTimeUnit.DAY))
                        newSlot = newSlot.copy(date = selectedDate.plus(1, DateTimeUnit.DAY))
                    }
                ) {
                    Icon(
                        painter = painterResource(Res.drawable.arrow_left),
                        contentDescription = null,
                        modifier = Modifier.rotate(180f)
                    )
                }
            }

            Box(
                modifier = Modifier
                    .clip(
                        RoundedCornerShape(
                            topStart = 30.dp,
                            topEnd = 30.dp
                        )
                    )
                    .background(MaterialTheme.colorScheme.background)
                    .fillMaxWidth()
                    
                    .weight(1f),


            ) {
                if (isSlotCard) {
                    NewSlot(
                        modifier = Modifier.align(Alignment.Center).padding(horizontal = 20.dp),
                        slots = filteredTasks?.map { it.task } ?: emptyList(),
                        newSlot = newSlot,
                        onChange = { newSlot = it },
                        onBack = {
                            AnalyticsLogger.logEvent("schedule_close_task_clicked")
                            isSlotCard = false
                            newSlot = Task(date = selectedDate)
                        },
                        onSave = {
                            AnalyticsLogger.logEvent("schedule_save_task_clicked")
                            viewModel.upsertTask(newSlot)
                            isSlotCard = false
                            newSlot = Task(date = selectedDate)
                        },
                        onDelete = {
                            AnalyticsLogger.logEvent("schedule_delete_task_clicked")
                            viewModel.deleteTask(newSlot)
                            isSlotCard = false
                            newSlot = Task(date = selectedDate)
                        }
                    )
                } else {
                    if (filteredTasks == null) {
                        
                        Spacer(modifier = Modifier.fillMaxSize())
                    } else if (filteredTasks!!.isEmpty()) {
                        Column(
                            modifier = Modifier.align(Alignment.Center),
                            verticalArrangement = Arrangement.spacedBy(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = stringResource(Res.string.schedule_empty_state),
                                style = MaterialTheme.typography.bodyMedium,
                                textAlign = TextAlign.Center
                            )
                            Icon(
                                painter = painterResource(Res.drawable.mascot),
                                contentDescription = null,
                                tint = Color.Unspecified,
                            )
                        }
                    } else {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 20.dp)
                                .verticalScroll(rememberScrollState()),
                            verticalArrangement = Arrangement.spacedBy(5.dp),
                        ) {
                            Spacer(Modifier.height(35.dp))
                            filteredTasks!!.forEach { slot ->
                                key(slot.task.id) {
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
                            Spacer(Modifier.height(110.dp))
                        }
                    }
                    Button(
                        onClick = {
                            AnalyticsLogger.logEvent("schedule_add_task_clicked")
                            isSlotCard = true
                        },
                        modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 40.dp),
                        colors = ButtonDefaults.buttonColors().copy(containerColor = MaterialTheme.colorScheme.onBackground)
                    ) {
                        Text(
                            text = stringResource(Res.string.schedule_add_task),
                            style = MaterialTheme.typography.bodyMedium
                        )
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
    onSave: () -> Unit = {},
    onDelete: () -> Unit = {}
) {
    var fromTime by remember(newSlot.id) {
        mutableStateOf(formatMinutesToTime(newSlot.startTime))
    }
    var toTime by remember(newSlot.id) {
        mutableStateOf(formatMinutesToTime(newSlot.endTime))
    }

    val fromMinutes = remember(fromTime) { parseTimeToMinutes(fromTime) }
    val toMinutes = remember(toTime) { parseTimeToMinutes(toTime) }

    fun hasTimeOverlap(task: Task): Boolean {
        if (fromMinutes == null || toMinutes == null) return false
        if (task.id == newSlot.id) return false
        return fromMinutes < task.endTime && toMinutes > task.startTime
    }

    fun isBlockingOverlap(existingTask: Task): Boolean {
        val overlapsTime = hasTimeOverlap(existingTask)
        return overlapsTime && (newSlot.isWork || !existingTask.isWork)
    }

    var showOverlapWarningDialog by remember { mutableStateOf(false) }

    if (showOverlapWarningDialog) {
        OverlapWarningDialog(
            onDismiss = { showOverlapWarningDialog = false },
            onConfirm = {
                showOverlapWarningDialog = false
                onSave()
            }
        )
    }
    
    val focusManager = LocalFocusManager.current
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState()),
    ) {
        Spacer(Modifier.height(40.dp))
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(30.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.secondary,
                contentColor = MaterialTheme.colorScheme.onBackground
            )
        ) {
            Column(
                modifier = Modifier
                    .padding(vertical = 20.dp)
                    .pointerInput(Unit) {
                        detectTapGestures(onTap = { focusManager.clearFocus() })
                    },
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier
                    ) {
                        Icon(
                            painter = painterResource(Res.drawable.close_circle),
                            contentDescription = stringResource(Res.string.schedule_close_desc),
                        )
                    }
                    Text(
                        text = if (newSlot.id == 0) stringResource(Res.string.schedule_new_slot) else stringResource(Res.string.schedule_edit_slot),
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    IconButton(
                        onClick = {  },
                        modifier = Modifier,
                        enabled = false
                    ) {
                        Icon(
                            painter = painterResource(Res.drawable.close_circle),
                            contentDescription = stringResource(Res.string.schedule_close_desc),
                            tint = Color.Transparent
                        )
                    }
                }

                if (slots.isNotEmpty()) {
                    LazyRow(
                        modifier = Modifier,
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        contentPadding = PaddingValues(horizontal = 20.dp)
                    ) {
                        items(slots) { slot ->
                            val hasOverlap = isBlockingOverlap(slot)
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
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                    keyboardActions = KeyboardActions(
                        onNext = { focusManager.moveFocus(androidx.compose.ui.focus.FocusDirection.Next) }
                    ),
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
                                    text = stringResource(Res.string.schedule_task_name_hint),
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
                        text = stringResource(Res.string.schedule_allow_break),
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

                val hasAnyOverlap = slots.any { isBlockingOverlap(it) }

                val isValid = newSlot.name.isNotBlank() &&
                        fromMinutes != null &&
                        toMinutes != null &&
                        fromMinutes < toMinutes &&
                        !hasAnyOverlap

                val hasAnyInput = newSlot.name.isNotBlank() && fromTime.length == 5 && toTime.length == 5

                if (hasAnyInput && !isValid) {
                    val errors = buildList {
                        if (newSlot.name.isBlank()) {
                            add(stringResource(Res.string.schedule_error_empty_name))
                        }
                        if (fromMinutes == null && fromTime.isNotBlank()) {
                            add(stringResource(Res.string.schedule_error_invalid_start))
                        } else if (fromMinutes == null) {
                            add(stringResource(Res.string.schedule_error_empty_start))
                        }
                        if (toMinutes == null && toTime.isNotBlank()) {
                            add(stringResource(Res.string.schedule_error_invalid_end))
                        } else if (toMinutes == null) {
                            add(stringResource(Res.string.schedule_error_empty_end))
                        }
                        if (fromMinutes != null && toMinutes != null && fromMinutes >= toMinutes) {
                            add(stringResource(Res.string.schedule_error_start_after_end))
                        }
                        if (fromMinutes != null && toMinutes != null && fromMinutes < toMinutes && hasAnyOverlap) {
                            add(stringResource(Res.string.schedule_error_overlap))
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
                    if (newSlot.id != 0) {
                        IconButton(
                            onClick = onDelete
                        ) {
                            Icon(
                                painter = painterResource(Res.drawable.trash),
                                contentDescription = stringResource(Res.string.schedule_delete_desc),
                                tint = Color.Red.copy(alpha = 0.8f)
                            )
                        }
                    } else {
                        Spacer(Modifier.width(0.dp))
                    }
                    IconButton(
                        onClick = {
                            val hasWorkOverlap = !newSlot.isWork && slots.any { it.isWork && hasTimeOverlap(it) }
                            if (hasWorkOverlap) {
                                showOverlapWarningDialog = true
                            } else {
                                onSave()
                            }
                        },
                        enabled = isValid
                    ) {
                        Icon(
                            painter = painterResource(Res.drawable.tick_circle),
                            contentDescription = null,
                            tint = if (isValid) MaterialTheme.colorScheme.onBackground
                            else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.3f)
                        )
                    }
                }
            }
        }
        Spacer(Modifier.height(40.dp))
    }
}

@Preview()
@Composable
fun ScheduleScreenPreview() {
    AppTheme {
        NewSlot()
    }
}
package com.supikashi.recharge.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.supikashi.recharge.analytics.AnalyticsLogger
import com.supikashi.recharge.components.BreakProgressChart
import com.supikashi.recharge.components.AchievementsSection
import com.supikashi.recharge.components.TaskCard
import com.supikashi.recharge.components.TopBar
import com.supikashi.recharge.database.PuzzleDayStatus
import com.supikashi.recharge.database.PuzzleDayStatusValue
import com.supikashi.recharge.database.Task
import com.supikashi.recharge.theme.mascotPrimary
import com.supikashi.recharge.utils.formatDate
import com.supikashi.recharge.utils.formatDayOfWeek
import com.supikashi.recharge.viewmodels.SlotViewModel
import com.supikashi.recharge.viewmodels.StatisticsViewModel
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus
import kotlinx.datetime.todayIn
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import recharge.composeapp.generated.resources.Res
import recharge.composeapp.generated.resources.arrow_left
import recharge.composeapp.generated.resources.calendar
import recharge.composeapp.generated.resources.home
import recharge.composeapp.generated.resources.stats_completion_good
import recharge.composeapp.generated.resources.stats_completion_great
import recharge.composeapp.generated.resources.stats_completion_perfect
import recharge.composeapp.generated.resources.stats_day_status_success
import recharge.composeapp.generated.resources.stats_day_status_fail
import recharge.composeapp.generated.resources.stats_empty_schedule_title
import recharge.composeapp.generated.resources.stats_record_mood_btn
import recharge.composeapp.generated.resources.stats_setup_schedule_btn
import recharge.composeapp.generated.resources.stats_title
import recharge.composeapp.generated.resources.stats_trend_negative
import recharge.composeapp.generated.resources.stats_trend_neutral
import recharge.composeapp.generated.resources.stats_trend_positive
import recharge.composeapp.generated.resources.stats_average_delay_title
import recharge.composeapp.generated.resources.stats_average_delay_format_min_sec
import recharge.composeapp.generated.resources.stats_average_delay_format_sec
import recharge.composeapp.generated.resources.stats_cancelled_breaks_title
import recharge.composeapp.generated.resources.stats_postponed_breaks_title
import recharge.composeapp.generated.resources.stats_puzzle_debug_locked
import recharge.composeapp.generated.resources.stats_puzzle_debug_no_saved_days
import recharge.composeapp.generated.resources.stats_puzzle_debug_no_status
import recharge.composeapp.generated.resources.stats_puzzle_debug_reset
import recharge.composeapp.generated.resources.stats_puzzle_debug_saved_days
import recharge.composeapp.generated.resources.stats_puzzle_debug_selected_day
import recharge.composeapp.generated.resources.stats_puzzle_debug_title
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import com.supikashi.recharge.components.MoodBarChart
import com.supikashi.recharge.components.SurveyDialog

@OptIn(ExperimentalMaterial3Api::class, ExperimentalTime::class)
@Composable
fun StatisticsScreen(
    onNavigateBack: () -> Unit,
    onNavigateToSchedule: (LocalDate) -> Unit, 
    calendarResult: LocalDate? = null,
    onNavigateToCalendar: (LocalDate) -> Unit = {},
    onNavigateToPuzzleCollection: () -> Unit = {},
    viewModel: StatisticsViewModel = koinViewModel()
) {
    val LocalDateSaver = Saver<LocalDate, Long>(
        save = { it.toEpochDays() },
        restore = { LocalDate.fromEpochDays(it.toInt()) }
    )

    var selectedDate by rememberSaveable(stateSaver = LocalDateSaver) { mutableStateOf(Clock.System.todayIn(TimeZone.currentSystemDefault())) }
    var showSurveyDialog by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(selectedDate) {
        viewModel.setSelectedDate(selectedDate)
    }

    LaunchedEffect(calendarResult) {
        calendarResult?.let {
            selectedDate = it
        }
    }
    
    val dailyStats by viewModel.dailyStats.collectAsStateWithLifecycle()
    val dailyMoodStats by viewModel.dailyMoodStats.collectAsStateWithLifecycle()
    val puzzles by viewModel.puzzles.collectAsStateWithLifecycle()
    val puzzleDayStatuses by viewModel.puzzleDayStatuses.collectAsStateWithLifecycle()
    val today = remember { Clock.System.todayIn(TimeZone.currentSystemDefault()) }
    
    Scaffold { paddingValues ->
        if (showSurveyDialog) {
            SurveyDialog(
                onDismiss = { showSurveyDialog = false },
                onSubmit = {
                    viewModel.saveMood(it)
                    showSurveyDialog = false
                }
            )
        }
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.background(MaterialTheme.colorScheme.mascotPrimary)
                .padding(top = WindowInsets.safeDrawing.asPaddingValues().calculateTopPadding())
                .fillMaxSize()
        ) {
            TopBar(
                leftAction = { onNavigateToCalendar(selectedDate) },
                leftIcon = Res.drawable.calendar,
                rightAction = onNavigateBack,
                rightIcon = Res.drawable.home,
                modifier = Modifier.padding(horizontal = 20.dp)
            )

            Text(
                text = stringResource(Res.string.stats_title),
                style = MaterialTheme.typography.headlineMedium
            )

            Spacer(Modifier.height(20.dp))

            Button(
                onClick = {
                    showSurveyDialog = true
                },
                colors = ButtonDefaults.buttonColors().copy(containerColor = MaterialTheme.colorScheme.onBackground),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .heightIn(min = 40.dp)
            ) {
                Text(
                    text = stringResource(Res.string.stats_record_mood_btn),
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Spacer(Modifier.height(20.dp))

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
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
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
                            AnalyticsLogger.logEvent("statistics_prev_day_clicked")
                            selectedDate = selectedDate.plus(-1, DateTimeUnit.DAY)
                        }
                    ) {
                        Icon(
                            painter = painterResource(Res.drawable.arrow_left),
                            contentDescription = null,
                        )
                    }

                    IconButton(
                        onClick = {
                            AnalyticsLogger.logEvent("statistics_next_day_clicked")
                            selectedDate = selectedDate.plus(1, DateTimeUnit.DAY)
                        }
                    ) {
                        Icon(
                            painter = painterResource(Res.drawable.arrow_left),
                            contentDescription = null,
                            modifier = Modifier.rotate(180f)
                        )
                    }
                }

                Spacer(Modifier.height(10.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 250.dp),
                    contentAlignment = Alignment.TopCenter
                ) {
                    if (dailyStats.totalBreaks == 0) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = stringResource(Res.string.stats_empty_schedule_title),
                                style = MaterialTheme.typography.titleMedium,
                                textAlign = TextAlign.Center
                            )
                            
                            Spacer(Modifier.height(40.dp))
                            
                            Button(
                                colors = ButtonDefaults.buttonColors().copy(
                                    contentColor = MaterialTheme.colorScheme.background,
                                    containerColor = MaterialTheme.colorScheme.onBackground
                                ),
                                onClick = {
                                    AnalyticsLogger.logEvent("statistics_setup_schedule_clicked")
                                    onNavigateToSchedule(selectedDate)
                                }
                            ) {
                                Text(
                                    text = stringResource(Res.string.stats_setup_schedule_btn),
                                    style = MaterialTheme.typography.bodyMedium,
                                )
                            }
                        }
                    } else {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            BreakProgressChart(
                                stats = dailyStats,
                            )

                            Spacer(Modifier.height(20.dp))

                            Text(
                                text = when {
                                    dailyStats.completionPercentage <= 0f -> ""
                                    dailyStats.completionPercentage < 0.5f -> stringResource(Res.string.stats_completion_good)
                                    dailyStats.completionPercentage < 1f -> stringResource(Res.string.stats_completion_great)
                                    else -> stringResource(Res.string.stats_completion_perfect)
                                },
                                style = MaterialTheme.typography.bodyMedium,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(horizontal = 35.dp)
                            )
                            
                            val currentDayStatus = puzzleDayStatuses.firstOrNull { it.date == selectedDate }?.status
                            if (currentDayStatus == PuzzleDayStatusValue.SUCCESS || currentDayStatus == PuzzleDayStatusValue.FAIL) {
                                Spacer(Modifier.height(10.dp))
                                Text(
                                    text = if (currentDayStatus == PuzzleDayStatusValue.SUCCESS) {
                                        stringResource(Res.string.stats_day_status_success)
                                    } else {
                                        stringResource(Res.string.stats_day_status_fail)
                                    },
                                    style = MaterialTheme.typography.bodyMedium,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(horizontal = 35.dp)
                                )
                            }
                        }
                    }
                }

                AchievementsSection(
                    puzzles = puzzles,
                    onOpenCollection = onNavigateToPuzzleCollection,
                    isStatisticsScreen = true,
                )

                Spacer(Modifier.height(20.dp))

                PuzzleDayStatusDebugSection(
                    selectedDate = selectedDate,
                    dayStatuses = puzzleDayStatuses,
                    onSetStatus = viewModel::setDebugPuzzleDayStatus,
                    onReset = viewModel::resetDebugPuzzleState
                )

                Spacer(Modifier.height(20.dp))

                val positiveTrend = stringResource(Res.string.stats_trend_positive)
                val negativeTrend = stringResource(Res.string.stats_trend_negative)
                val neutralTrend = stringResource(Res.string.stats_trend_neutral)

                val trendText = remember(dailyMoodStats, positiveTrend, negativeTrend, neutralTrend) {
                    val stats = dailyMoodStats.days.map { it.avg }
                    val n = stats.size
                    val prev = stats.subList(0, (n + 1) / 2).filter { it != 0f }.average()
                    val cur = stats.subList((n + 1) / 2, n).filter { it != 0f }.average()

                    when {
                        cur - 1 > prev -> positiveTrend
                        cur + 1 < prev -> negativeTrend
                        else -> neutralTrend
                    }
                }
                
                MoodBarChart(
                    stats = dailyMoodStats
                )
                
                Spacer(Modifier.height(10.dp))
                
                Text(
                    text = trendText,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 35.dp, vertical = 10.dp).heightIn(min = 60.dp)
                )

                if (dailyStats.averageDelaySeconds != null) {
                    Spacer(Modifier.height(10.dp))
                    Text(
                        text = stringResource(Res.string.stats_average_delay_title),
                        style = MaterialTheme.typography.titleMedium,
                        textAlign = TextAlign.Center
                    )
                    Spacer(Modifier.height(5.dp))

                    val delay = dailyStats.averageDelaySeconds!!
                    val minutes = delay / 60
                    val seconds = delay % 60
                    val delayText = if (minutes > 0) {
                        stringResource(Res.string.stats_average_delay_format_min_sec, minutes, seconds)
                    } else {
                        stringResource(Res.string.stats_average_delay_format_sec, seconds)
                    }

                    val color = when {
                        delay < 60 -> Color(0xFF4CAF50) // Green
                        delay > 300 -> Color(0xFFF44336) // Red
                        else -> MaterialTheme.colorScheme.onBackground
                    }

                    Text(
                        text = delayText,
                        style = MaterialTheme.typography.headlineMedium.copy(color = color),
                        textAlign = TextAlign.Center
                    )
                    Spacer(Modifier.height(20.dp))
                }

                if (dailyStats.totalBreaks > 0) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        BreakCountStat(
                            title = stringResource(Res.string.stats_postponed_breaks_title),
                            count = dailyStats.postponedBreaks,
                            modifier = Modifier.weight(1f)
                        )
                        BreakCountStat(
                            title = stringResource(Res.string.stats_cancelled_breaks_title),
                            count = dailyStats.cancelledBreaks,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun BreakCountStat(
    title: String,
    count: Int,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(MaterialTheme.colorScheme.secondary)
            .padding(horizontal = 12.dp, vertical = 14.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelMedium,
            textAlign = TextAlign.Center,
            minLines = 2
        )
        Text(
            text = count.toString(),
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center
        )
    }
}

@OptIn(ExperimentalTime::class)
@Composable
private fun PuzzleDayStatusDebugSection(
    selectedDate: LocalDate,
    dayStatuses: List<PuzzleDayStatus>,
    onSetStatus: (LocalDate, String) -> Unit,
    onReset: () -> Unit,
    modifier: Modifier = Modifier
) {
    val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
    val selectedStatus = dayStatuses.firstOrNull { it.date == selectedDate }?.status
    val canEditSelectedDate = selectedDate < today

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.secondary.copy(alpha = 0.35f))
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.16f),
                shape = RoundedCornerShape(8.dp)
            )
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(
            text = stringResource(Res.string.stats_puzzle_debug_title),
            style = MaterialTheme.typography.titleMedium
        )

        Text(
            text = stringResource(
                Res.string.stats_puzzle_debug_selected_day,
                formatDate(selectedDate),
                selectedStatus ?: stringResource(Res.string.stats_puzzle_debug_no_status)
            ),
            style = MaterialTheme.typography.bodyMedium
        )

        if (canEditSelectedDate) {
            PuzzleDayStatusDebugButtons(
                currentStatus = selectedStatus,
                onSetStatus = { status -> onSetStatus(selectedDate, status) }
            )
        } else {
            Text(
                text = stringResource(Res.string.stats_puzzle_debug_locked),
                style = MaterialTheme.typography.labelMedium
            )
        }

        Button(
            onClick = onReset,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFF44336),
                contentColor = Color.White
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = stringResource(Res.string.stats_puzzle_debug_reset),
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center
            )
        }

        Text(
            text = stringResource(Res.string.stats_puzzle_debug_saved_days),
            style = MaterialTheme.typography.bodyMedium
        )

        if (dayStatuses.isEmpty()) {
            Text(
                text = stringResource(Res.string.stats_puzzle_debug_no_saved_days),
                style = MaterialTheme.typography.labelMedium
            )
        } else {
            dayStatuses.forEach { dayStatus ->
                PuzzleDayStatusDebugRow(
                    dayStatus = dayStatus,
                    canEdit = dayStatus.date < today,
                    onSetStatus = { status -> onSetStatus(dayStatus.date, status) }
                )
            }
        }
    }
}

@Composable
private fun PuzzleDayStatusDebugRow(
    dayStatus: PuzzleDayStatus,
    canEdit: Boolean,
    onSetStatus: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.background.copy(alpha = 0.72f))
            .padding(10.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = formatDate(dayStatus.date),
                style = MaterialTheme.typography.bodyMedium
            )

            Text(
                text = dayStatus.status,
                style = MaterialTheme.typography.labelMedium
            )
        }

        if (canEdit) {
            PuzzleDayStatusDebugButtons(
                currentStatus = dayStatus.status,
                onSetStatus = onSetStatus
            )
        } else {
            Text(
                text = stringResource(Res.string.stats_puzzle_debug_locked),
                style = MaterialTheme.typography.labelMedium
            )
        }
    }
}

@Composable
private fun PuzzleDayStatusDebugButtons(
    currentStatus: String?,
    onSetStatus: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        listOf(
            PuzzleDayStatusValue.SUCCESS,
            PuzzleDayStatusValue.FAIL,
            PuzzleDayStatusValue.SKIPED
        ).forEach { status ->
            val isSelected = currentStatus == status

            Button(
                onClick = { onSetStatus(status) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isSelected) {
                        MaterialTheme.colorScheme.onBackground
                    } else {
                        MaterialTheme.colorScheme.background
                    },
                    contentColor = if (isSelected) {
                        MaterialTheme.colorScheme.background
                    } else {
                        MaterialTheme.colorScheme.onBackground
                    }
                ),
                modifier = Modifier.heightIn(min = 36.dp)
            ) {
                Text(
                    text = status,
                    style = MaterialTheme.typography.labelMedium
                )
            }
        }
    }
}

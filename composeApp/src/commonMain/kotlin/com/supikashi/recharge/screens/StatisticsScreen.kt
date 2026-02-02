package com.supikashi.recharge.screens

import androidx.compose.foundation.background
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.supikashi.recharge.components.BreakProgressChart
import com.supikashi.recharge.components.TaskCard
import com.supikashi.recharge.components.TopBar
import com.supikashi.recharge.database.Task
import com.supikashi.recharge.theme.mascotPrimary
import com.supikashi.recharge.utils.formatDate
import com.supikashi.recharge.utils.formatDayOfWeek
import com.supikashi.recharge.viewmodels.SlotViewModel
import com.supikashi.recharge.viewmodels.StatisticsViewModel
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus
import kotlinx.datetime.todayIn
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel
import recharge.composeapp.generated.resources.Res
import recharge.composeapp.generated.resources.arrow_back
import recharge.composeapp.generated.resources.arrow_left
import recharge.composeapp.generated.resources.calendar
import recharge.composeapp.generated.resources.home
import recharge.composeapp.generated.resources.mascot
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalMaterial3Api::class, ExperimentalTime::class)
@Composable
fun StatisticsScreen(
    onNavigateBack: () -> Unit,
    onNavigateToSchedule: () -> Unit,
    viewModel: StatisticsViewModel = koinViewModel()
) {
    var selectedDate by remember { mutableStateOf(Clock.System.todayIn(TimeZone.currentSystemDefault())) }
    
    LaunchedEffect(selectedDate) {
        viewModel.setSelectedDate(selectedDate)
    }
    
    val dailyStats by viewModel.dailyStats.collectAsStateWithLifecycle()
    
    Scaffold { paddingValues ->
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.background(MaterialTheme.colorScheme.mascotPrimary)
                .padding(top = WindowInsets.safeDrawing.asPaddingValues().calculateTopPadding())
                .fillMaxSize()
        ) {
            TopBar(
                leftAction = onNavigateBack,
                leftIcon = Res.drawable.arrow_back,
                rightAction = onNavigateBack,
                rightIcon = Res.drawable.home,
                modifier = Modifier.padding(horizontal = 20.dp)
            )

            Text(
                text = "СТАТИСТИКА",
                style = MaterialTheme.typography.headlineMedium
            )

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
                    .padding(20.dp)
                    .weight(1f),
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

                if (dailyStats.totalBreaks == 0) {
                    Spacer(Modifier.height(30.dp))
                    
                    Text(
                        text = "Пришло время настроить расписание отдыха!",
                        style = MaterialTheme.typography.titleMedium,
                        textAlign = TextAlign.Center
                    )
                    
                    Spacer(Modifier.height(40.dp))
                    
                    Button(
                        colors = ButtonDefaults.buttonColors().copy(
                            contentColor = MaterialTheme.colorScheme.background,
                            containerColor = MaterialTheme.colorScheme.onBackground
                        ),
                        onClick = onNavigateToSchedule
                    ) {
                        Text(
                            text = "Настроить расписание перерывов",
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    }
                } else {
                    BreakProgressChart(
                        stats = dailyStats,
                    )

                    Spacer(Modifier.height(20.dp))

                    Text(
                        text = "Отлично! Продолжай в том же духе.\nКаждый перерыв помогает заботиться\nо себе!",
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

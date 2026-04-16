package com.supikashi.recharge.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import com.supikashi.recharge.analytics.AnalyticsLogger
import com.supikashi.recharge.components.TopBar
import com.supikashi.recharge.theme.AppTheme
import com.supikashi.recharge.theme.mascotPrimary
import com.supikashi.recharge.utils.formatMonth
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.daysUntil
import kotlinx.datetime.isoDayNumber
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.todayIn
import org.jetbrains.compose.resources.InternalResourceApi
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import recharge.composeapp.generated.resources.Res
import recharge.composeapp.generated.resources.arrow_back
import recharge.composeapp.generated.resources.arrow_left
import recharge.composeapp.generated.resources.close_circle
import recharge.composeapp.generated.resources.home
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

private val WEEK_DAYS = listOf("пн", "вт", "ср", "чт", "пт", "сб", "вс")

@OptIn(InternalResourceApi::class)
@Composable
fun CalendarScreen(
    initialDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit,
    onBack: () -> Unit,
    onHome: () -> Unit,
) {
    var displayedMonthFirstDay by remember { 
        mutableStateOf(LocalDate(initialDate.year, initialDate.month, 1)) 
    }
    
    // Calculate calendar days
    val days = remember(displayedMonthFirstDay) {
        val daysInMonth = displayedMonthFirstDay.plus(1, DateTimeUnit.MONTH)
            .minus(1, DateTimeUnit.DAY).dayOfMonth
        
        val firstDayOfWeek = displayedMonthFirstDay.dayOfWeek.isoDayNumber // 1 (Mon) .. 7 (Sun)
        val offset = firstDayOfWeek - 1 // 0 (Mon) .. 6 (Sun)
        
        val list = mutableListOf<LocalDate?>()
        val year = displayedMonthFirstDay.year
        val month = displayedMonthFirstDay.month
        
        repeat(offset) { list.add(null) }
        for (day in 1..daysInMonth) {
            list.add(LocalDate(year, month, day))
        }
        
        val remainder = list.size % 7
        if (remainder != 0) {
            repeat(7 - remainder) { list.add(null) }
        }
        
        list
    }

    Scaffold { paddingValues ->
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.mascotPrimary)
                .padding(paddingValues)
        ) {
            TopBar(
                leftAction = onBack,
                rightAction = onHome,
                leftIcon = Res.drawable.arrow_back,
                rightIcon = Res.drawable.home,
                modifier = Modifier.padding(horizontal = 20.dp)
            )

            Column(
                modifier = Modifier
                    .padding(horizontal = 20.dp, vertical = 40.dp)
                    .clip(RoundedCornerShape(30.dp))
                    .background(MaterialTheme.colorScheme.background)
                    .fillMaxSize()
                    .padding(30.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    IconButton(
                        onClick = {
                            AnalyticsLogger.logEvent("calendar_prev_month_clicked")
                            displayedMonthFirstDay = displayedMonthFirstDay.minus(1, DateTimeUnit.MONTH)
                        }
                    ) {
                        Icon(
                            painter = painterResource(Res.drawable.arrow_left),
                            contentDescription = "Previous Month",
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }

                    Text(
                        text = "${formatMonth(displayedMonthFirstDay)} ${displayedMonthFirstDay.year}",
                        style = MaterialTheme.typography.titleMedium
                    )

                    IconButton(
                        onClick = {
                            AnalyticsLogger.logEvent("calendar_next_month_clicked")
                            displayedMonthFirstDay = displayedMonthFirstDay.plus(1, DateTimeUnit.MONTH)
                        }
                    ) {
                        Icon(
                            painter = painterResource(Res.drawable.arrow_left),
                            contentDescription = "Next Month",
                            modifier = Modifier.rotate(180f),
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    WEEK_DAYS.forEach { day ->
                        Text(
                            text = day,
                            modifier = Modifier.requiredWidth(30.dp),
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 10.dp),
                ) {
                    days.chunked(7).forEach { week ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            week.forEach { date ->
                                if (date != null) {
                                    val isSelected = date == initialDate

                                    Box(
                                        modifier = Modifier
                                            .requiredSize(30.dp)
                                            .clip(CircleShape)
                                            .background(
                                                if (isSelected) MaterialTheme.colorScheme.primary 
                                                else Color.Transparent
                                            )
                                            .clickable {
                                                AnalyticsLogger.logEvent("calendar_date_selected")
                                                onDateSelected(date)
                                            },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = date.dayOfMonth.toString(),
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = if (isSelected) MaterialTheme.colorScheme.onPrimary 
                                            else MaterialTheme.colorScheme.onBackground
                                        )
                                    }
                                } else {
                                    Spacer(modifier = Modifier.requiredSize(30.dp))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalTime::class)
@Preview
@Composable
fun CalendarScreenPreview() {
    AppTheme {
        CalendarScreen(
            Clock.System.todayIn(TimeZone.currentSystemDefault()),
            {},
            {},
            {}
        )
    }
}

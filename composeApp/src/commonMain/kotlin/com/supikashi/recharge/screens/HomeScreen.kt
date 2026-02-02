package com.supikashi.recharge.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.supikashi.recharge.viewmodels.HomeViewModel
import com.supikashi.recharge.theme.AppTheme
import com.supikashi.recharge.theme.mascotPrimary
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.vectorResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel
import recharge.composeapp.generated.resources.Res
import recharge.composeapp.generated.resources.avatar
import recharge.composeapp.generated.resources.frame_1
import recharge.composeapp.generated.resources.frame_1_png
import recharge.composeapp.generated.resources.frame_2
import recharge.composeapp.generated.resources.frame_2_png
import recharge.composeapp.generated.resources.frame_3
import recharge.composeapp.generated.resources.frame_3_png
import recharge.composeapp.generated.resources.notification

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToSchedule: () -> Unit = {},
    onNavigateToPomodoroSelection: () -> Unit = {},
    onNavigateToRest: () -> Unit = {},
    onNavigateToStatistics: () -> Unit = {},
    onNavigateToBreakNotification: () -> Unit = {},
) {
    val viewModel: HomeViewModel = koinViewModel()
    val isFirstScheduleVisit by viewModel.isFirstScheduleVisit.collectAsStateWithLifecycle()
    val currentBreak by viewModel.currentBreak.collectAsStateWithLifecycle()
    Scaffold { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.mascotPrimary)
                .padding(top = WindowInsets.safeDrawing.asPaddingValues().calculateTopPadding()),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Spacer(Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(Res.drawable.avatar),
                    contentDescription = "Avatar",
                    modifier = Modifier
                        .height(50.dp)
                        .padding(horizontal = 20.dp)
                        .clickable {
                            viewModel.resetCounter()
                        }
                )

                if (currentBreak != null) {
                    Button(
                        onClick = onNavigateToBreakNotification,
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp),
                        shape = RoundedCornerShape(
                            topStart = 10.dp,
                            bottomStart = 10.dp,
                        ),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.background,
                            contentColor = MaterialTheme.colorScheme.onBackground
                        ),
                        elevation = ButtonDefaults.buttonElevation(
                            defaultElevation = 10.dp,
                            pressedElevation = 15.dp
                        ),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Spacer(modifier = Modifier.width(10.dp))
                            Icon(
                                painter = painterResource(Res.drawable.notification),
                                contentDescription = null,
                                modifier = Modifier
                                    .size(30.dp),
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "Пора отдыхать!",
                                style = MaterialTheme.typography.titleMedium,
                            )
                        }
                    }
                }
            }
            Text(
                text = "ПРИВЕТ!",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(horizontal = 20.dp)
            )
            Text(
                text = "Чем займемся?",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(horizontal = 20.dp)
            )
            Spacer(modifier = Modifier.height(0.dp))
            BoxWithConstraints(modifier = Modifier.fillMaxWidth().weight(1f)) {
                val itemWidth = maxWidth * 0.8f
                LazyRow(
                    modifier = Modifier
                        .clip(
                            RoundedCornerShape(
                                topStart = 30.dp,
                                topEnd = 30.dp
                            )
                        )
                        .background(MaterialTheme.colorScheme.background),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(5.dp),
                    contentPadding = PaddingValues(top = 60.dp, start = 20.dp, end = 20.dp, bottom = 40.dp)
                ) {
                    item {
                        NavigationCard(
                            title = "Расписание",
                            description = "Заполни задачи, а мы поможем выстроить расписание перерывов!",
                            backgroundColor = MaterialTheme.colorScheme.tertiary,
                            contentColor = MaterialTheme.colorScheme.onBackground,
                            resource = Res.drawable.frame_1_png,
                            onClick = {
                                if (isFirstScheduleVisit) {
                                    onNavigateToPomodoroSelection()
                                } else {
                                    onNavigateToSchedule()
                                }
                            },
                            width = itemWidth,
                        )
                    }
                    item {
                        NavigationCard(
                            title = "Отдых",
                            description = "Подберем способ расслабиться на любой вкус!",
                            backgroundColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onSurface,
                            resource = Res.drawable.frame_2_png,
                            onClick = onNavigateToRest,
                            width = itemWidth,
                        )
                    }
                    item {
                        NavigationCard(
                            title = "Статистика",
                            description = "Здесь хранятся ваши заметки о самочувствии и достижения!",
                            backgroundColor = MaterialTheme.colorScheme.secondary,
                            contentColor = MaterialTheme.colorScheme.onBackground,
                            resource = Res.drawable.frame_3_png,
                            onClick = onNavigateToStatistics,
                            width = itemWidth,
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NavigationCard(
    title: String,
    description: String,
    backgroundColor: Color,
    contentColor: Color,
    resource: DrawableResource,
    onClick: () -> Unit,
    width: Dp,
) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .width(width)
            .fillMaxHeight(),
        shape = RoundedCornerShape(30.dp),
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor,
            contentColor = contentColor
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            verticalArrangement = Arrangement.Top
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineMedium
            )
            
            Spacer(modifier = Modifier.height(10.dp))
            
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.weight(1f)
            )

            Spacer(modifier = Modifier.height(10.dp))

            Image(
                painter = painterResource(resource),
                contentDescription = null,
            )
        }
    }
}

@Preview()
@Composable
fun HomeScreenPreview() {
    AppTheme {
        HomeScreen()
    }
}

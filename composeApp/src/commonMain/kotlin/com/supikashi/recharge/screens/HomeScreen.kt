package com.supikashi.recharge.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.supikashi.recharge.analytics.AnalyticsLogger
import com.supikashi.recharge.components.SurveyDialog
import com.supikashi.recharge.theme.AppTheme
import com.supikashi.recharge.theme.mascotPrimary
import com.supikashi.recharge.viewmodels.HomeViewModel
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
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
import recharge.composeapp.generated.resources.home_greeting
import recharge.composeapp.generated.resources.home_record_mood
import recharge.composeapp.generated.resources.home_rest_desc
import recharge.composeapp.generated.resources.home_rest_title
import recharge.composeapp.generated.resources.home_schedule_desc
import recharge.composeapp.generated.resources.home_schedule_title
import recharge.composeapp.generated.resources.home_stats_desc
import recharge.composeapp.generated.resources.home_stats_title
import recharge.composeapp.generated.resources.home_survey_text
import recharge.composeapp.generated.resources.home_take_survey
import recharge.composeapp.generated.resources.home_time_to_rest
import recharge.composeapp.generated.resources.home_what_to_do
import recharge.composeapp.generated.resources.notification

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToSchedule: () -> Unit = {},
    onNavigateToPomodoroSelection: () -> Unit = {},
    onNavigateToRest: () -> Unit = {},
    onNavigateToStatistics: () -> Unit = {},
    onNavigateToBreakNotification: () -> Unit = {},
    onNavigateToSettings: () -> Unit = {},
) {
    val viewModel: HomeViewModel = koinViewModel()
    val isFirstScheduleVisit by viewModel.isFirstScheduleVisit.collectAsStateWithLifecycle()
    val shouldShowHomeSurvey by viewModel.shouldShowHomeSurvey.collectAsStateWithLifecycle()
    val currentBreak by viewModel.currentBreak.collectAsStateWithLifecycle()
    
    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        viewModel.refreshCurrentBreak()
    }

    HomeScreenContent(
        isFirstScheduleVisit = isFirstScheduleVisit,
        shouldShowHomeSurvey = shouldShowHomeSurvey,
        hasCurrentBreak = currentBreak != null,
        onNavigateToSchedule = onNavigateToSchedule,
        onNavigateToPomodoroSelection = onNavigateToPomodoroSelection,
        onNavigateToRest = onNavigateToRest,
        onNavigateToStatistics = onNavigateToStatistics,
        onNavigateToBreakNotification = onNavigateToBreakNotification,
        onNavigateToSettings = onNavigateToSettings,
        onSaveMood = { viewModel.saveMood(it) }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreenContent(
    isFirstScheduleVisit: Boolean,
    shouldShowHomeSurvey: Boolean,
    hasCurrentBreak: Boolean,
    onNavigateToSchedule: () -> Unit = {},
    onNavigateToPomodoroSelection: () -> Unit = {},
    onNavigateToRest: () -> Unit = {},
    onNavigateToStatistics: () -> Unit = {},
    onNavigateToBreakNotification: () -> Unit = {},
    onNavigateToSettings: () -> Unit = {},
    onSaveMood: (Int) -> Unit = {},
) {
    var isVisible by rememberSaveable { mutableStateOf(false) }
    var showSurveyDialog by rememberSaveable { mutableStateOf(false) }
    val uriHandler = LocalUriHandler.current
    val alpha by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0f,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow)
    )

    LaunchedEffect(Unit) {
        isVisible = true
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        if (showSurveyDialog) {
            SurveyDialog(
                onDismiss = { showSurveyDialog = false },
                onSubmit = { 
                    onSaveMood(it)
                    showSurveyDialog = false 
                }
            )
        }
        
        Column(
            modifier = Modifier
                .alpha(alpha)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.mascotPrimary)
                .padding(top = WindowInsets.safeDrawing.asPaddingValues().calculateTopPadding()),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Spacer(Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Spacer(Modifier.width(20.dp))
                IconButton(
                    onClick = {
                        AnalyticsLogger.logEvent("home_avatar_clicked")
                        onNavigateToSettings()
                    },
                    modifier = Modifier
                        
                        .size(50.dp)

                ) {
                    Image(
                        painter = painterResource(Res.drawable.avatar),
                        contentDescription = "Avatar",
                        modifier = Modifier

                            .padding(3.dp)
                    )
                }
                Spacer(Modifier.width(20.dp))

                AnimatedVisibility(
                    visible = hasCurrentBreak,
                    enter = slideInHorizontally(initialOffsetX = { fullWidth -> fullWidth }),
                    exit = slideOutHorizontally(targetOffsetX = { fullWidth -> fullWidth }),
                    modifier = Modifier.weight(1f)
                ) {
                    Button(
                        onClick = {
                            AnalyticsLogger.logEvent("home_break_notification_clicked")
                            onNavigateToBreakNotification()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
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
                                text = stringResource(Res.string.home_time_to_rest),
                                style = MaterialTheme.typography.titleMedium,
                            )
                        }
                    }
                }
            }
            Text(
                text = stringResource(Res.string.home_greeting),
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(horizontal = 20.dp)
            )
            Text(
                text = stringResource(Res.string.home_what_to_do),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(horizontal = 20.dp)
            )
            Spacer(modifier = Modifier.height(0.dp))
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .clip(
                        RoundedCornerShape(
                            topStart = 40.dp,
                            topEnd = 40.dp
                        )
                    )
                    .background(MaterialTheme.colorScheme.background),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Spacer(Modifier.height(20.dp))
                if (shouldShowHomeSurvey) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .background(MaterialTheme.colorScheme.secondary)
                            .padding(10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = stringResource(Res.string.home_survey_text),
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onBackground,
                            textAlign = TextAlign.Center
                        )
                        Spacer(Modifier.height(10.dp))
                        Button(
                            onClick = {
                                uriHandler.openUri("https://forms.gle/uNm3wsPQeSnuMrtF8")
                            },
                            colors = ButtonDefaults.buttonColors().copy(containerColor = MaterialTheme.colorScheme.onBackground),
                            contentPadding = PaddingValues(horizontal = 30.dp),
                            modifier = Modifier.height(30.dp)
                        ) {
                            Text(
                                text = stringResource(Res.string.home_take_survey),
                                style = MaterialTheme.typography.labelMedium
                            )
                        }
                    }
                }
                Spacer(Modifier.height(20.dp))
                BoxWithConstraints(modifier = Modifier.fillMaxWidth().weight(1f)) {
                    val itemWidth = maxWidth * 0.8f
                    LazyRow(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(5.dp),
                        contentPadding = PaddingValues(start = 20.dp, end = 20.dp)
                    ) {
                        item {
                            NavigationCard(
                                title = stringResource(Res.string.home_schedule_title),
                                description = stringResource(Res.string.home_schedule_desc),
                                backgroundColor = MaterialTheme.colorScheme.tertiary,
                                contentColor = MaterialTheme.colorScheme.onBackground,
                                resource = Res.drawable.frame_1,
                                onClick = {
                                    AnalyticsLogger.logEvent("home_open_schedule_clicked")
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
                                title = stringResource(Res.string.home_rest_title),
                                description = stringResource(Res.string.home_rest_desc),
                                backgroundColor = MaterialTheme.colorScheme.primary,
                                contentColor = MaterialTheme.colorScheme.onSurface,
                                resource = Res.drawable.frame_2,
                                onClick = {
                                    AnalyticsLogger.logEvent("home_open_rest_clicked")
                                    onNavigateToRest()
                                },
                                width = itemWidth,
                            )
                        }
                        item {
                            NavigationCard(
                                title = stringResource(Res.string.home_stats_title),
                                description = stringResource(Res.string.home_stats_desc),
                                backgroundColor = MaterialTheme.colorScheme.secondary,
                                contentColor = MaterialTheme.colorScheme.onBackground,
                                resource = Res.drawable.frame_3,
                                onClick = {
                                    AnalyticsLogger.logEvent("home_open_statistics_clicked")
                                    onNavigateToStatistics()
                                },
                                width = itemWidth,
                            )
                        }
                    }
                }
                Button(
                    onClick = {
                        showSurveyDialog = true
                    },
                    colors = ButtonDefaults.buttonColors().copy(containerColor = MaterialTheme.colorScheme.onBackground),
                    modifier = Modifier.fillMaxWidth().padding(top = 20.dp, bottom = 40.dp, start = 20.dp, end = 20.dp).heightIn(min = 40.dp)
                ) {
                    Text(
                        text = stringResource(Res.string.home_record_mood),
                        style = MaterialTheme.typography.bodyMedium
                    )
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
        shape = RoundedCornerShape(20.dp),
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
            )

            Spacer(modifier = Modifier.height(10.dp))

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.BottomCenter
            ) {
                Image(
                    painter = painterResource(resource),
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                )
            }
        }
    }
}

@Preview()
@Composable
fun HomeScreenPreview() {
    AppTheme {
        HomeScreenContent(
            isFirstScheduleVisit = false,
            shouldShowHomeSurvey = true,
            hasCurrentBreak = false
        )
    }
}

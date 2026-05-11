package com.supikashi.recharge

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.Transition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.supikashi.recharge.database.TaskDao
import com.supikashi.recharge.models.RestType
import com.supikashi.recharge.navigation.BreakResultType
import com.supikashi.recharge.navigation.Screen
import com.supikashi.recharge.notifications.RequestNotificationPermission
import com.supikashi.recharge.components.NotificationPermissionDialog
import com.supikashi.recharge.screens.BreakNotificationScreen
import com.supikashi.recharge.screens.BreakResultScreen
import com.supikashi.recharge.screens.CalendarScreen
import com.supikashi.recharge.screens.HomeScreen
import com.supikashi.recharge.screens.OnboardingScreen
import com.supikashi.recharge.screens.PomodoroSelectionScreen
import com.supikashi.recharge.screens.RestActivitiesListScreen
import com.supikashi.recharge.screens.RestActivitiesScreen
import com.supikashi.recharge.screens.RestScreen
import com.supikashi.recharge.screens.ScheduleScreen
import com.supikashi.recharge.screens.SettingsScreen
import com.supikashi.recharge.screens.StatisticsScreen
import com.supikashi.recharge.theme.AppTheme
import com.supikashi.recharge.utils.rememberDebounceClickHandler
import com.supikashi.recharge.utils.rememberOpenAppSettings
import com.supikashi.recharge.viewmodels.NotificationViewModel
import com.supikashi.recharge.viewmodels.SettingsViewModel
import kotlinx.coroutines.delay
import kotlinx.datetime.LocalDate
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.koin.compose.viewmodel.koinViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.supikashi.recharge.models.AppLanguage

fun NavController.navigateWithFlags(
    route: Any,
    builder: NavOptionsBuilder.() -> Unit = {}
) {
    navigate(route) {
        launchSingleTop = true
        restoreState = true
        builder()
    }
}

@OptIn(ExperimentalResourceApi::class)
@Composable
fun App(
    taskDao: TaskDao,
    shouldOpenBreakNotification: Boolean = false,
    onBreakNotificationNavigated: () -> Unit = {}
) {
    val settingsViewModel: SettingsViewModel = koinViewModel()
    val language by settingsViewModel.appLanguage.collectAsStateWithLifecycle()

    val currentAppLocale = remember(language) {
        when (language) {
            AppLanguage.SYSTEM -> null
            AppLanguage.RUSSIAN -> "ru"
            AppLanguage.ENGLISH -> "en"
        }
    }

    AppEnvironment(appLocale = currentAppLocale) {
        AppTheme {
            val isIOS = getPlatform().name.contains("iOS")
        val navController = rememberNavController()
        val notificationViewModel : NotificationViewModel = koinViewModel()

        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry?.destination

        LaunchedEffect(shouldOpenBreakNotification, currentDestination) {
            if (shouldOpenBreakNotification && currentDestination != null) {
                val isOnboarding = currentDestination.route?.contains("Onboarding") == true
                
                if (!isOnboarding) {
                    delay(300)
                    navController.navigateWithFlags(Screen.BreakNotification)
                    onBreakNotificationNavigated()
                }
            }
        }

        var showPermissionDialog by remember { mutableStateOf(false) }
        val openAppSettings = rememberOpenAppSettings()

        RequestNotificationPermission { granted ->
            notificationViewModel.setNotificationPermission(granted)
            if (!granted) {
                showPermissionDialog = true
            }
        }

        if (showPermissionDialog) {
            NotificationPermissionDialog(
                onDismiss = { showPermissionDialog = false },
                onConfirm = {
                    showPermissionDialog = false
                    openAppSettings()
                }
            )
        }

        val debouncedNavigateToHome = rememberDebounceClickHandler {
            navController.popBackStack(Screen.Home, false)
        }
        val debouncedNavigateToSchedule = rememberDebounceClickHandler {
            navController.navigateWithFlags(Screen.Schedule)
        }
        val debouncedNavigateToPomodoroSelection = rememberDebounceClickHandler {
            navController.navigateWithFlags(Screen.PomodoroSelection(isFromSettings = false))
        }
        val debouncedNavigateToPomodoroSelectionFromSettings = rememberDebounceClickHandler {
            navController.navigateWithFlags(Screen.PomodoroSelection(isFromSettings = true))
        }
        val debouncedNavigateToRest = rememberDebounceClickHandler {
            navController.navigateWithFlags(Screen.Rest)
        }
        val debouncedNavigateToStatistics = rememberDebounceClickHandler {
            navController.navigateWithFlags(Screen.Statistics)
        }
        val debouncedNavigateToScheduleFromPomodoro = rememberDebounceClickHandler {
            navController.navigateWithFlags(Screen.Schedule) {
                popUpTo(Screen.Home)
            }
        }
        val debouncedNavigateToBreakNotification = rememberDebounceClickHandler {
            navController.navigateWithFlags(Screen.BreakNotification)
        }
        val debouncedNavigateToRestFromBreak = rememberDebounceClickHandler {
            navController.navigateWithFlags(Screen.Rest) {
                popUpTo(Screen.Home)
            }
        }
        val debouncedNavigateToSettings = rememberDebounceClickHandler {
            navController.navigateWithFlags(Screen.Settings)
        }
        val debouncedNavigateToOnboarding = rememberDebounceClickHandler {
            navController.navigateWithFlags(Screen.Onboarding(isFromSettings = true))
        }

        var pendingRestType: String? = null
        val debouncedNavigateToRestActivities = rememberDebounceClickHandler {
            pendingRestType?.let { type ->
                navController.navigateWithFlags(Screen.RestActivities(type))
            }
        }

        var pendingBreakResult: Pair<String, Int>? = null
        val debouncedNavigateToBreakResult = rememberDebounceClickHandler {
            pendingBreakResult?.let { (type, duration) ->
                navController.navigateWithFlags(Screen.BreakResult(type, duration)) {
                    popUpTo(Screen.Home)
                }
            }
        }

        var pendingDateEpochDays: Long? = null
        val debouncedNavigateToCalendar = rememberDebounceClickHandler {
            pendingDateEpochDays?.let { date ->
                navController.navigateWithFlags(Screen.Calendar(date))
            }
        }

        var pendingRestActivitiesListType: String? = null
        val debouncedNavigateToList = rememberDebounceClickHandler {
            pendingRestActivitiesListType?.let { type ->
                navController.navigateWithFlags(Screen.RestActivitiesList(type))
            }
        }
        
        val debouncedNavigateUp = rememberDebounceClickHandler {
            navController.navigateUp()
        }

        NavHost(
            modifier = Modifier.background(MaterialTheme.colorScheme.background),
            navController = navController,
            startDestination = Screen.Onboarding(isFromSettings = false),
            enterTransition = {
                slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Start,
                    animationSpec = tween(
                        durationMillis = 200,
                        easing = LinearEasing
                    )
                )
            },
            exitTransition = {
                slideOutOfContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Start,
                    animationSpec = tween(
                        durationMillis = 200,
                        easing = LinearEasing
                    ),
                    targetOffset = { fullOffset -> (fullOffset * 0.3f).toInt() }
                )
            },
            popEnterTransition = {
                slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.End,
                    animationSpec = tween(
                        durationMillis = 200,
                        easing = LinearEasing
                    ),
                    initialOffset = { fullOffset -> (fullOffset * 0.3f).toInt() }
                )
            },
            popExitTransition = {
                slideOutOfContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.End,
                    animationSpec = tween(
                        durationMillis = 200,
                        easing = LinearEasing
                    )
                )
            }
        ) {
            composable<Screen.Onboarding> { backStackEntry ->
                val route = backStackEntry.toRoute<Screen.Onboarding>()
                println("is from settings${route.isFromSettings}")
                OnboardingScreen(
                    onNavigateToHome = {
                        if (route.isFromSettings) {
                            debouncedNavigateUp()
                        } else {
                            navController.navigate(Screen.Home) {
                                popUpTo(Screen.Onboarding(isFromSettings = false)) { inclusive = true }
                            }
                        }
                    },
                    isFromSettings = route.isFromSettings
                )
            }

            composable<Screen.Home>(
                enterTransition = { EnterTransition.None },
                popEnterTransition = {
                    slideIntoContainer(
                        towards = AnimatedContentTransitionScope.SlideDirection.End,
                        animationSpec = tween(
                            durationMillis = 200,
                            easing = LinearEasing
                        ),
                        initialOffset = { fullOffset -> (fullOffset * 0.3f).toInt() }
                    )
                }
            ) {
                HomeScreen(
                    onNavigateToSchedule = debouncedNavigateToSchedule,
                    onNavigateToPomodoroSelection = debouncedNavigateToPomodoroSelection,
                    onNavigateToRest = debouncedNavigateToRest,
                    onNavigateToStatistics = debouncedNavigateToStatistics,
                    onNavigateToBreakNotification = debouncedNavigateToBreakNotification,
                    onNavigateToSettings = debouncedNavigateToSettings
                )
            }

            composable<Screen.PomodoroSelection> { backStackEntry ->
                val route = backStackEntry.toRoute<Screen.PomodoroSelection>()
                PomodoroSelectionScreen(
                    onPomodoroSelected = if (route.isFromSettings) {
                        debouncedNavigateUp
                    } else {
                        { debouncedNavigateToScheduleFromPomodoro() }
                    },
                    onNavigateHome = debouncedNavigateToHome,
                    onNavigateBack = debouncedNavigateUp
                )
            }

            composable<Screen.Schedule> { backStackEntry ->
                val calendarResult = backStackEntry.savedStateHandle.get<Long>("selected_date")
                    ?.let { LocalDate.fromEpochDays(it) }

                ScheduleScreen(
                    onNavigateHome = debouncedNavigateToHome,
                    calendarResult = calendarResult,
                    onNavigateToCalendar = { date ->
                        backStackEntry.savedStateHandle.remove<Long>("selected_date")
                        pendingDateEpochDays = date.toEpochDays().toLong()
                        debouncedNavigateToCalendar()
                    }
                )
            }

            composable<Screen.Calendar> { backStackEntry ->
                val route = backStackEntry.toRoute<Screen.Calendar>()
                val initialDate = LocalDate.fromEpochDays(route.selectedDateEpochDays.toInt())

                CalendarScreen(
                    initialDate = initialDate,
                    onDateSelected = { date ->
                        navController.previousBackStackEntry
                            ?.savedStateHandle
                            ?.set("selected_date", date.toEpochDays())
                        navController.popBackStack()
                    },
                    onBack = debouncedNavigateUp,
                    onHome = debouncedNavigateToHome
                )
            }

            composable<Screen.Rest> {
                RestScreen(
                    onNavigateHome = debouncedNavigateToHome,
                    onNavigateBack = debouncedNavigateUp,
                    onNavigateToType = { type ->
                        pendingRestType = type.name
                        debouncedNavigateToRestActivities()
                    }
                )
            }

            composable<Screen.RestActivities> { backStackEntry ->
                val route = backStackEntry.toRoute<Screen.RestActivities>()
                val type = RestType.valueOf(route.type)
                
                RestActivitiesScreen(
                    type = type,
                    onNavigateToList = {
                        pendingRestActivitiesListType = type.name
                        debouncedNavigateToList()
                    },
                    onNavigateBack = debouncedNavigateUp
                )
            }

            composable<Screen.RestActivitiesList> { backStackEntry ->
                val route = backStackEntry.toRoute<Screen.RestActivitiesList>()
                val type = RestType.valueOf(route.type)
                
                RestActivitiesListScreen(
                    type = type,
                    onNavigateToCardView = debouncedNavigateUp,
                    onNavigateBack = debouncedNavigateUp
                )
            }

            composable<Screen.Statistics> { backStackEntry ->
                val calendarResult = backStackEntry.savedStateHandle.get<Long>("selected_date")
                    ?.let { LocalDate.fromEpochDays(it) }

                StatisticsScreen(
                    onNavigateBack = debouncedNavigateUp,
                    onNavigateToSchedule = {
                        backStackEntry.savedStateHandle.remove<Int>("selected_date")
                        debouncedNavigateToSchedule()
                    },
                    calendarResult = calendarResult,
                    onNavigateToCalendar = { date ->
                        backStackEntry.savedStateHandle.remove<Long>("selected_date")
                        pendingDateEpochDays = date.toEpochDays().toLong()
                        debouncedNavigateToCalendar()
                    }
                )
            }

            composable<Screen.BreakNotification> {
                BreakNotificationScreen(
                    onNavigateBack = debouncedNavigateUp,
                    onNavigateToRest = debouncedNavigateToRestFromBreak,
                    onNavigateHome = debouncedNavigateToHome,
                    onNavigateToBreakResult = { type, duration ->
                        pendingBreakResult = type to duration
                        debouncedNavigateToBreakResult()
                    }
                )
            }

            composable<Screen.BreakResult> { backStackEntry ->
                val route = backStackEntry.toRoute<Screen.BreakResult>()
                val resultType = BreakResultType.valueOf(route.type)
                BreakResultScreen(
                    resultType = resultType,
                    durationMinutes = route.durationMinutes,
                    onNavigateHome = debouncedNavigateToHome,
                    onNavigateToRest = debouncedNavigateToRestFromBreak
                )
            }

            composable<Screen.Settings> {
                SettingsScreen(
                    onNavigateBack = debouncedNavigateUp,
                    onNavigateToPomodoroSelection = debouncedNavigateToPomodoroSelectionFromSettings,
                    onNavigateToOnboarding = debouncedNavigateToOnboarding
                )
            }
        }
    }
    }
}

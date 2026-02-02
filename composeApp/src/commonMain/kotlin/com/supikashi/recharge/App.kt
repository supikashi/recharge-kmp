package com.supikashi.recharge

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.supikashi.recharge.database.TaskDao
import com.supikashi.recharge.models.RestType
import com.supikashi.recharge.navigation.BreakResultType
import com.supikashi.recharge.navigation.Screen
import com.supikashi.recharge.notifications.RequestNotificationPermission
import com.supikashi.recharge.screens.BreakNotificationScreen
import com.supikashi.recharge.screens.BreakResultScreen
import com.supikashi.recharge.screens.HomeScreen
import com.supikashi.recharge.screens.OnboardingScreen
import com.supikashi.recharge.screens.PomodoroSelectionScreen
import com.supikashi.recharge.screens.RestActivitiesScreen
import com.supikashi.recharge.screens.RestScreen
import com.supikashi.recharge.screens.ScheduleScreen
import com.supikashi.recharge.screens.StatisticsScreen
import com.supikashi.recharge.theme.AppTheme
import com.supikashi.recharge.utils.rememberDebounceClickHandler
import com.supikashi.recharge.viewmodels.NotificationViewModel
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel


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
@Preview
fun App(
    taskDao: TaskDao,
    shouldOpenBreakNotification: Boolean = false
) {
    AppTheme {
        val navController = rememberNavController()
        val notificationViewModel : NotificationViewModel = koinViewModel()

        androidx.compose.runtime.LaunchedEffect(shouldOpenBreakNotification) {
            if (shouldOpenBreakNotification) {
                navController.navigate(Screen.BreakNotification)
            }
        }

        RequestNotificationPermission { granted ->
            notificationViewModel.setNotificationPermission(granted)
        }

        val debouncedNavigateToHome = rememberDebounceClickHandler {
            navController.popBackStack(Screen.Home, false)
        }
        val debouncedNavigateToSchedule = rememberDebounceClickHandler {
            navController.navigateWithFlags(Screen.Schedule)
        }
        val debouncedNavigateToPomodoroSelection = rememberDebounceClickHandler {
            navController.navigateWithFlags(Screen.PomodoroSelection)
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

        NavHost(
            navController = navController,
            startDestination = Screen.Onboarding
        ) {
            composable<Screen.Onboarding>(
                exitTransition = { fadeOut() }
            ) {
                OnboardingScreen(
                    onNavigateToHome = {
                        navController.navigate(Screen.Home) {
                            popUpTo(Screen.Onboarding) { inclusive = true }
                        }
                    }
                )
            }

            composable<Screen.Home>(
                enterTransition = { fadeIn() }
            ) {
                HomeScreen(
                    onNavigateToSchedule = debouncedNavigateToSchedule,
                    onNavigateToPomodoroSelection = debouncedNavigateToPomodoroSelection,
                    onNavigateToRest = debouncedNavigateToRest,
                    onNavigateToStatistics = debouncedNavigateToStatistics,
                    onNavigateToBreakNotification = debouncedNavigateToBreakNotification
                )
            }

            composable<Screen.PomodoroSelection> {
                PomodoroSelectionScreen(
                    onPomodoroSelected = debouncedNavigateToScheduleFromPomodoro,
                    onNavigateHome = debouncedNavigateToHome,
                    onNavigateBack = {
                        navController.navigateUp()
                    }
                )
            }

            composable<Screen.Schedule> {
                ScheduleScreen(
                    onNavigateHome = debouncedNavigateToHome,
                )
            }

            composable<Screen.Rest> {
                RestScreen(
                    onNavigateHome = debouncedNavigateToHome,
                    onNavigateBack = {
                        navController.navigateUp()
                    },
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
                    onNavigateHome = debouncedNavigateToHome,
                    onNavigateBack = {
                        navController.navigateUp()
                    }
                )
            }

            composable<Screen.Statistics> {
                StatisticsScreen(
                    onNavigateBack = {
                        navController.navigateUp()
                    },
                    onNavigateToSchedule = debouncedNavigateToSchedule
                )
            }

            composable<Screen.BreakNotification> {
                BreakNotificationScreen(
                    onNavigateBack = {
                        navController.navigateUp()
                    },
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
        }
    }
}

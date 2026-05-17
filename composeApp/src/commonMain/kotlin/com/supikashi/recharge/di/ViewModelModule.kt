package com.supikashi.recharge.di

import com.supikashi.recharge.viewmodels.BreakNotificationViewModel
import com.supikashi.recharge.viewmodels.HomeViewModel
import com.supikashi.recharge.viewmodels.NotificationViewModel
import com.supikashi.recharge.viewmodels.OnboardingViewModel
import com.supikashi.recharge.viewmodels.PomodoroSelectionViewModel
import com.supikashi.recharge.viewmodels.RestActivitiesViewModel
import com.supikashi.recharge.viewmodels.SettingsViewModel
import com.supikashi.recharge.viewmodels.SlotViewModel
import com.supikashi.recharge.viewmodels.StatisticsViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { OnboardingViewModel(get()) }
    viewModel { HomeViewModel(get(), get()) }
    viewModel { PomodoroSelectionViewModel(get(), get()) }
    viewModel { SlotViewModel(get(), get()) }
    viewModel { NotificationViewModel(get(), get(), get()) }
    viewModel { BreakNotificationViewModel(get(), get(), get()) }
    viewModel { StatisticsViewModel(get()) }
    viewModel { SettingsViewModel(get()) }
    viewModel { RestActivitiesViewModel(get()) }
}

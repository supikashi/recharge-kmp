package com.supikashi.recharge.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.supikashi.recharge.data.UserPreferencesRepository
import com.supikashi.recharge.models.AppLanguage
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    val appLanguage: StateFlow<AppLanguage> = userPreferencesRepository.appLanguage
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = AppLanguage.SYSTEM
        )

    fun setLanguage(language: AppLanguage) {
        viewModelScope.launch {
            userPreferencesRepository.setAppLanguage(language)
        }
    }

    fun toggleLanguage() {
        viewModelScope.launch {
            userPreferencesRepository.setAppLanguage(appLanguage.value.next())
        }
    }

    fun resetAllPreferences() {
        viewModelScope.launch {
            userPreferencesRepository.resetAllPreferences()
        }
    }
    fun resetOnboarding() {
        viewModelScope.launch {
            userPreferencesRepository.setOnboardingCompleted(false)
        }
    }
}

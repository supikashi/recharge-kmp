package com.supikashi.recharge.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.supikashi.recharge.data.UserPreferencesRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed class OnboardingState {
    data object Loading : OnboardingState()
    data object ShowOnboarding : OnboardingState()
    data object OnboardingCompleted : OnboardingState() 
    data object NavigateToHome : OnboardingState() 
}

class OnboardingViewModel(
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    private val _onboardingState = MutableStateFlow<OnboardingState>(OnboardingState.Loading)
    val onboardingState: StateFlow<OnboardingState> = _onboardingState.asStateFlow()

    private val _currentPage = MutableStateFlow(0)
    val currentPage: StateFlow<Int> = _currentPage.asStateFlow()

    val totalPages = 3

    init {
        checkOnboardingStatus()
    }

    private fun checkOnboardingStatus() {
        viewModelScope.launch {
            delay(1000)
            _onboardingState.value = if (userPreferencesRepository.isOnboardingCompleted.first()) {
                OnboardingState.NavigateToHome
            } else {
                OnboardingState.ShowOnboarding
            }
        }
    }

    fun nextPage() {
        if (_currentPage.value < totalPages - 1) {
            _currentPage.value++
        } else {
            completeOnboarding()
        }
    }

    fun goToPage(page: Int) {
        if (page in 0 until totalPages) {
            _currentPage.value = page
        }
    }

    private fun completeOnboarding() {
        viewModelScope.launch {
            userPreferencesRepository.setOnboardingCompleted(true)
            _onboardingState.value = OnboardingState.OnboardingCompleted
        }
    }
}

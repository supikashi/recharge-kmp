package com.supikashi.recharge.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.supikashi.recharge.data.RestActivitiesRepository
import com.supikashi.recharge.models.RestActivity
import com.supikashi.recharge.models.RestType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class RestActivitiesUiState(
    val activities: List<RestActivity> = emptyList(),
    val isLoading: Boolean = false,
    val isError: Boolean = false
)

class RestActivitiesViewModel(
    private val repository: RestActivitiesRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(RestActivitiesUiState(isLoading = true))
    val uiState: StateFlow<RestActivitiesUiState> = _uiState.asStateFlow()

    private var lastRequest: Pair<RestType, String>? = null

    fun loadActivities(type: RestType, locale: String) {
        val request = type to locale
        if (lastRequest == request && !_uiState.value.isError) return

        lastRequest = request
        _uiState.value = RestActivitiesUiState(isLoading = true)

        viewModelScope.launch {
            runCatching {
                repository.getActivitiesForType(type, locale)
            }.onSuccess { activities ->
                _uiState.value = RestActivitiesUiState(activities = activities)
            }.onFailure {
                _uiState.value = RestActivitiesUiState(isError = true)
            }
        }
    }
}

package com.supikashi.recharge.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import com.supikashi.recharge.models.PomodoroType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class UserPreferencesRepository(
    private val dataStore: DataStore<Preferences>
) {
    companion object {
        private val CLICK_COUNTER_KEY = intPreferencesKey("click_counter")
        private val FIRST_SCHEDULE_VISIT_KEY = booleanPreferencesKey("first_schedule_visit")
        private val SELECTED_POMODORO_TYPE_KEY = intPreferencesKey("selected_pomodoro_type")
        private val ONBOARDING_COMPLETED_KEY = booleanPreferencesKey("onboarding_completed")
    }

    val isFirstScheduleVisit: Flow<Boolean> = dataStore.data
        .map { preferences -> preferences[FIRST_SCHEDULE_VISIT_KEY] ?: true }

    suspend fun setFirstScheduleVisit(isFirst: Boolean) {
        dataStore.edit { preferences ->
            preferences[FIRST_SCHEDULE_VISIT_KEY] = isFirst
        }
    }

    val isOnboardingCompleted: Flow<Boolean> = dataStore.data
        .map { preferences -> preferences[ONBOARDING_COMPLETED_KEY] ?: false }

    suspend fun setOnboardingCompleted(completed: Boolean) {
        dataStore.edit { preferences ->
            preferences[ONBOARDING_COMPLETED_KEY] = completed
        }
    }

    val selectedPomodoroType: Flow<PomodoroType?> = dataStore.data
        .map { preferences ->
            println("map ${preferences[SELECTED_POMODORO_TYPE_KEY]} ${preferences[SELECTED_POMODORO_TYPE_KEY]?.let { PomodoroType.fromOrdinal(it) }}")
            preferences[SELECTED_POMODORO_TYPE_KEY]?.let { PomodoroType.fromOrdinal(it) }
        }

    suspend fun setSelectedPomodoroType(type: PomodoroType) {
        dataStore.edit { preferences ->
            preferences[SELECTED_POMODORO_TYPE_KEY] = type.ordinal
        }
    }

    suspend fun selectPomodoroAndMarkVisited(type: PomodoroType) {
        dataStore.edit { preferences ->
            preferences[SELECTED_POMODORO_TYPE_KEY] = type.ordinal
            println("${type.ordinal} type")
            println("${preferences[SELECTED_POMODORO_TYPE_KEY]}")
            preferences[FIRST_SCHEDULE_VISIT_KEY] = false
        }
    }

    val clickCounter: Flow<Int> = dataStore.data
        .map { preferences -> preferences[CLICK_COUNTER_KEY] ?: 0 }

    suspend fun incrementClickCounter() {
        dataStore.edit { preferences ->
            val currentValue = preferences[CLICK_COUNTER_KEY] ?: 0
            preferences[CLICK_COUNTER_KEY] = currentValue + 1
        }
    }

    suspend fun resetClickCounter() {
        dataStore.edit { preferences ->
            preferences[CLICK_COUNTER_KEY] = 0
        }
    }

    suspend fun resetAllPreferences() {
        dataStore.edit { preferences ->
            preferences[CLICK_COUNTER_KEY] = 0
            preferences[FIRST_SCHEDULE_VISIT_KEY] = true
            preferences.remove(SELECTED_POMODORO_TYPE_KEY)
            preferences.remove(ONBOARDING_COMPLETED_KEY)
        }
    }
}

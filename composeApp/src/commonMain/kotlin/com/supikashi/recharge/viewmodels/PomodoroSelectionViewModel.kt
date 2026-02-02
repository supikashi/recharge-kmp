package com.supikashi.recharge.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.supikashi.recharge.data.UserPreferencesRepository
import com.supikashi.recharge.database.TaskDatabase
import com.supikashi.recharge.models.PomodoroType
import com.supikashi.recharge.utils.calculateBreaksForTask
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class PomodoroSelectionViewModel(
    private val userPreferencesRepository: UserPreferencesRepository,
    database: TaskDatabase,
) : ViewModel() {
    private val dao = database.taskDao()
    fun selectPomodoroType(type: PomodoroType) {
        viewModelScope.launch {
            userPreferencesRepository.selectPomodoroAndMarkVisited(type)
            dao.deleteAllBreaks()
            dao.getAll().first().forEach { task ->
                if (task.isSplittable) {
                    val breaks = calculateBreaksForTask(task, type)
                    if (breaks.isNotEmpty()) {
                        dao.insertBreaks(breaks)
                    }
                }
            }
        }
    }

    fun getPomodoroTypes(): List<PomodoroType> {
        return PomodoroType.entries
    }
}

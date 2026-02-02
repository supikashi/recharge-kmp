package com.supikashi.recharge.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.supikashi.recharge.data.UserPreferencesRepository
import com.supikashi.recharge.database.Task
import com.supikashi.recharge.database.TaskDatabase
import com.supikashi.recharge.database.TaskWithBreaks
import com.supikashi.recharge.models.PomodoroType
import com.supikashi.recharge.utils.calculateBreaksForTask
import com.supikashi.recharge.utils.shouldRecalculateBreaks
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SlotViewModel(
    database: TaskDatabase,
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {
    private val dao = database.taskDao()

    val tasks: StateFlow<List<TaskWithBreaks>> =
        dao.getAllTasksWithBreaks()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val selectedPomodoroType: StateFlow<PomodoroType?> =
        userPreferencesRepository.selectedPomodoroType
            .map {
                println("maaaap ${it}")
                it
            }
            .stateIn(viewModelScope, SharingStarted.Eagerly, null)

    fun upsertTask(task: Task) {
        viewModelScope.launch {
            println(selectedPomodoroType.value)
            val pomodoroType = selectedPomodoroType.value
                ?: PomodoroType.CLASSIC 

            if (task.id == 0) {
                val insertedId = dao.upsert(task)
                
                if (task.isSplittable) {
                    val savedTask = task.copy(id = insertedId.toInt())
                    val breaks = calculateBreaksForTask(savedTask, pomodoroType)
                    if (breaks.isNotEmpty()) {
                        dao.insertBreaks(breaks)
                    }
                }
            } else {
                val oldTask = tasks.value.first { it.task.id == task.id }.task
                dao.upsert(task)
                
                if (shouldRecalculateBreaks(oldTask, task)) {
                    dao.deleteBreaksByTaskId(task.id)

                    if (task.isSplittable) {
                        val breaks = calculateBreaksForTask(task, pomodoroType)
                        if (breaks.isNotEmpty()) {
                            dao.insertBreaks(breaks)
                        }
                    }
                }
            }
        }
    }

    fun deleteTask(task: Task) {
        viewModelScope.launch {
            dao.delete(task)
        }
    }

    fun recalculateAllBreaks() {
        viewModelScope.launch {
            val pomodoroType = userPreferencesRepository.selectedPomodoroType.first()
                ?: PomodoroType.CLASSIC

            val allTasks = dao.getAll().first()

            for (task in allTasks.filter { it.isSplittable }) {
                dao.deleteBreaksByTaskId(task.id)

                val breaks = calculateBreaksForTask(task, pomodoroType)
                if (breaks.isNotEmpty()) {
                    dao.insertBreaks(breaks)
                }
            }
        }
    }
}
package com.supikashi.recharge.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.supikashi.recharge.data.UserPreferencesRepository
import com.supikashi.recharge.database.Break
import com.supikashi.recharge.database.Task
import com.supikashi.recharge.database.TaskDatabase
import com.supikashi.recharge.database.TaskWithBreaks
import com.supikashi.recharge.models.PomodoroType
import com.supikashi.recharge.utils.calculateBreaksForTask
import com.supikashi.recharge.utils.shouldRecalculateBreaks
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

import kotlinx.coroutines.flow.flatMapLatest

import kotlinx.coroutines.ExperimentalCoroutinesApi

class SlotViewModel(
    database: TaskDatabase,
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {
    private val dao = database.taskDao()

    private val f : List<TaskWithBreaks> = listOf(
        TaskWithBreaks(
            Task(
                1
            ),
            listOf(Break(1), Break(2), Break(3), Break(4), Break(5))
        ),
        TaskWithBreaks(
            Task(
                2
            ),
            listOf(Break(1), Break(2), Break(3), Break(4), Break(5))
        ),
        TaskWithBreaks(
            Task(
                3
            ),
            listOf(Break(1), Break(2), Break(3), Break(4), Break(5))
        ),
        TaskWithBreaks(
            Task(
                4
            ),
            listOf(Break(1), Break(2), Break(3), Break(4), Break(5))
        ),
        TaskWithBreaks(
            Task(
                5
            ),
            listOf(Break(1), Break(2), Break(3), Break(4), Break(5))
        ),
        TaskWithBreaks(
            Task(
                6
            ),
            listOf(Break(1), Break(2), Break(3), Break(4), Break(5))
        ),
        TaskWithBreaks(
            Task(
                7
            ),
            listOf(Break(1), Break(2), Break(3), Break(4), Break(5))
        ),
        TaskWithBreaks(
            Task(
                8
            ),
            listOf(Break(1), Break(2), Break(3), Break(4), Break(5))
        ),
    )

    private val s : List<TaskWithBreaks> = listOf(
        TaskWithBreaks(
            Task(
                9
            ),
            listOf(Break(1), Break(2), Break(3), Break(4), Break(5))
        ),
        TaskWithBreaks(
            Task(
                10
            ),
            listOf(Break(1), Break(2), Break(3), Break(4), Break(5))
        ),
        TaskWithBreaks(
            Task(
                11
            ),
            listOf(Break(1), Break(2), Break(3), Break(4), Break(5))
        ),
        TaskWithBreaks(
            Task(
                12
            ),
            listOf(Break(1), Break(2), Break(3), Break(4), Break(5))
        ),
        TaskWithBreaks(
            Task(
                13
            ),
            listOf(Break(1), Break(2), Break(3), Break(4), Break(5))
        ),
        TaskWithBreaks(
            Task(
                14
            ),
            listOf(Break(1), Break(2), Break(3), Break(4), Break(5))
        ),
        TaskWithBreaks(
            Task(
                15
            ),
            listOf(Break(1), Break(2), Break(3), Break(4), Break(5))
        ),
        TaskWithBreaks(
            Task(
                16
            ),
            listOf(Break(1), Break(2), Break(3), Break(4), Break(5))
        ),
        TaskWithBreaks(
            Task(
                17
            ),
            listOf(Break(1), Break(2), Break(3), Break(4), Break(5))
        ),
        TaskWithBreaks(
            Task(
                18
            ),
            listOf(Break(1), Break(2), Break(3), Break(4), Break(5))
        ),
        TaskWithBreaks(
            Task(
                19
            ),
            listOf(Break(1), Break(2), Break(3), Break(4), Break(5))
        ),
        TaskWithBreaks(
            Task(
                20
            ),
            listOf(Break(1), Break(2), Break(3), Break(4), Break(5))
        ),
    )
    @OptIn(ExperimentalTime::class)
    val selectedDate = MutableStateFlow(Clock.System.todayIn(TimeZone.currentSystemDefault()))

    @OptIn(ExperimentalCoroutinesApi::class)
    val filteredTasks: StateFlow<List<TaskWithBreaks>?> = selectedDate
        .flatMapLatest { date -> dao.getTasksWithBreaksByDate(date) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    fun updateSelectedDate(date: LocalDate) {
        selectedDate.value = date
    }

    val selectedPomodoroType: StateFlow<PomodoroType?> =
        userPreferencesRepository.selectedPomodoroType
            .stateIn(viewModelScope, SharingStarted.Eagerly, null)

    @OptIn(ExperimentalTime::class)
    fun upsertTask(task: Task) {
        viewModelScope.launch {
            println(selectedPomodoroType.value)
            val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
            val today = now.date
            val currentTimeMinutes = now.hour * 60 + now.minute
            val startFromTime = if (task.date == today && task.startTime < currentTimeMinutes && task.endTime > currentTimeMinutes) {
                currentTimeMinutes
            } else {
                null
            }
            val pomodoroType = selectedPomodoroType.value
                ?: PomodoroType.CLASSIC

            if (!task.isWork) {
                val allTasksToday = dao.getTasksWithBreaksByDateSync(task.date)
                val overlappingWorkTasks = allTasksToday.filter { 
                    it.task.isWork && 
                    it.task.id != task.id && 
                    it.task.startTime < task.endTime && 
                    it.task.endTime > task.startTime 
                }

                for (overlap in overlappingWorkTasks) {
                    val workTask = overlap.task

                    if (task.startTime <= workTask.startTime && task.endTime >= workTask.endTime) {
                        dao.delete(workTask)
                    } else if (task.startTime > workTask.startTime && task.endTime < workTask.endTime) {
                        val firstPart = workTask.copy(endTime = task.startTime)
                        upsertTask(firstPart)

                        val secondPart = workTask.copy(id = 0, startTime = task.endTime)
                        upsertTask(secondPart)
                    } else if (task.startTime <= workTask.startTime && task.endTime > workTask.startTime) {
                        val updatedWorkTask = workTask.copy(startTime = task.endTime)
                        upsertTask(updatedWorkTask)
                    } else if (task.startTime < workTask.endTime && task.endTime >= workTask.endTime) {
                        val updatedWorkTask = workTask.copy(endTime = task.startTime)
                        upsertTask(updatedWorkTask)
                    }
                }
            }

            if (task.id == 0) {
                val insertedId = dao.upsert(task)
                
                if (task.isSplittable) {
                    val savedTask = task.copy(id = insertedId.toInt())
                    val breaks = calculateBreaksForTask(savedTask, pomodoroType, startFromTime)
                    if (breaks.isNotEmpty()) {
                        dao.insertBreaks(breaks)
                    }
                }
            } else {
                val oldTask = try { dao.getTaskWithBreaks(task.id) } catch (e: Exception) { null }
                if (oldTask?.task == null) {
                    dao.upsert(task)
                    return@launch
                }

                if (shouldRecalculateBreaks(oldTask.task, task)) {
                    if (!task.isSplittable) {
                        val allBreaks = oldTask.breaks
                        dao.updateTaskSchedule(task, allBreaks, emptyList())
                    } else {
                        val existingBreaks = oldTask.breaks
                        val breakDuration = pomodoroType.restMinutes

                        val (validBreaks, invalidBreaks) = existingBreaks.partition { breakItem ->
                            breakItem.date == task.date &&
                                    breakItem.time >= task.startTime &&
                                    (breakItem.time + breakDuration) <= task.endTime &&
                                    (today > breakItem.date || (today == breakItem.date && currentTimeMinutes >= breakItem.time))
                        }

                        val breaksToInsert = if ((today < task.date || (today == task.date && currentTimeMinutes < task.endTime))) {
                            calculateBreaksForTask(task, pomodoroType, startFromTime)
                        } else {
                            emptyList()
                        }

                        dao.updateTaskSchedule(task, invalidBreaks, breaksToInsert)
                    }
                } else {
                    dao.upsert(task)
                }
            }
        }
    }

    fun deleteTask(task: Task) {
        viewModelScope.launch {
            dao.delete(task)
        }
    }
}
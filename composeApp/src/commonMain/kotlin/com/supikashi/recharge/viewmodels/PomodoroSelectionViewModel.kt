package com.supikashi.recharge.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.supikashi.recharge.data.UserPreferencesRepository
import com.supikashi.recharge.database.Break
import com.supikashi.recharge.database.TaskDatabase
import com.supikashi.recharge.models.PomodoroType
import com.supikashi.recharge.utils.calculateBreaksForTask
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.math.max
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

class PomodoroSelectionViewModel(
    private val userPreferencesRepository: UserPreferencesRepository,
    database: TaskDatabase,
) : ViewModel() {
    private val dao = database.taskDao()

    private val _selectionCompleted = Channel<Unit>()
    val selectionCompleted = _selectionCompleted.receiveAsFlow()

    private var job: Job? = null

    @OptIn(ExperimentalTime::class)
    fun selectPomodoroType(type: PomodoroType) {
        if (job?.isActive == true) return
        job = viewModelScope.launch {
            withContext(Dispatchers.Default) {
                userPreferencesRepository.selectPomodoroAndMarkVisited(type)

                val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
                val today = now.date
                val currentTimeMinutes = now.hour * 60 + now.minute

                val breaksToDelete = mutableListOf<Break>()
                val breaksToInsert = mutableListOf<Break>()

                dao.getAllTasksWithBreaks().first()
                    .filter { it.task.date > today || (it.task.date == today && it.task.endTime > currentTimeMinutes) }
                    .forEach { taskWithBreaks ->
                        println(taskWithBreaks.task.name)
                        val task = taskWithBreaks.task
                        if (task.isSplittable) {
                            val allBreaks = taskWithBreaks.breaks
                            val futureBreaks = allBreaks.filter {
                                it.date > today || (it.date == today && it.time > currentTimeMinutes)
                            }

                            // Add future breaks to delete list
                            breaksToDelete.addAll(futureBreaks)

                            // Calculate new breaks starting from the last break's time
                            val startFromTime = if (task.date > today || (task.date == today && task.startTime > currentTimeMinutes)) {
                                null
                            } else {
                                currentTimeMinutes
                            }

                            val newBreaks = calculateBreaksForTask(task, type, startFromTime)

                            if (newBreaks.isNotEmpty()) {
                                breaksToInsert.addAll(newBreaks)
                            }
                        }
                    }

                if (breaksToDelete.isNotEmpty()) {
                    dao.deleteBreaks(breaksToDelete)
                }
                if (breaksToInsert.isNotEmpty()) {
                    dao.insertBreaks(breaksToInsert)
                }
            }
            _selectionCompleted.send(Unit)
        }
    }

    fun getPomodoroTypes(): List<PomodoroType> {
        return PomodoroType.entries
    }
}

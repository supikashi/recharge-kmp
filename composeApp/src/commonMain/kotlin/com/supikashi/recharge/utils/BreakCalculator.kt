package com.supikashi.recharge.utils

import com.supikashi.recharge.database.Break
import com.supikashi.recharge.database.Task
import com.supikashi.recharge.models.PomodoroType

fun calculateBreaksForTask(task: Task, pomodoroType: PomodoroType): List<Break> {
    if (!task.isSplittable) {
        return emptyList()
    }
    
    val taskDuration = task.endTime - task.startTime

    if (taskDuration <= pomodoroType.workMinutes) {
        return emptyList()
    }
    
    val breaks = mutableListOf<Break>()
    var currentTime = task.startTime + pomodoroType.workMinutes

    while (currentTime + pomodoroType.restMinutes <= task.endTime) {
        breaks.add(
            Break(
                taskId = task.id,
                date = task.date,
                time = currentTime,
                isForBreak = true,
                isCompleted = false
            )
        )
        currentTime += pomodoroType.restMinutes + pomodoroType.workMinutes
    }
    
    return breaks
}

fun shouldRecalculateBreaks(oldTask: Task?, newTask: Task): Boolean {
    if (oldTask == null) return newTask.isSplittable
    
    return oldTask.startTime != newTask.startTime ||
           oldTask.endTime != newTask.endTime ||
           oldTask.date != newTask.date ||
           oldTask.isSplittable != newTask.isSplittable
}

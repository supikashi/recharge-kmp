package com.supikashi.recharge.database

import androidx.room.Embedded
import androidx.room.Relation

data class TaskWithBreaks(
    @Embedded val task: Task,
    @Relation(
        parentColumn = "id",
        entityColumn = "taskId"
    )
    val breaks: List<Break>
)
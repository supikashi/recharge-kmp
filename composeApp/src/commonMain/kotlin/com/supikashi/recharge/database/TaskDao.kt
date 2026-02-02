package com.supikashi.recharge.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {

    @Upsert
    suspend fun upsert(person: Task) : Long

    @Delete
    suspend fun delete(person: Task)

    @Query("SELECT * FROM task ORDER BY startTime ASC")
    fun getAll(): Flow<List<Task>>

    @Query("SELECT * FROM Task WHERE id = :taskId")
    suspend fun getTask(taskId: Int): Task?

    @Transaction
    @Query("SELECT * FROM Task WHERE id = :taskId")
    suspend fun getTaskWithBreaks(taskId: Int): TaskWithBreaks

    @Transaction
    @Query("SELECT * FROM Task ORDER BY startTime ASC")
    fun getAllTasksWithBreaks(): Flow<List<TaskWithBreaks>>

    @Insert
    suspend fun insertBreak(breakItem: Break): Long

    @Delete
    suspend fun deleteBreak(breakItem: Break)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBreaks(breaks: List<Break>): List<Long>

    @Query("DELETE FROM Break WHERE taskId = :taskId")
    suspend fun deleteBreaksByTaskId(taskId: Int)

    @Query("DELETE FROM Break")
    suspend fun deleteAllBreaks()

    @Query("SELECT * FROM Break ORDER BY date ASC, time ASC")
    fun getAllBreaksFlow(): Flow<List<Break>>

    @Query("SELECT * FROM Break ORDER BY date ASC, time ASC")
    suspend fun getAllBreaksSorted(): List<Break>

    @Query("""
        SELECT * FROM Break 
        WHERE isNotificationScheduled = 0
        AND (date > :today OR (date = :today AND time > :currentTimeMinutes))
        ORDER BY date ASC, time ASC 
        LIMIT :limit
    """)
    suspend fun getBreaksWithoutNotification(today: String, currentTimeMinutes: Int, limit: Int): List<Break>

    @Query("""
        SELECT COUNT(*) FROM Break 
        WHERE isNotificationScheduled = 1 
        AND (date > :today OR (date = :today AND time > :currentTimeMinutes))
    """)
    suspend fun getScheduledNotificationsCount(today: String, currentTimeMinutes: Int): Int

    @Query("UPDATE Break SET isNotificationScheduled = :isScheduled WHERE id = :breakId")
    suspend fun updateNotificationScheduled(breakId: Int, isScheduled: Boolean)

    @Query("""
        UPDATE Break 
        SET isNotificationScheduled = 0 
        WHERE (date < :today OR (date = :today AND time < :currentTimeMinutes))
    """)
    suspend fun resetPastNotifications(today: String, currentTimeMinutes: Int)

    @Query("UPDATE Break SET isNotificationScheduled = 0 WHERE isNotificationScheduled = 1")
    suspend fun resetAllNotificationFlags()

    @Query("UPDATE Break SET isCompleted = 1, isNotificationScheduled = 0 WHERE id = :breakId")
    suspend fun markBreakCompleted(breakId: Int)

    @Query("SELECT * FROM Break WHERE date = :date ORDER BY time ASC")
    fun getBreaksByDate(date: String): Flow<List<Break>>

    @Transaction
    suspend fun postponeBreak(currentBreak: Break, breakDuration: Int) {
        val taskWithBreaks = getTaskWithBreaks(currentBreak.taskId)
        val task = taskWithBreaks.task

        val breaksToPostpone = taskWithBreaks.breaks.filter { 
            it.date == currentBreak.date && it.time >= currentBreak.time - 6
        }

        breaksToPostpone.forEach { breakItem ->
            deleteBreak(breakItem)
        }

        val newBreaks = breaksToPostpone
            .map { breakItem ->
                breakItem.copy(
                    id = 0, 
                    time = breakItem.time + 5,
                    isNotificationScheduled = false 
                )
            }
            .filter { it.time + breakDuration <= task.endTime } 

        if (newBreaks.isNotEmpty()) {
            insertBreaks(newBreaks)
        }
    }
}
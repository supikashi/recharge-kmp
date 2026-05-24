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

    @Transaction
    @Query("SELECT * FROM Task WHERE date = :date ORDER BY startTime ASC")
    fun getTasksWithBreaksByDate(date: kotlinx.datetime.LocalDate): Flow<List<TaskWithBreaks>>

    @Transaction
    @Query("SELECT * FROM Task WHERE date = :date ORDER BY startTime ASC")
    suspend fun getTasksWithBreaksByDateSync(date: kotlinx.datetime.LocalDate): List<TaskWithBreaks>

    @Insert
    suspend fun insertBreak(breakItem: Break): Long

    @Delete
    suspend fun deleteBreak(breakItem: Break)

    @Delete
    suspend fun deleteBreaks(breaks: List<Break>)

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
        WHERE (date > :today OR (date = :today AND time > :currentTimeMinutes))
        ORDER BY date ASC, time ASC 
        LIMIT :limit
    """)
    suspend fun getNextBreaksWithLimit(today: String, currentTimeMinutes: Int, limit: Int): List<Break>

    @Query("""
        SELECT * FROM Break 
        WHERE (date >= :today)
        ORDER BY date ASC, time ASC
    """)
    fun getNextBreaksFlow(today: String): Flow<List<Break>>

    @Transaction
    @Query("""
        SELECT * FROM Task 
        WHERE (date >= :today)
        ORDER BY date ASC, startTime ASC
    """)
    fun getNextTasksFlow(today: String): Flow<List<TaskWithBreaks>>

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

    @Query("UPDATE Break SET isCompleted = 1, isCancelled = 0, isNotificationScheduled = 0, delaySeconds = :delaySeconds WHERE id = :breakId")
    suspend fun markBreakCompleted(breakId: Int, delaySeconds: Int?)

    @Query("UPDATE Break SET isCompleted = 0, isCancelled = 1, isNotificationScheduled = 0, delaySeconds = NULL WHERE id = :breakId")
    suspend fun markBreakCancelled(breakId: Int)

    @Query("SELECT * FROM Break WHERE date = :date ORDER BY time ASC")
    fun getBreaksByDate(date: String): Flow<List<Break>>

    @Query("SELECT * FROM Break WHERE date = :date ORDER BY time ASC")
    suspend fun getBreaksByDateSync(date: String): List<Break>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertPuzzleDayStatus(dayStatus: PuzzleDayStatus)

    @Query("SELECT * FROM PuzzleDayStatus WHERE date = :date")
    suspend fun getPuzzleDayStatusByDate(date: String): PuzzleDayStatus?

    @Query("SELECT * FROM PuzzleDayStatus ORDER BY date DESC LIMIT 1")
    suspend fun getLatestPuzzleDayStatus(): PuzzleDayStatus?

    @Query("DELETE FROM PuzzleDayStatus WHERE date = :date")
    suspend fun deletePuzzleDayStatus(date: String)

    @Transaction
    suspend fun updateTaskSchedule(
        task: Task,
        breaksToDelete: List<Break>,
        breaksToInsert: List<Break>
    ) {
        upsert(task)
        if (breaksToDelete.isNotEmpty()) {
            deleteBreaks(breaksToDelete)
        }
        if (breaksToInsert.isNotEmpty()) {
            insertBreaks(breaksToInsert)
        }
    }

    @Transaction
    suspend fun postponeBreak(currentBreak: Break, breakDuration: Int) {
        val taskWithBreaks = getTaskWithBreaks(currentBreak.taskId)
        val task = taskWithBreaks.task

        val breaksToPostpone = taskWithBreaks.breaks.filter { 
            it.date == currentBreak.date && it.time >= currentBreak.time
        }

        breaksToPostpone.forEach { breakItem ->
            deleteBreak(breakItem)
        }

        val newBreaks = breaksToPostpone
            .map { breakItem ->
                breakItem.copy(
                    id = 0, 
                    time = breakItem.time + 5,
                    isNotificationScheduled = false,
                    postponeCount = breakItem.postponeCount + if (breakItem.id == currentBreak.id) 1 else 0
                )
            }
            .filter { it.time + breakDuration <= task.endTime } 

        if (newBreaks.isNotEmpty()) {
            insertBreaks(newBreaks)
        }
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMoodRecord(record: MoodRecord)

    @Query("SELECT * FROM MoodRecord WHERE date >= :startDate AND date <= :endDate ORDER BY date ASC, timeMinutes ASC")
    fun getMoodRecordsByDateRange(startDate: kotlinx.datetime.LocalDate, endDate: kotlinx.datetime.LocalDate): Flow<List<MoodRecord>>
}

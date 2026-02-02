package com.supikashi.recharge.database

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.datetime.LocalDate
import kotlin.jvm.JvmStatic

@Database(
    entities = [Task::class, Break::class],
    version = 1
)
@TypeConverters(Converters::class)
@ConstructedBy(TaskDatabaseConstructor::class)
abstract class TaskDatabase: RoomDatabase() {

    abstract fun taskDao(): TaskDao

}

@Suppress("KotlinNoActualForExpect")
expect object TaskDatabaseConstructor : RoomDatabaseConstructor<TaskDatabase> {
    override fun initialize(): TaskDatabase
}

fun getRoomDatabase(
    builder: RoomDatabase.Builder<TaskDatabase>
): TaskDatabase {
    return builder
        .setDriver(BundledSQLiteDriver())
        .setQueryCoroutineContext(Dispatchers.IO)
        .build()
}

object Converters {
    @TypeConverter
    @JvmStatic
    fun fromLocalDate(date: LocalDate?): String? = date?.toString()

    @TypeConverter
    @JvmStatic
    fun toLocalDate(value: String?): LocalDate? = value?.let { LocalDate.parse(it) }
}
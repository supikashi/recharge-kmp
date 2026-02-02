package com.supikashi.recharge

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import com.supikashi.recharge.database.TaskDatabase

fun getDatabaseBuilder(context: Context): RoomDatabase.Builder<TaskDatabase> {
    val appContext = context.applicationContext
    val dbFile = appContext.getDatabasePath("my_room.db")
    return Room.databaseBuilder<TaskDatabase>(
        context = appContext,
        name = dbFile.absolutePath
    )
}
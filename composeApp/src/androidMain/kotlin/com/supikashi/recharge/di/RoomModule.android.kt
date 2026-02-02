package com.supikashi.recharge.di

import com.supikashi.recharge.getDatabaseBuilder
import com.supikashi.recharge.database.getRoomDatabase
import org.koin.dsl.module

actual val roomModule = module {
    single { getRoomDatabase(getDatabaseBuilder(get())) }
}
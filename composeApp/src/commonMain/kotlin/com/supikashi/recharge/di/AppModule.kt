package com.supikashi.recharge.di

import com.supikashi.recharge.data.UserPreferencesRepository
import org.koin.dsl.module

val appModule = module {
    single { "Hello world!123456789" }
    single { UserPreferencesRepository(get()) }
}

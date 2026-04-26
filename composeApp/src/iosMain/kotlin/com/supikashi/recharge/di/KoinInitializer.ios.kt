package com.supikashi.recharge.di

import org.koin.core.context.startKoin
import org.koin.mp.KoinPlatformTools

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual class KoinInitializer {
    actual fun init() {
        if (KoinPlatformTools.defaultContext().getOrNull() != null) return

        startKoin {
            modules(appModule, viewModelModule, dataStoreModule, roomModule, notificationModule)
        }
    }
}
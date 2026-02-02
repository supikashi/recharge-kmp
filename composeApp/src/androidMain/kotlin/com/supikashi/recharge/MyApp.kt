package com.supikashi.recharge

import android.app.Application
import com.supikashi.recharge.di.KoinInitializer

class MyApp: Application() {
    override fun onCreate() {
        super.onCreate()
        KoinInitializer(applicationContext).init()
    }
}
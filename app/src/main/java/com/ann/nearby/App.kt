package com.ann.nearby

import android.app.Application
import com.ann.nearby.di.module.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class App:Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin{
            androidContext(this@App)
            modules(listOf(viewModelModule))
        }
    }
}
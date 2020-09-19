package com.ann.nearby

import android.app.Application
import com.ann.nearby.di.module.networkModule
import com.ann.nearby.di.module.venueRepoModule
import com.ann.nearby.di.module.viewModelModule
import com.mapbox.mapboxsdk.Mapbox
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class App:Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin{
            androidContext(this@App)
            modules(listOf(viewModelModule, networkModule,venueRepoModule))
        }

        Mapbox.getInstance(
            this,
            BuildConfig.MAPBOX_ACCESS_TOKEN
        )
        if (BuildConfig.DEBUG) {
            if (Mapbox.getTelemetry() == null) {
                throw RuntimeException("Mapbox.getTelemetry() == null in debug config")
            } else {
                Mapbox.getTelemetry()!!.setDebugLoggingEnabled(true)
            }
        }
    }
}
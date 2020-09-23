package com.ann.nearby

import android.app.Application
import com.mapbox.mapboxsdk.Mapbox
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class TestApp : Application() {

    @ExperimentalCoroutinesApi
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@TestApp)
            modules(emptyList())
        }

        Mapbox.getInstance(
            this,
            BuildConfig.MAPBOX_ACCESS_TOKEN
        )
    }
}
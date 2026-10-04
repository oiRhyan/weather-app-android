package com.dev.unip.weather

import android.app.Application
import com.dev.unip.weather.data.di.network
import com.dev.unip.weather.data.di.repository
import com.dev.unip.weather.data.di.viewmodel
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class WeatherApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@WeatherApplication)
            modules(network, repository, viewmodel)
        }
    }
}

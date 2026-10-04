package com.dev.unip.weather.data.di

import com.dev.unip.weather.data.repositories.WeatherRepository
import org.koin.dsl.module

val repository = module {
    single { WeatherRepository(get()) }
}

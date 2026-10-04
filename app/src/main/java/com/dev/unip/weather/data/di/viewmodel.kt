package com.dev.unip.weather.data.di

import com.dev.unip.weather.ui.viewmodels.WeatherViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val viewmodel = module {
    viewModel {
        WeatherViewModel(get())
    }
}

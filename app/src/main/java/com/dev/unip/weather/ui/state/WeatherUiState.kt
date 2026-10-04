package com.dev.unip.weather.ui.state

import com.dev.unip.weather.data.models.ApiWeatherResponse

sealed interface WeatherUiState {
    data object Idle : WeatherUiState
    data object Loading : WeatherUiState
    data class Success(val response: ApiWeatherResponse) : WeatherUiState
    data class Error(val message: String) : WeatherUiState
}

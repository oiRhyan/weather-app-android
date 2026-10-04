package com.dev.unip.weather.data.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class WeatherRequest(
    val latitude: Double,
    val longitude: Double,
    @SerialName("forecast_days")
    val forecastDays: Int = 7
)

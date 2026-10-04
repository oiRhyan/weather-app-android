package com.dev.unip.weather.data.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ApiWeatherResponse(
    val data: WeatherReportData? = null,
    val insights: InsightsData? = null,
    val report: TextReportData? = null
)

@Serializable
data class WeatherReportData(
    val location: LocationData? = null,
    val current: CurrentWeather? = null,
    val forecast: ForecastData? = null,
    val risk: RiskInfo? = null,
    val historical: HistoricalData? = null,
    val alerts: List<String> = emptyList(),
    val inmet: InmetData? = null
)

@Serializable
data class LocationData(
    val municipality: String? = null,
    val state: String? = null,
    @SerialName("ibge_code")
    val ibgeCode: Long? = null
)

@Serializable
data class CurrentWeather(
    val temperature: Double? = null,
    val humidity: Double? = null,
    @SerialName("precipitation_mm")
    val precipitationMm: Double? = null,
    @SerialName("wind_speed_kmh")
    val windSpeedKmh: Double? = null,
    @SerialName("wind_gust_kmh")
    val windGustKmh: Double? = null,
    @SerialName("pressure_hpa")
    val pressureHpa: Double? = null
)

@Serializable
data class ForecastData(
    val days: List<ForecastDay> = emptyList(),
    val summary: ForecastSummary? = null
)

@Serializable
data class ForecastDay(
    val date: String? = null,
    @SerialName("temperature_max")
    val temperatureMax: Double? = null,
    @SerialName("temperature_min")
    val temperatureMin: Double? = null,
    @SerialName("precipitation_mm")
    val precipitationMm: Double? = null,
    @SerialName("precipitation_probability")
    val precipitationProbability: Double? = null,
    @SerialName("wind_max_kmh")
    val windMaxKmh: Double? = null
)

@Serializable
data class ForecastSummary(
    @SerialName("max_precipitation_mm")
    val maxPrecipitationMm: Double? = null,
    @SerialName("max_precipitation_probability")
    val maxPrecipitationProbability: Double? = null,
    @SerialName("highest_precipitation_day")
    val highestPrecipitationDay: String? = null,
    @SerialName("temperature_min")
    val temperatureMin: Double? = null,
    @SerialName("temperature_max")
    val temperatureMax: Double? = null,
    @SerialName("wind_max_kmh")
    val windMaxKmh: Double? = null
)

@Serializable
data class RiskInfo(
    val score: Double? = null,
    val level: String? = null,
    val factors: List<String> = emptyList()
)

@Serializable
data class HistoricalData(
    @SerialName("total_events")
    val totalEvents: Int = 0,
    val deaths: Int = 0,
    val injured: Int = 0,
    val displaced: Int = 0,
    val homeless: Int = 0,
    @SerialName("destroyed_houses")
    val destroyedHouses: Int = 0,
    @SerialName("damaged_houses")
    val damagedHouses: Int = 0,
    @SerialName("total_material_damage")
    val totalMaterialDamage: Double = 0.0,
    @SerialName("main_disaster_type")
    val mainDisasterType: String? = null
)

@Serializable
data class InmetData(
    val available: Boolean = false,
    @SerialName("has_data")
    val hasData: Boolean = false,
    val station: InmetStation? = null,
    val period: InmetPeriod? = null
)

@Serializable
data class InmetStation(
    val id: String? = null,
    val name: String? = null,
    @SerialName("distance_km")
    val distanceKm: Double? = null
)

@Serializable
data class InmetPeriod(
    val start: String? = null,
    val end: String? = null
)

@Serializable
data class InsightsData(
    val summary: String? = null,
    val precipitation: String? = null,
    val risk: String? = null,
    val danger: String? = null
)

@Serializable
data class TextReportData(
    val current: String? = null,
    val forecast: String? = null,
    val precipitation: String? = null,
    val historical: String? = null,
    val inmet: String? = null,
    val risk: String? = null,
    val general: String? = null
)

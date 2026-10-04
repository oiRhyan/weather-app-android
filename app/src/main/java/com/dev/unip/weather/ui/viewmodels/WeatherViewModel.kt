package com.dev.unip.weather.ui.viewmodels

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dev.unip.weather.data.models.WeatherRequest
import com.dev.unip.weather.data.repositories.WeatherRepository
import com.dev.unip.weather.ui.state.WeatherUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class WeatherViewModel(
    private val repository: WeatherRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<WeatherUiState>(WeatherUiState.Idle)
    val uiState = _uiState.asStateFlow()

    val latitudeInput = MutableStateFlow("-23.5505")
    val longitudeInput = MutableStateFlow("-46.6333")
    val forecastDaysInput = MutableStateFlow("7")

    fun fetchWeatherReport() {
        val lat = latitudeInput.value.toDoubleOrNull()
        val lon = longitudeInput.value.toDoubleOrNull()
        val days = forecastDaysInput.value.toIntOrNull() ?: 7

        if (lat == null || lon == null) {
            _uiState.value = WeatherUiState.Error("Latitude e Longitude são obrigatórias e devem ser numéricas.")
            return
        }

        viewModelScope.launch {
            _uiState.value = WeatherUiState.Loading
            try {
                val request = WeatherRequest(
                    latitude = lat,
                    longitude = lon,
                    forecastDays = days
                )
                val response = repository.getWeatherReport(request)
                _uiState.value = WeatherUiState.Success(response)
            } catch (e: Exception) {
                Log.e("WeatherViewModel", "Erro ao buscar previsão do tempo", e)
                _uiState.value = WeatherUiState.Error(
                    e.localizedMessage ?: "Não foi possível conectar à API de Previsão do Tempo."
                )
            }
        }
    }

    @SuppressLint("MissingPermission")
    fun requestUserLocation(context: Context, autoSearch: Boolean = false) {
        val fineGranted = ContextCompat.checkSelfPermission(
            context,
            android.Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val coarseGranted = ContextCompat.checkSelfPermission(
            context,
            android.Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (!fineGranted && !coarseGranted) {
            Log.d("WeatherViewModel", "Permissão de localização não concedida.")
            return
        }

        try {
            val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            val providers = locationManager.getProviders(true)
            var bestLocation: Location? = null

            for (provider in providers) {
                val loc = locationManager.getLastKnownLocation(provider) ?: continue
                if (bestLocation == null || loc.accuracy < bestLocation.accuracy) {
                    bestLocation = loc
                }
            }

            if (bestLocation != null) {
                Log.d("WeatherViewModel", "Localização GPS obtida: ${bestLocation.latitude}, ${bestLocation.longitude}")
                latitudeInput.value = "%.4f".format(bestLocation.latitude).replace(",", ".")
                longitudeInput.value = "%.4f".format(bestLocation.longitude).replace(",", ".")
                if (autoSearch) {
                    fetchWeatherReport()
                }
            }
        } catch (e: Exception) {
            Log.e("WeatherViewModel", "Erro ao obter localização do sistema", e)
        }
    }

    fun selectPresetCity(lat: Double, lon: Double) {
        latitudeInput.value = lat.toString()
        longitudeInput.value = lon.toString()
        fetchWeatherReport()
    }

    fun updateLatitude(value: String) {
        latitudeInput.value = value
    }

    fun updateLongitude(value: String) {
        longitudeInput.value = value
    }

    fun updateForecastDays(value: String) {
        forecastDaysInput.value = value
    }
}

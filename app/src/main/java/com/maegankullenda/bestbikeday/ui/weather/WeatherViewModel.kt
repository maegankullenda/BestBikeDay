package com.maegankullenda.bestbikeday.ui.weather

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maegankullenda.bestbikeday.data.WeatherApi
import com.maegankullenda.bestbikeday.data.WeatherForecast
import com.maegankullenda.bestbikeday.data.WeatherService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class WeatherState(
    val forecast: List<WeatherForecast> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val cityName: String = ""
)

class WeatherViewModel(
    private val weatherService: WeatherService = WeatherApi.service
) : ViewModel() {
    private val _uiState = MutableStateFlow(WeatherState())
    val uiState: StateFlow<WeatherState> = _uiState.asStateFlow()

    fun loadWeatherForecast(lat: Double, lon: Double, apiKey: String) {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true)
                val response = weatherService.getForecast(lat, lon, apiKey)

                // Group forecasts by day and get the middle of the day forecast (around noon)
                val dailyForecasts = response.list
                    .groupBy { item ->
                        // Convert timestamp to date string (YYYY-MM-DD)
                        java.time.Instant.ofEpochSecond(item.dt)
                            .atZone(java.time.ZoneId.systemDefault())
                            .toLocalDate()
                            .toString()
                    }
                    .mapValues { (_, forecasts) ->
                        // Find forecast closest to noon for each day
                        forecasts.minByOrNull { forecast ->
                            val hour = java.time.Instant.ofEpochSecond(forecast.dt)
                                .atZone(java.time.ZoneId.systemDefault())
                                .hour
                            Math.abs(hour - 12)
                        } ?: forecasts.first()
                    }
                    .values
                    .take(5) // Take only the first 5 days
                    .map { item ->
                        WeatherForecast(
                            date = item.dt,
                            temperature = item.main.temp,
                            windSpeed = item.wind.speed,
                            weatherMain = item.weather.firstOrNull()?.main ?: "",
                            weatherDescription = item.weather.firstOrNull()?.description ?: ""
                        )
                    }
                    .toList()

                _uiState.value = _uiState.value.copy(
                    forecast = dailyForecasts,
                    isLoading = false,
                    error = null,
                    cityName = response.city.name
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message
                )
            }
        }
    }
}

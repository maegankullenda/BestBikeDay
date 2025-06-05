package com.maegankullenda.bestbikeday.ui.weather

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maegankullenda.bestbikeday.data.ForecastItem
import com.maegankullenda.bestbikeday.data.WeatherApi
import com.maegankullenda.bestbikeday.data.WeatherApiClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class WeatherUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val cityName: String = "",
    val forecasts: List<ForecastItem> = emptyList()
)

class WeatherViewModel(
    private val weatherApi: WeatherApi = WeatherApiClient.weatherApi
) : ViewModel() {
    private val _uiState = MutableStateFlow(WeatherUiState())
    val uiState: StateFlow<WeatherUiState> = _uiState.asStateFlow()

    fun loadWeatherForecast(lat: Double, lon: Double, apiKey: String) {
        _uiState.update { it.copy(isLoading = true, error = null) }

        viewModelScope.launch {
            try {
                val response = weatherApi.getForecast(lat, lon, apiKey)

                // Group forecasts by day and take the middle forecast for each day
                val dailyForecasts = response.list
                    .groupBy { forecast ->
                        // Convert Unix timestamp to day start (midnight)
                        val date = java.util.Date(forecast.date * 1000)
                        val calendar = java.util.Calendar.getInstance()
                        calendar.time = date
                        calendar.set(java.util.Calendar.HOUR_OF_DAY, 0)
                        calendar.set(java.util.Calendar.MINUTE, 0)
                        calendar.set(java.util.Calendar.SECOND, 0)
                        calendar.set(java.util.Calendar.MILLISECOND, 0)
                        calendar.timeInMillis
                    }
                    .map { (_, forecasts) ->
                        // Take the forecast from the middle of the day (around noon)
                        forecasts[forecasts.size / 2]
                    }
                    .take(5) // Ensure we only take 5 days max

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        cityName = response.city.name,
                        forecasts = dailyForecasts,
                        error = null
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Unknown error occurred",
                        cityName = "",
                        forecasts = emptyList()
                    )
                }
            }
        }
    }
}

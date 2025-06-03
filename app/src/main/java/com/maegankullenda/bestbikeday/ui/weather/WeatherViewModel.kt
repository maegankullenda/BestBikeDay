package com.maegankullenda.bestbikeday.ui.weather

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maegankullenda.bestbikeday.data.ForecastItem
import com.maegankullenda.bestbikeday.data.WeatherApi
import com.maegankullenda.bestbikeday.data.WeatherApiClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
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
        _uiState.value = _uiState.value.copy(isLoading = true, error = null)

        viewModelScope.launch {
            try {
                val response = weatherApi.getForecast(lat, lon, apiKey)
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    cityName = response.city.name,
                    forecasts = response.list
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Unknown error occurred"
                )
            }
        }
    }
}

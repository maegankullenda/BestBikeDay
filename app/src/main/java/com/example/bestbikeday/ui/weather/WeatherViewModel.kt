package com.example.bestbikeday.ui.weather

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bestbikeday.data.ForecastItem
import com.example.bestbikeday.data.WeatherApiClient
import java.io.IOException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class WeatherUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val forecasts: List<ForecastItem> = emptyList(),
    val cityName: String = ""
)

class WeatherViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(WeatherUiState())
    val uiState: StateFlow<WeatherUiState> = _uiState.asStateFlow()

    private val weatherApi = WeatherApiClient.create()

    fun loadWeatherForecast(lat: Double, lon: Double, apiKey: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                val response = weatherApi.getWeatherForecast(lat, lon, apiKey = apiKey)
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    forecasts = response.list,
                    cityName = response.city.name
                )
            } catch (e: IOException) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Network error: ${e.message}"
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Error: ${e.message}"
                )
            }
        }
    }
}
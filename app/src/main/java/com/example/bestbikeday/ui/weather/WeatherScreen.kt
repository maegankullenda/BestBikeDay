package com.example.bestbikeday.ui.weather

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.bestbikeday.BuildConfig
import com.example.bestbikeday.data.City
import com.example.bestbikeday.data.ForecastItem
import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack

// Define colors
private val SkyBlue = Color(0xFF87CEEB)
private val LightPink = Color(0xFFFFE4E1) // Misty Rose color

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeatherScreen(
    city: City,
    numberOfDays: Int,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: WeatherViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    
    LaunchedEffect(city) {
        viewModel.loadWeatherForecast(
            lat = city.lat,
            lon = city.lon,
            apiKey = BuildConfig.OPENWEATHER_API_KEY
        )
    }
    
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(LightPink),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top Bar with back button
        TopAppBar(
            title = { Text("Weather Forecast") },
            navigationIcon = {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back"
                    )
                }
            }
        )
        
        Text(
            text = if (uiState.cityName.isNotEmpty()) {
                "Weather Forecast for ${uiState.cityName}"
            } else {
                city.name
            },
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(16.dp)
        )
        
        when {
            uiState.isLoading -> {
                CircularProgressIndicator(
                    modifier = Modifier.padding(16.dp)
                )
            }
            uiState.error != null -> {
                Text(
                    text = uiState.error!!,
                    color = MaterialTheme.colorScheme.error,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(16.dp)
                )
            }
            else -> {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(uiState.forecasts.take(numberOfDays)) { forecast ->
                        WeatherCard(forecast = forecast)
                    }
                }
            }
        }
    }
}

@Composable
fun WeatherCard(
    forecast: ForecastItem,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(
            containerColor = SkyBlue,
            contentColor = Color.Black // Text color
        )
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Date and Weather
            Column(
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = SimpleDateFormat("EEEE, MMM d", Locale.getDefault())
                        .format(Date(forecast.date * 1000)),
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.Black
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = forecast.weather.firstOrNull()?.description?.capitalize() ?: "Unknown",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.DarkGray
                )
            }
            
            // Temperature and Wind
            Column(
                horizontalAlignment = Alignment.End
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "${forecast.main.tempMax.toInt()}°",
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.Black
                    )
                    Text(
                        text = "${forecast.main.tempMin.toInt()}°",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.DarkGray
                    )
                }
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = "Wind: ${forecast.wind.speed.toInt()} km/h",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.DarkGray
                )
            }
        }
    }
}

private fun String.capitalize(): String {
    return this.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
} 
package com.maegankullenda.bestbikeday.ui.weather

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.maegankullenda.bestbikeday.data.City
import com.maegankullenda.bestbikeday.data.ForecastItem
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

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
            apiKey = "7057ebb4ab85723ae3109867380ec71b"
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
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
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
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .testTag("error_message"),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = uiState.error!!,
                        color = MaterialTheme.colorScheme.error,
                        textAlign = TextAlign.Center
                    )
                }
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
                // Use a more specific date format that includes the day of the week
                val dateFormat = SimpleDateFormat("EEEE, MMM d", Locale.getDefault()).apply {
                    timeZone = TimeZone.getDefault()
                }
                Text(
                    text = dateFormat.format(Date(forecast.date * 1000)),
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.height(4.dp))

                val description = forecast.weather.firstOrNull()?.description?.let { desc ->
                    val firstChar = desc.first()
                    val titlecaseChar = if (firstChar.isLowerCase()) {
                        firstChar.titlecase(Locale.getDefault())
                    } else {
                        firstChar.toString()
                    }
                    titlecaseChar + desc.substring(1)
                } ?: "Unknown"

                Text(
                    text = description,
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
    return this.replaceFirstChar {
        if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
    }
}

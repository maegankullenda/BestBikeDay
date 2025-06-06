package com.maegankullenda.bestbikeday.ui.weather

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.maegankullenda.bestbikeday.BuildConfig
import com.maegankullenda.bestbikeday.R
import com.maegankullenda.bestbikeday.data.SouthAfricanCity
import com.maegankullenda.bestbikeday.data.WeatherForecast
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

// Define colors
private val SkyBlue = Color(0xFF87CEEB)
private val LightPink = Color(0xFFFFE4E1) // Misty Rose color

// Define gradient colors
private val GradientStart = Color(0xFF1A237E) // Deep blue
private val GradientEnd = Color(0xFF304FFE) // Bright blue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeatherScreen(
    city: SouthAfricanCity,
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
            apiKey = BuildConfig.WEATHER_API_KEY
        )
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF1A237E), // Deep blue
                        Color(0xFF304FFE) // Bright blue
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TopAppBar(
                title = {
                    Text(
                        "Weather Forecast",
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )

            Text(
                text = if (uiState.cityName.isNotEmpty()) {
                    "Weather Forecast for ${uiState.cityName}"
                } else {
                    city.name
                },
                style = MaterialTheme.typography.headlineMedium,
                color = Color.White,
                modifier = Modifier.padding(16.dp)
            )

            when {
                uiState.isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.padding(16.dp),
                        color = Color.White
                    )
                }
                uiState.error != null -> {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = uiState.error!!,
                            color = Color.White,
                            textAlign = TextAlign.Center
                        )
                    }
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(uiState.forecast) { forecast ->
                            WeatherCard(forecast = forecast)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun WeatherCard(forecast: WeatherForecast) {
    val score = calculateBikingScore(
        temperature = forecast.temperature,
        windSpeed = forecast.windSpeed,
        weatherMain = forecast.weatherMain
    )
    val color = getBikingConditionColor(score)

    val dateFormat = SimpleDateFormat("EEEE, MMM d", Locale.getDefault())
    dateFormat.timeZone = TimeZone.getDefault()

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.9f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = dateFormat.format(Date(forecast.date * 1000)),
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.Black
                )
                Text(
                    text = "${score.toInt()}%",
                    style = MaterialTheme.typography.titleMedium,
                    color = color
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${forecast.temperature.toInt()}°C",
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.Black
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_wind),
                        contentDescription = "Wind speed",
                        tint = Color.Black,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = "${forecast.windSpeed} km/h",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Black
                    )
                }
            }

            Text(
                text = forecast.weatherDescription.replaceFirstChar { it.uppercase() },
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Black,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}
